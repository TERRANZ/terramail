package com.terramail.repository;

import com.terramail.model.Folder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FolderRepositoryImplTest {

    private FolderRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new FolderRepositoryImpl(TestDatabaseUtil.getDataSource());
        TestDatabaseUtil.resetDatabase();
    }

    @Test
    void testSaveFolder() {
        Folder folder = new Folder(1L, "Inbox", Folder.Type.INBOX);
        Folder saved = repository.save(folder);

        assertNotNull(saved);
        assertTrue(saved.getId() > 0);
        assertEquals("Inbox", saved.getName());
        assertEquals(Folder.Type.INBOX, saved.getType());
    }

    @Test
    void testFindById() {
        Folder folder = new Folder(1L, "Inbox", Folder.Type.INBOX);
        Folder saved = repository.save(folder);

        Folder found = repository.findById(saved.getId());
        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("Inbox", found.getName());
    }

    @Test
    void testFindByAccountId() {
        repository.save(new Folder(1L, "Inbox", Folder.Type.INBOX));
        repository.save(new Folder(1L, "Sent", Folder.Type.SENT));
        repository.save(new Folder(2L, "Inbox2", Folder.Type.INBOX));

        List<Folder> folders = repository.findByAccountId(1L);
        assertEquals(2, folders.size());
    }

    @Test
    void testFindByName() {
        repository.save(new Folder(1L, "Inbox", Folder.Type.INBOX));

        Folder found = repository.findByName(1L, "Inbox");
        assertNotNull(found);
        assertEquals("Inbox", found.getName());

        assertNull(repository.findByName(1L, "NonExistent"));
    }

    @Test
    void testDeleteById() {
        Folder folder = new Folder(1L, "Inbox", Folder.Type.INBOX);
        Folder saved = repository.save(folder);

        repository.deleteById(saved.getId());
        assertNull(repository.findById(saved.getId()));
    }

    @Test
    void testCountByAccountId() {
        repository.save(new Folder(1L, "Inbox", Folder.Type.INBOX));
        repository.save(new Folder(1L, "Sent", Folder.Type.SENT));

        assertEquals(2, repository.countByAccountId(1L));
    }

    @Test
    void testUpdateFolder() {
        Folder folder = new Folder(1L, "Inbox", Folder.Type.INBOX);
        Folder saved = repository.save(folder);
        saved.setName("Drafts");
        saved.setType(Folder.Type.DRAFTS);
        repository.save(saved);

        Folder found = repository.findById(saved.getId());
        assertEquals("Drafts", found.getName());
        assertEquals(Folder.Type.DRAFTS, found.getType());
    }
}
