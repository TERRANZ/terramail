package ru.terra.mail.storage;

import com.beust.jcommander.internal.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.terra.mail.config.StartUpParameters;
import ru.terra.mail.core.domain.MailFolder;
import ru.terra.mail.core.domain.MailFoldersTree;
import ru.terra.mail.core.domain.MailMessage;
import ru.terra.mail.core.domain.MailMessageAttachment;
import ru.terra.mail.gui.core.NotificationManager;
import ru.terra.mail.storage.db.entity.AttachmentEntity;
import ru.terra.mail.storage.db.entity.FolderEntity;
import ru.terra.mail.storage.db.entity.MessageEntity;
import ru.terra.mail.storage.db.repos.AttachmentsRepo;
import ru.terra.mail.storage.db.repos.FoldersRepo;
import ru.terra.mail.storage.db.repos.MessagesRepo;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
@Component
@Scope("singleton")
public class BasicStorageImpl implements AbstractStorage {
    private static final int PAGE_SIZE = 10;

    @Autowired
    private FoldersRepo foldersRepo;
    @Autowired
    private MessagesRepo messagesRepo;
    @Autowired
    private AttachmentsRepo attachmentsRepo;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ObservableList<MailFolder> getAllFoldersTree() {
        final Map<String, MailFolder> foldersMap = new HashMap<>();
        MailFolder inbox = null;
        final Map<String, FolderEntity> storedFoldersMap = new HashMap<>();
        try {
            foldersRepo.findAll().forEach(f -> {
                storedFoldersMap.put(f.getGuid(), f);
                foldersMap.put(f.getFullName(), new MailFolder(f));
            });
        } catch (Exception e) {
            logger.error("Unable to save folder", e);
        }
        for (final FolderEntity fe : storedFoldersMap.values()) {
            if (!Objects.equals(fe.getParentFolderId(), "-1")) {
                final FolderEntity parentEntity = storedFoldersMap.get(fe.getParentFolderId());
                final MailFolder parentFolder = foldersMap.get(parentEntity.getFullName());
                final MailFolder childFolder = foldersMap.get(fe.getFullName());
                parentFolder.getChildFolders().add(childFolder);
            } else
                inbox = foldersMap.get(fe.getFullName());
        }
        if (inbox == null)
            return FXCollections.emptyObservableList();
        return FXCollections.observableArrayList(inbox);
    }

    @Override
    public void storeFolders(final List<MailFolder> mailFolders, final String parentId) {
        if (mailFolders != null) {
            mailFolders.forEach(folder -> {
                try {
                    final FolderEntity folderEntity =
                            Optional
                                    .ofNullable(foldersRepo.findByFullName(folder.getFullName()))
                                    .orElse(new FolderEntity(folder, parentId));
                    folderEntity.setUnreadMessages(folder.getUnreadMessages());
                    foldersRepo.save(folderEntity);
                    folder.setGuid(folderEntity.getGuid());
                    storeFolders(folder.getChildFolders(), folderEntity.getGuid());
                } catch (Exception e) {
                    logger.error("Unable to load folder: " + folder, e);
                }
            });
        }
    }

    @Override
    public ObservableSet<MailMessage> getFolderMessages(final String folderGuid) {
        return FXCollections.observableSet(messagesRepo
                .findByFolderId(folderGuid).stream().map(m -> {
                    final MailMessage ret = new MailMessage(m, folderGuid);
                    final List<AttachmentEntity> attachments = attachmentsRepo.findByMessageId(m.getGuid());
                    if (attachments != null && attachments.size() > 0) {
                        attachments.forEach(a -> ret.getAttachments().add(new MailMessageAttachment(a)));
                    }
                    return ret;
                }).collect(Collectors.toSet()));
    }

    @Override
    public void storeFolderMessageInFolder(final String folderId, final MailMessage m) {
        MessageEntity me = new MessageEntity(m, folderId);
        if (messagesRepo.findByCreateDate(me.getCreateDate()) == null) {
            try {
                messagesRepo.save(me);
                m.getAttachments().parallelStream().map(mma -> new AttachmentEntity(mma, me.getGuid())).forEach(attachmentsRepo::save);
            } catch (Exception e) {
                logger.error("Unable to create new message entity", e);
            }
        }
    }

    @Override
    public Integer countMessagesInFolder(final String folderId) {
        return messagesRepo.countByFolderId(folderId);
    }

