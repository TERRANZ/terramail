package ru.terra.mail.storage;

import com.beust.jcommander.internal.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
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

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static ru.terra.mail.config.StartUpParameters.getInstance;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
@Component
@Scope("singleton")
@Slf4j
@RequiredArgsConstructor
public class BasicStorageImpl implements AbstractStorage {
    private static final int PAGE_SIZE = 10;

    private final FoldersRepo foldersRepo;
    private final MessagesRepo messagesRepo;
    private final AttachmentsRepo attachmentsRepo;
    private final ArchiveManager archiveManager;

    @Override
    public ObservableList<MailFolder> getAllFoldersTree() {
        val foldersMap = new HashMap<String, MailFolder>();
        MailFolder inbox = null;
        val storedFoldersMap = new HashMap<UUID, FolderEntity>();
        try {
            foldersRepo.findAll().forEach(f -> {
                storedFoldersMap.put(f.getGuid(), f);
                foldersMap.put(f.getFullName(), new MailFolder(f));
            });
        } catch (Exception e) {
            log.error("Unable to save folder", e);
        }
        for (val fe : storedFoldersMap.values()) {
            if (fe.getParentFolderId() != null) {
                val parentEntity = storedFoldersMap.get(fe.getParentFolderId());
                val parentFolder = foldersMap.get(parentEntity.getFullName());
                val childFolder = foldersMap.get(fe.getFullName());
                parentFolder.getChildFolders().add(childFolder);
            } else
                inbox = foldersMap.get(fe.getFullName());
        }
        if (inbox == null) return FXCollections.emptyObservableList();
        return FXCollections.observableArrayList(inbox);
    }

    @Override
    public void storeFolders(final List<MailFolder> mailFolders, final UUID parentId) {
        if (mailFolders != null) {
            mailFolders.forEach(folder -> {
                try {
                    val folderEntity = Optional.ofNullable(foldersRepo.findByFullName(folder.getFullName())).orElse(new FolderEntity(folder, parentId));
                    folderEntity.setUnreadMessages(folder.getUnreadMessages());
                    foldersRepo.save(folderEntity);
                    folder.setGuid(folderEntity.getGuid());
                    storeFolders(folder.getChildFolders(), folderEntity.getGuid());
                } catch (Exception e) {
                    log.error("Unable to load folder: {}", folder, e);
                }
            });
        }
    }

    @Override
    public ObservableSet<MailMessage> getFolderMessages(final UUID folderGuid) {
        return FXCollections.observableSet(messagesRepo.findByFolderId(folderGuid).stream().map(m -> {
            val ret = new MailMessage(m, folderGuid);
            val attachments = attachmentsRepo.findByMessageId(m.getGuid());
            if (attachments != null && !attachments.isEmpty()) {
                attachments.forEach(a -> ret.getAttachments().add(new MailMessageAttachment(a)));
            }
            return ret;
        }).collect(Collectors.toSet()));
    }

    @Override
    public void storeFolderMessageInFolder(final UUID folderId, final MailMessage m) {
        val me = new MessageEntity(m, folderId);
        if (messagesRepo.findByCreateDate(me.getCreateDate()) == null) {
            try {
                messagesRepo.save(me);
                m.getAttachments().parallelStream().map(mma -> new AttachmentEntity(mma, me.getGuid())).forEach(attachmentsRepo::save);
            } catch (Exception e) {
                log.error("Unable to create new message entity", e);
            }
        }
    }

    @Override
    public Integer countMessagesInFolder(final UUID folderId) {
        return messagesRepo.countByFolderId(folderId);
    }

    @Override
    public void loadFromFolder(final MailFolder folder) {
        if (folder.getFolder() == null) return;
        try {
            if (!folder.getFolder().isOpen()) folder.getFolder().open(Folder.READ_ONLY);
            int start = 1;
            val count = folder.getFolder().getMessageCount();
            log.info("Count: " + count + " in folder " + folder.getFullName());
            while (start < count) {
                val end = Math.min(count - start, PAGE_SIZE);
                val messages = new HashMap<Long, Message>();
                Arrays.stream(folder.getFolder().getMessages(start, end + start)).forEach(m -> {
                    try {
                        messages.put(m.getReceivedDate().getTime(), m);
                    } catch (MessagingException e) {
                        log.error("Unable to process message", e);
                    }
                });

                messagesRepo.findByDatesInList(messages.keySet()).forEach(m -> messages.remove(m.getCreateDate()));
                NotificationManager.getInstance().notify("Storage", "Loading messages folder: " + folder.getFullName() + " loaded " + (end + start) + " of " + count);
                messages.values().parallelStream().forEach(m -> {
                    val msg = new MailMessage(m, folder.getGuid());
                    processMailMessageAttachments(msg);
                    storeFolderMessageInFolder(msg.getFolderGuid(), msg);
                    archiveManager.close();
                });

                start += end;
                if (!messages.isEmpty()) {
                    folder.notifyModified();
                }
            }
            log.info("Messages in folder {} : {}", folder.getFullName(), messagesRepo.countByFolderId(folder.getGuid()));
        } catch (Exception e) {
            log.error("Unable to load messages from server", e);
        } finally {
            try {
                folder.getFolder().close(true);
            } catch (MessagingException e) {
                log.error("Unable to close folder from server", e);
            }
        }
    }

