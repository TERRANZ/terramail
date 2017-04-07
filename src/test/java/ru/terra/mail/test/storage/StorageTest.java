package ru.terra.mail.test.storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.terra.mail.storage.AbstractStorage;

/**
 * Created by Vadim_Korostelev on 4/7/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class StorageTest {
    @Autowired
    public AbstractStorage storage;

    @Test
    public void getRootFolders() throws Exception {
    }

    @Test
    public void storeFolders() throws Exception {
    }

    @Test
    public void storeFolders1() throws Exception {
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