    @Override
    public void loadFromFolder(final MailFolder folder) {
        if (folder.getFolder() == null)
            return;
        try {
            if (!folder.getFolder().isOpen())
                folder.getFolder().open(Folder.READ_ONLY);
            int start = 1;
            final int count = folder.getFolder().getMessageCount();
            logger.info("Count: " + count + " in folder " + folder.getFullName());
            while (start < count) {
                final int end = count - start < PAGE_SIZE ? count - start : PAGE_SIZE;
                final Map<Long, Message> messages = new HashMap<>();
                Arrays.stream(folder.getFolder().getMessages(start, end + start)).forEach(m -> {
                    try {
                        messages.put(m.getReceivedDate().getTime(), m);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                });

                messagesRepo.findByDatesInList(messages.keySet()).forEach(m -> messages.remove(m.getCreateDate()));
                NotificationManager.getInstance().notify("Storage", "Loading messages folder: " + folder.getFullName() + " loaded " + (end + start) + " of " + count);
                messages.values().parallelStream().forEach(m -> {
                    final MailMessage msg = new MailMessage(m, folder.getGuid());
                    processMailMessageAttachments(msg);
                    storeFolderMessageInFolder(msg.getFolderGuid(), msg);
                    ArchiveWorker.getInstance().close();
                });

                start += end;
                if (messages.size() > 0) {
                    folder.notifyModified();
                }
            }
            logger.info("Messages in folder " + folder.getFullName() + " : " + messagesRepo.countByFolderId(folder.getGuid()));
        } catch (Exception e) {
            logger.error("Unable to load messages from server", e);
        } finally {
            try {
                folder.getFolder().close(true);
            } catch (MessagingException e) {
                logger.error("Unable to close folder from server", e);
            }
        }
    }

    @Override
    public MailFoldersTree processFolder(final MailFoldersTree parent, final MailFolder mailFolder) {
        final MailFoldersTree ret = new MailFoldersTree(mailFolder);
        parent.getChildrens().add(ret);
        mailFolder.getChildFolders().forEach(cf -> processFolder(ret, cf));
        return ret;
    }

    @Override
    public List<MailFolder> merge(List<MailFolder> storedFolders, final List<MailFolder> serverFolders) {
        if (storedFolders == null || storedFolders.size() == 0) {
            storedFolders = FXCollections.observableArrayList();
            storedFolders.addAll(serverFolders);
        } else {
            final Map<String, MailFolder> serverFoldersMap = new HashMap<>();
            final List<MailFolder> expandedServerfolders = new ArrayList<>();
            final List<MailFolder> expandedStoredfolders = new ArrayList<>();
            serverFolders.forEach(sf -> expandedServerfolders.addAll(expandFoldersTree(sf)));
            storedFolders.forEach(sf -> expandedStoredfolders.addAll(expandFoldersTree(sf)));
            expandedServerfolders.forEach(f -> serverFoldersMap.put(f.getFullName(), f));
            for (final MailFolder storedFolder : expandedStoredfolders) {
                final MailFolder serverFolder = serverFoldersMap.get(storedFolder.getFullName());
                if (serverFolder != null) {
                    storedFolder.setFolder(serverFolder.getFolder());
                    storedFolder.setUnreadMessages(serverFolder.getUnreadMessages());
                } else
                    storedFolder.setDeleted(true);
            }
            mergeFoldersTree(storedFolders, serverFoldersMap);
        }

        storeFolders(storedFolders, "-1");
        return storedFolders;
    }

    @Override
    public void mergeFoldersTree(final List<MailFolder> storedFolders, final Map<String, MailFolder> serverFoldersMap) {
        final List<MailFolder> foldersToAdd = new ArrayList<>();
        for (final MailFolder storedFolder : storedFolders)
            if (!serverFoldersMap.containsKey(storedFolder.getFullName()))
                foldersToAdd.add(serverFoldersMap.get(storedFolder.getFullName()));
            else
                mergeFoldersTree(storedFolder.getChildFolders(), serverFoldersMap);

        storedFolders.addAll(foldersToAdd);
    }

    @Override
    public List<MailFolder> expandFoldersTree(final MailFolder mailFolder) {
        List<MailFolder> folders = Lists.newArrayList(mailFolder);
        mailFolder.getChildFolders().forEach(mf -> folders.addAll(expandFoldersTree(mf)));
        return folders;
    }

    private void processMailMessageAttachments(final MailMessage mm) {
        final Message msg = mm.getMessage();
        try {
            if (msg.getContent() instanceof MimeMultipart) {
                final MimeMultipart multipart = (MimeMultipart) msg.getContent();
                for (int j = 0; j < multipart.getCount(); j++) {
                    final BodyPart bodyPart = multipart.getBodyPart(j);
                    final DataHandler handler = bodyPart.getDataHandler();
                    final String targetFileName = StartUpParameters.getInstance().getAttachments() + File.separator + mm.getFolderGuid() + File.separator + mm.getGuid();
                    mm.getAttachments().add(new MailMessageAttachment(
                            handler.getContentType(),
                            handler.getName(),
                            targetFileName + File.separator + "attachment_" + String.valueOf(j),
                            false)
                    );
//                    ArchiveWorker.getInstance().saveAttachment(targetFileName, targetFileName + File.separator + "attachment_" + String.valueOf(j), handler.getInputStream());
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
