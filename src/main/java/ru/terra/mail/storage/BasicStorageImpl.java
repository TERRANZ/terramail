package ru.terra.mail.storage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.control.TreeItem;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.terra.mail.gui.controller.beans.FoldersTreeItem;
import ru.terra.mail.storage.db.entity.AttachmentEntity;
import ru.terra.mail.storage.db.entity.FolderEntity;
import ru.terra.mail.storage.db.entity.MessageEntity;
import ru.terra.mail.storage.db.repos.AttachmentsRepo;
import ru.terra.mail.storage.db.repos.FoldersRepo;
import ru.terra.mail.storage.db.repos.MessagesRepo;
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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
@Component
@Scope("singleton")
public class BasicStorageImpl implements AbstractStorage {
    @Autowired
    private FoldersRepo foldersRepo;
    @Autowired
    private MessagesRepo messagesRepo;
    @Autowired
    private AttachmentsRepo attachmentsRepo;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ExecutorService service = Executors.newFixedThreadPool(50);

    @Override
    public ObservableList<MailFolder> getRootFolders() throws Exception {
        Map<String, MailFolder> foldersMap = new HashMap<>();
        MailFolder inbox = null;
        Map<String, FolderEntity> storedFoldersMap = new HashMap<>();
        try {
            foldersRepo.findAll().forEach(f -> {
                storedFoldersMap.put(f.getGuid(), f);
                foldersMap.put(f.getFullName(), new MailFolder(f));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (FolderEntity fe : storedFoldersMap.values()) {
            if (!Objects.equals(fe.getParentFolderId(), "-1")) {
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

    @Override
    public void storeFolders(List<MailFolder> mailFolders) {
        mailFolders.forEach(f -> {
            try {
                FolderEntity folderEntity = foldersRepo.findByFullName(f.getFullName());
                if (folderEntity == null) {
                    folderEntity = new FolderEntity(f, "-1");
                } else {
                    folderEntity.setUnreadMessages(f.getUnreadMessages());
                }
                foldersRepo.save(folderEntity);
                storeFolders(f.getChildFolders(), folderEntity.getGuid());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void storeFolders(List<MailFolder> mailFolders, String parentId) {
        mailFolders.forEach(f -> {
            try {
                FolderEntity folderEntity = foldersRepo.findByFullName(f.getFullName());
                if (folderEntity == null) {
                    folderEntity = new FolderEntity(f, parentId);
                } else {
                    folderEntity.setUnreadMessages(f.getUnreadMessages());
                }
                foldersRepo.save(folderEntity);
                storeFolders(f.getChildFolders(), folderEntity.getGuid());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public ObservableSet<MailMessage> getFolderMessages(MailFolder mailFolder) {
        return FXCollections.observableSet(messagesRepo
                .findByFolderId(foldersRepo.findByFullName(mailFolder.getFullName()).getGuid()).stream().map(m -> {
                    MailMessage ret = new MailMessage(m, mailFolder);
                    List<AttachmentEntity> attachments = attachmentsRepo.findByMessageId(m.getGuid());
                    if (attachments != null && attachments.size() > 0) {
                        attachments.forEach(a -> ret.getAttachments().add(new MailMessageAttachment(a)));
                    }
                    return ret;
                }).collect(Collectors.toSet()));
    }

    @Override
    public void storeFolderMessage(MailMessage m) {
        storeFolderMessage(m.getFolder().getGuid(), m);
    }

    @Override
    public void storeFolderMessage(String parentId, MailMessage m) {
        MessageEntity me = new MessageEntity(m, parentId);
        if (messagesRepo.findByCreateDate(me.getCreateDate()) == null) {
            try {
                messagesRepo.save(me);
                logger.info("Created " + me.toString());
                for (MailMessageAttachment mma : m.getAttachments()) {
                    AttachmentEntity ae = new AttachmentEntity(mma, me.getGuid());
                    attachmentsRepo.save(ae);
                }
            } catch (Exception e) {
                logger.error("Unable to create new message entity", e);
            }
        }
    }

    @Override
    public void storeFolderMessages(MailFolder mailFolder, List<MailMessage> messages) {
        String parentId = foldersRepo.findByFullName(mailFolder.getFullName()).getGuid();
        messages.forEach(m -> storeFolderMessage(parentId, m));
    }

    @Override
    public Integer countMessages(MailFolder mailFolder) {
        return messagesRepo.findByFolderId(foldersRepo.findByFullName(mailFolder.getFullName()).getGuid()).size();
    }

    @Override
    public void loadFromFolder(MailFolder folder) {
        if (folder.getFolder() == null)
            return;
        try {
            if (!folder.getFolder().isOpen())
                folder.getFolder().open(Folder.READ_ONLY);
            int start = 1;
            int count = folder.getFolder().getMessageCount();
            logger.info("Count: " + count + " in folder " + folder.getFullName());
            List<Long> loadedDates = new ArrayList<>();
            while (start < count) {
                int end = count - start < 20 ? count - start : 20;
                logger.info("requesting from " + start + " to " + (start + end));
                Arrays.stream(folder.getFolder().getMessages(start, end + start)).forEach(m -> {
                    try {
                        loadedDates.add(m.getReceivedDate().getTime());
                        if (messagesRepo.findByCreateDate(m.getReceivedDate().getTime()) == null) {
                            MailMessage msg = new MailMessage(m, folder);
                            processMailMessageAttachments(msg);
                            storeFolderMessage(msg);
                        } else {
//                            logger.info("Message " + m.getSubject() + " already exists");
                        }
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                });
                start += end;
            }
            folder.getFolder().close(true);
            List<MessageEntity> allMessages = new LinkedList<>();
            allMessages.addAll(messagesRepo.findByFolderId(folder.getGuid()));
            logger.info("Messages in folder " + folder.getFullName() + " : " + allMessages.size());
            allMessages.stream().filter(m -> !loadedDates.contains(m.getCreateDate())).forEach(messagesRepo::delete);
        } catch (Exception e) {
            logger.error("Unable to load messages from server", e);
        }
    }

    @Override
    public TreeItem<FoldersTreeItem> processFolder(TreeItem<FoldersTreeItem> parent, MailFolder mailFolder) {
        TreeItem<FoldersTreeItem> ret = new TreeItem<>(new FoldersTreeItem(mailFolder));
        parent.getChildren().add(ret);
        mailFolder.getChildFolders().forEach(cf -> processFolder(ret, cf));
        return ret;
    }

    @Override
    public ObservableList<MailFolder> merge(ObservableList<MailFolder> storedFolders, ObservableList<MailFolder> serverFolders) {
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

    @Override
    public void mergeFoldersTree(List<MailFolder> storedFolders, Map<String, MailFolder> serverFoldersMap) {
        List<MailFolder> foldersToAdd = new ArrayList<>();
        for (MailFolder storedFolder : storedFolders)
            if (!serverFoldersMap.containsKey(storedFolder.getFullName()))
                foldersToAdd.add(serverFoldersMap.get(storedFolder.getFullName()));
            else
                mergeFoldersTree(storedFolder.getChildFolders(), serverFoldersMap);

        storedFolders.addAll(foldersToAdd);
    }

    @Override
    public List<MailFolder> expandFoldersTree(MailFolder mailFolder) {
        List<MailFolder> folders = new ArrayList<>();
        folders.add(mailFolder);
        if (mailFolder.getChildFolders().size() > 0)
            mailFolder.getChildFolders().forEach(mf -> folders.addAll(expandFoldersTree(mf)));
        return folders;
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
}
