package ru.terra.mail.storage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.control.TreeItem;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.terra.mail.gui.controller.beans.FoldersTreeItem;
import ru.terra.mail.storage.db.controllers.AttachmentsController;
import ru.terra.mail.storage.db.controllers.FoldersController;
import ru.terra.mail.storage.db.controllers.MessagesController;
import ru.terra.mail.storage.db.entity.AttachmentEntity;
import ru.terra.mail.storage.db.entity.FolderEntity;
import ru.terra.mail.storage.db.entity.MessageEntity;
import ru.terra.mail.storage.domain.MailFolder;
import ru.terra.mail.storage.domain.MailMessage;
import ru.terra.mail.storage.domain.MailMessageAttachment;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by terranz on 18.10.16.
 */
public class Storage {

    private FoldersController foldersController = new FoldersController();
    private MessagesController messagesController = new MessagesController();
    private AttachmentsController attachmentsController = new AttachmentsController();
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ExecutorService service;

    public Storage() {
        service = Executors.newCachedThreadPool();
    }

    public ObservableList<MailFolder> getRootFolders() throws Exception {
        Map<String, MailFolder> foldersMap = new HashMap<>();
        MailFolder inbox = null;
        Map<Integer, FolderEntity> storedFoldersMap = new HashMap<>();
        foldersController.list(true, -1, -1).forEach(f -> {
            storedFoldersMap.put(f.getId(), f);
            foldersMap.put(f.getFullName(), new MailFolder(f));
        });
        for (FolderEntity fe : storedFoldersMap.values()) {
            if (fe.getParentFolderId() > -1) {
                FolderEntity parentEntity = storedFoldersMap.get(fe.getParentFolderId());
                MailFolder parentFolder = foldersMap.get(parentEntity.getFullName());
                MailFolder childFolder = foldersMap.get(fe.getFullName());
                parentFolder.getChildFolders().add(childFolder);
            } else
                inbox = foldersMap.get(fe.getFullName());
        }
        if (inbox == null)
            return FXCollections.emptyObservableList();
        return FXCollections.observableArrayList(inbox);
    }