    @Override
    public MailFoldersTree processFolder(final MailFoldersTree parent, final MailFolder mailFolder) {
        val ret = new MailFoldersTree(mailFolder);
        parent.getChildrens().add(ret);
        mailFolder.getChildFolders().forEach(cf -> processFolder(ret, cf));
        return ret;
    }

    @Override
    public List<MailFolder> merge(List<MailFolder> storedFolders, final List<MailFolder> serverFolders) {
        if (storedFolders == null || storedFolders.isEmpty()) {
            storedFolders = FXCollections.observableArrayList();
            storedFolders.addAll(serverFolders);
        } else {
            val serverFoldersMap = new HashMap<String, MailFolder>();
            val expandedServerfolders = new ArrayList<MailFolder>();
            val expandedStoredfolders = new ArrayList<MailFolder>();

            serverFolders.forEach(sf -> expandedServerfolders.addAll(expandFoldersTree(sf)));
            storedFolders.forEach(sf -> expandedStoredfolders.addAll(expandFoldersTree(sf)));
            expandedServerfolders.forEach(f -> serverFoldersMap.put(f.getFullName(), f));
            for (val storedFolder : expandedStoredfolders) {
                val serverFolder = serverFoldersMap.get(storedFolder.getFullName());
                if (serverFolder != null) {
                    storedFolder.setFolder(serverFolder.getFolder());
                    storedFolder.setUnreadMessages(serverFolder.getUnreadMessages());
                } else storedFolder.setDeleted(true);
            }
            mergeFoldersTree(storedFolders, serverFoldersMap);
        }

        storeFolders(storedFolders, null);
        return storedFolders;
    }

    @Override
    public void mergeFoldersTree(final List<MailFolder> storedFolders, final Map<String, MailFolder> serverFoldersMap) {
        val foldersToAdd = new ArrayList<MailFolder>();
        for (val storedFolder : storedFolders)
            if (!serverFoldersMap.containsKey(storedFolder.getFullName()))
                foldersToAdd.add(serverFoldersMap.get(storedFolder.getFullName()));
            else
                mergeFoldersTree(storedFolder.getChildFolders(), serverFoldersMap);
        storedFolders.addAll(foldersToAdd);
    }

    @Override
    public List<MailFolder> expandFoldersTree(final MailFolder mailFolder) {
        val folders = Lists.newArrayList(mailFolder);
        mailFolder.getChildFolders().forEach(mf -> folders.addAll(expandFoldersTree(mf)));
        return folders;
    }

    private void processMailMessageAttachments(final MailMessage mm) {
        final Message msg = mm.getMessage();
        try {
            if (msg.getContent() instanceof MimeMultipart multipart) {
                for (int j = 0; j < multipart.getCount(); j++) {
                    val bodyPart = multipart.getBodyPart(j);
                    val handler = bodyPart.getDataHandler();
                    val targetFileName = getInstance().getAttachments() + File.separator + msg.getFolder().getFullName() + File.separator + mm.getGuid();
                    mm.getAttachments().add(new MailMessageAttachment(handler.getContentType(), handler.getName(), true, targetFileName + File.separator + "attachment_" + j));
                    archiveManager.saveAttachment(targetFileName, targetFileName + File.separator + "attachment_" + j, handler.getInputStream());
                }
                if (msg.getContentType().startsWith("text/html") || msg.getContentType().startsWith("text/plain")) {
                    mm.setMessageBody(IOUtils.toString(msg.getInputStream(), StandardCharsets.UTF_8));
                }
            } else mm.setMessageBody(msg.getContent().toString());
        } catch (Exception e) {
            log.error("Unable to process mail message", e);
        }
    }
}
