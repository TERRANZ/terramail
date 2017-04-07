package ru.terra.mail.test.storage;

import javafx.collections.ObservableList;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.terra.mail.storage.AbstractStorage;
import ru.terra.mail.storage.db.entity.FolderEntity;
import ru.terra.mail.storage.db.repos.AttachmentsRepo;
import ru.terra.mail.storage.db.repos.FoldersRepo;
import ru.terra.mail.storage.db.repos.MessagesRepo;
import ru.terra.mail.storage.domain.MailFolder;

import java.util.UUID;

/**
 * Created by Vadim_Korostelev on 4/7/2017.
 */
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

    public StorageTest() {
        rootFolder = new FolderEntity();
        rootFolder.setGuid(UUID.randomUUID().toString());
        rootFolder.setFullName("INBOX.ROOT");
        rootFolder.setName("ROOT");
        rootFolder.setUnreadMessages(0);
        rootFolder.setParentFolderId("-1");

        childFolder1 = new FolderEntity();
        childFolder1.setGuid(UUID.randomUUID().toString());
        childFolder1.setName("child1");
        childFolder1.setFullName("INBOX.ROOT.child1");

        childFolder2 = new FolderEntity();
        childFolder2.setGuid(UUID.randomUUID().toString());
        childFolder2.setName("child2");
        childFolder2.setFullName("INBOX.ROOT.child2");

        childFolder3 = new FolderEntity();
        childFolder3.setGuid(UUID.randomUUID().toString());
        childFolder3.setName("child3");
        childFolder3.setFullName("INBOX.ROOT.child3");
    }

    @Before
    public void setUp() {
        rootFolder = foldersRepo.save(rootFolder);

        childFolder1.setParentFolderId(rootFolder.getGuid());
        childFolder2.setParentFolderId(rootFolder.getGuid());

        childFolder1 = foldersRepo.save(childFolder1);
        childFolder2 = foldersRepo.save(childFolder2);

        childFolder3.setParentFolderId(childFolder2.getGuid());
        childFolder3 = foldersRepo.save(childFolder3);
    }

    @After
    public void cleanUp() {
        foldersRepo.deleteAll();
    }

    @Test
    public void getAllFoldersTreeTest() throws Exception {
        ObservableList<MailFolder> list = storage.getAllFoldersTree();
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() == 1);
        Assert.assertTrue(list.get(0).getChildFolders().size() == 2);
    }

    @Test
    public void storeFoldersTest() throws Exception {

    }

    @Test
    public void storeFoldersTreeTest() throws Exception {
    }

    @Test
    public void getFolderMessages() throws Exception {
    }

    @Test
    public void storeFolderMessage() throws Exception {
    }

    @Test
    public void storeFolderMessage1() throws Exception {
    }

    @Test
    public void storeFolderMessages() throws Exception {
    }

    @Test
    public void countMessages() throws Exception {
    }

    @Test
    public void loadFromFolder() throws Exception {
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
