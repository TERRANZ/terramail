package ru.terra.mail.test.storage;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.terra.mail.core.domain.MailFolder;
import ru.terra.mail.core.domain.MailMessage;
import ru.terra.mail.core.domain.MailMessageAttachment;
import ru.terra.mail.storage.AbstractStorage;
import ru.terra.mail.storage.db.entity.FolderEntity;
import ru.terra.mail.storage.db.entity.MessageEntity;
import ru.terra.mail.storage.db.repos.AttachmentsRepo;
import ru.terra.mail.storage.db.repos.FoldersRepo;
import ru.terra.mail.storage.db.repos.MessagesRepo;

import java.util.*;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StorageTest {
    @Autowired
    private FoldersRepo foldersRepo;
    @Autowired
    private MessagesRepo messagesRepo;
    @Autowired
    private AttachmentsRepo attachmentsRepo;
    @Autowired
    public AbstractStorage storage;

    private FolderEntity rootFolder;
    private FolderEntity childFolder1, childFolder2, childFolder3;
    private MessageEntity messageEntity1, messageEntity2, messageEntity3;

    @Before
    public void setUp() {
        rootFolder = new FolderEntity();
        rootFolder.setGuid(UUID.randomUUID());
        rootFolder.setFullName("INBOX.ROOT");
        rootFolder.setName("ROOT");
        rootFolder.setUnreadMessages(0);
        rootFolder.setParentFolderId(UUID.randomUUID());

        childFolder1 = new FolderEntity();
        childFolder1.setGuid(UUID.randomUUID());
        childFolder1.setName("child1");
        childFolder1.setFullName("INBOX.ROOT.child1");

        childFolder2 = new FolderEntity();
        childFolder2.setGuid(UUID.randomUUID());
        childFolder2.setName("child2");
        childFolder2.setFullName("INBOX.ROOT.child2");

        childFolder3 = new FolderEntity();
        childFolder3.setGuid(UUID.randomUUID());
        childFolder3.setName("child3");
        childFolder3.setFullName("INBOX.ROOT.child3");

        rootFolder = foldersRepo.save(rootFolder);

        childFolder1.setParentFolderId(rootFolder.getGuid());
        childFolder2.setParentFolderId(rootFolder.getGuid());

        childFolder1 = foldersRepo.save(childFolder1);
        childFolder2 = foldersRepo.save(childFolder2);

        childFolder3.setParentFolderId(childFolder2.getGuid());
        childFolder3 = foldersRepo.save(childFolder3);

        messageEntity1 = new MessageEntity();
        messageEntity1.setFolderId(rootFolder.getGuid());
        messageEntity1.setFrom("from1");
        messageEntity1.setHeaders("headers1");
        messageEntity1.setMessageBody("messagebody1");
        messageEntity1.setSubject("subj1");
        messageEntity1.setTo("to1");
        messageEntity1.setCreateDate(new Date().getTime());

        messagesRepo.save(messageEntity1);

        messageEntity2 = new MessageEntity();
        messageEntity2.setFolderId(childFolder1.getGuid());
        messageEntity2.setFrom("from2");
        messageEntity2.setHeaders("headers2");
        messageEntity2.setMessageBody("messagebody2");
        messageEntity2.setSubject("subj2");
        messageEntity2.setTo("to2");
        messageEntity2.setCreateDate(new Date().getTime());

        messagesRepo.save(messageEntity2);

        messageEntity3 = new MessageEntity();
        messageEntity3.setFolderId(childFolder2.getGuid());
        messageEntity3.setFrom("from3");
        messageEntity3.setHeaders("headers3");
        messageEntity3.setMessageBody("messagebody3");
        messageEntity3.setSubject("subj3");
        messageEntity3.setTo("to3");
        messageEntity3.setCreateDate(new Date().getTime());

        messagesRepo.save(messageEntity3);
    }

    @After
    public void cleanUp() {
        foldersRepo.deleteAll();
    }

    @Test
    public void getAllFoldersTreeTest() throws Exception {
        List<MailFolder> list = storage.getAllFoldersTree();
        Assert.assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(2, list.get(0).getChildFolders().size());
    }

    @Test
    public void storeFoldersTest() throws Exception {
        MailFolder mf1 = new MailFolder();
        mf1.setName("folder1");
        mf1.setFullName("FOLDER1");
        mf1.setDeleted(false);
        mf1.setGuid(UUID.randomUUID());
        mf1.setUnreadMessages(1);

        storage.storeFolders(Collections.singletonList(mf1), rootFolder.getGuid());

        List<MailFolder> list = storage.getAllFoldersTree();
        assertEquals(3, list.get(0).getChildFolders().size());
    }

    @Test
    public void getFolderMessages() throws Exception {
        Set<MailMessage> messages = storage.getFolderMessages(rootFolder.getGuid());
        assertEquals(1, messages.size());
        messages = storage.getFolderMessages(childFolder1.getGuid());
        assertEquals(1, messages.size());
        messages = storage.getFolderMessages(childFolder2.getGuid());
        assertEquals(1, messages.size());
    }

    @Test
    public void countMessages() throws Exception {
        assertEquals(Integer.valueOf(1), storage.countMessagesInFolder(rootFolder.getGuid()));
        assertEquals(Integer.valueOf(1), storage.countMessagesInFolder(childFolder1.getGuid()));
        assertEquals(Integer.valueOf(1), storage.countMessagesInFolder(childFolder2.getGuid()));
    }

    @Test
    public void storeFolderMessageInFolder() throws Exception {
        MailMessage mm = new MailMessage();
        mm.setCreateDate(new Date());
        mm.setFolderGuid(rootFolder.getGuid());
        mm.setFrom("from");
        mm.setHeaders("headers");
        mm.setMessageBody("body");
        mm.setSubject("subj");
        mm.setTo("to");

        MailMessageAttachment attachment = new MailMessageAttachment();
        attachment.setType("type");
        attachment.setFileName("fn1");

        mm.setAttachments(Collections.singletonList(attachment));

        storage.storeFolderMessageInFolder(rootFolder.getGuid(), mm);
        assertEquals(Integer.valueOf(2), storage.countMessagesInFolder(rootFolder.getGuid()));
    }

    @Test
    public void processFolder() throws Exception {
    }

    @Test
    public void merge() throws Exception {
    }

    @Test
    public void mergeFoldersTree() throws Exception {
    }

    @Test
    public void expandFoldersTree() throws Exception {
    }
}