    public void storeFolders(List<MailFolder> mailFolders) {
        mailFolders.forEach(f -> {
            try {
                FolderEntity folderEntity = foldersController.findByFullName(f.getFullName());
                if (folderEntity == null) {
                    folderEntity = new FolderEntity(f, -1);
                    foldersController.create(folderEntity);
                } else {
                    folderEntity.setUnreadMessages(f.getUnreadMessages());
                    foldersController.update(folderEntity);
                }
                storeFolders(f.getChildFolders(), folderEntity.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void storeFolders(List<MailFolder> mailFolders, Integer parentId) {
        mailFolders.forEach(f -> {
            try {
                FolderEntity folderEntity = foldersController.findByFullName(f.getFullName());
                if (folderEntity == null) {
                    folderEntity = new FolderEntity(f, parentId);
                    foldersController.create(folderEntity);
                } else {
                    folderEntity.setUnreadMessages(f.getUnreadMessages());
                    foldersController.update(folderEntity);
                }
                storeFolders(f.getChildFolders(), folderEntity.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ObservableSet<MailMessage> getFolderMessages(MailFolder mailFolder) {
        return FXCollections.observableSet(messagesController
                .findByFolderId(foldersController.findByFullName(mailFolder.getFullName()).getId()).stream().map(m -> {
                    MailMessage ret = new MailMessage(m, mailFolder);
                    List<AttachmentEntity> attachments = attachmentsController.findByFolderId(m.getId());
                    if (attachments != null && attachments.size() > 0) {
                        attachments.forEach(a -> ret.getAttachments().add(new MailMessageAttachment(a)));
                    }
                    return ret;
                }).collect(Collectors.toSet()));
    }

    public void storeFolderMessage(MailMessage m) {
        storeFolderMessage(foldersController.findByFullName(m.getFolder().getFullName()).getId(), m);
    }

    public void storeFolderMessage(Integer parentId, MailMessage m) {
        MessageEntity me = new MessageEntity(m, parentId);
        if (!messagesController.isExists(me.getCreateDate())) {
            try {
                messagesController.create(me);
                logger.info("Created " + me.toString());
                for (MailMessageAttachment mma : m.getAttachments()) {
                    AttachmentEntity ae = new AttachmentEntity(mma, me.getId());
                    attachmentsController.create(ae);
                }
            } catch (Exception e) {
                logger.error("Unable to create new message entity", e);
            }
        }
    }

    public void storeFolderMessages(MailFolder mailFolder, List<MailMessage> messages) {
        Integer parentId = foldersController.findByFullName(mailFolder.getFullName()).getId();
        messages.forEach(m -> storeFolderMessage(parentId, m));
    }

    public Integer countMessages(MailFolder mailFolder) {
        return messagesController.findByFolderId(foldersController.findByFullName(mailFolder.getFullName()).getId())
                .size();
    }

    public void loadFromFolder(MailFolder folder) {
        if (folder.getFolder() == null)
            return;
        try {
            if (!folder.getFolder().isOpen())
                folder.getFolder().open(Folder.READ_ONLY);
            Arrays.stream(folder.getFolder().getMessages()).forEach(m -> service.submit(() -> {
                try {
                    if (!messagesController.isExists(m.getReceivedDate())) {
                        MailMessage msg = new MailMessage(m, folder);
                        processMailMessageAttachments(msg);
                        storeFolderMessage(msg);
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }));
        } catch (Exception e) {
            logger.error("Unable to load messages from server", e);
        }
    }

    private void processMailMessageAttachments(MailMessage mm) {
        Message msg = mm.getMessage();
        try {
            if (msg.getContent() instanceof MimeMultipart) {
                MimeMultipart multipart = (MimeMultipart) msg.getContent();
                for (int j = 0; j < multipart.getCount(); j++) {
                    BodyPart bodyPart = multipart.getBodyPart(j);
                    DataHandler handler = bodyPart.getDataHandler();
                    mm.getAttachments().add(new MailMessageAttachment(
                            IOUtils.toByteArray(handler.getInputStream()),
                            handler.getContentType(), handler.getName(), false));
                }
                if (msg.getContentType().startsWith("text/html") || msg.getContentType().startsWith("text/plain")) {
                    mm.setMessageBody(IOUtils.toString(msg.getInputStream(), Charset.forName("UTF-8")));
                }
            } else
                mm.setMessageBody(msg.getContent().toString());
        } catch (Exception e) {
            logger.error("Unable to process mail message", e);
        }
    }
        
    public TreeItem<FoldersTreeItem> processFolder(TreeItem<FoldersTreeItem> parent, MailFolder mailFolder) {
        TreeItem<FoldersTreeItem> ret = new TreeItem<>(new FoldersTreeItem(mailFolder));
        parent.getChildren().add(ret);
        mailFolder.getChildFolders().forEach(cf -> processFolder(ret, cf));
        return ret;
    }

    public ObservableList<MailFolder> merge(ObservableList<MailFolder> storedFolders,
                                               ObservableList<MailFolder> serverFolders) {
        if (storedFolders == null || storedFolders.size() == 0) {
            storedFolders = FXCollections.observableArrayList();
            storedFolders.addAll(serverFolders);
        } else {
            Map<String, MailFolder> serverFoldersMap = new HashMap<>();
            List<MailFolder> expandedServerfolders = new ArrayList<>();
            List<MailFolder> expandedStoredfolders = new ArrayList<>();
            serverFolders.forEach(sf -> expandedServerfolders.addAll(expandFoldersTree(sf)));
            storedFolders.forEach(sf -> expandedStoredfolders.addAll(expandFoldersTree(sf)));
            expandedServerfolders.forEach(f -> serverFoldersMap.put(f.getFullName(), f));
            for (MailFolder storedFolder : expandedStoredfolders) {
                MailFolder serverFolder = serverFoldersMap.get(storedFolder.getFullName());
                if (serverFolder != null) {
                    storedFolder.setFolder(serverFolder.getFolder());
                    storedFolder.setUnreadMessages(serverFolder.getUnreadMessages());
                } else
                    storedFolder.setDeleted(true);
            }
            mergeFoldersTree(storedFolders, serverFoldersMap);
        }

        storeFolders(storedFolders);
        return storedFolders;
    }

    public void mergeFoldersTree(List<MailFolder> storedFolders, Map<String, MailFolder> serverFoldersMap) {
        List<MailFolder> foldersToAdd = new ArrayList<>();
        for (MailFolder storedFolder : storedFolders)
            if (!serverFoldersMap.containsKey(storedFolder.getFullName()))
                foldersToAdd.add(serverFoldersMap.get(storedFolder.getFullName()));
            else
                mergeFoldersTree(storedFolder.getChildFolders(), serverFoldersMap);

        storedFolders.addAll(foldersToAdd);
    }

    public List<MailFolder> expandFoldersTree(MailFolder mailFolder) {
        List<MailFolder> folders = new ArrayList<>();
        folders.add(mailFolder);
        if (mailFolder.getChildFolders().size() > 0)
            mailFolder.getChildFolders().forEach(mf -> folders.addAll(expandFoldersTree(mf)));
        return folders;
    }
}
