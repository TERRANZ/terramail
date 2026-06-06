package com.terramail.repository;

import com.terramail.model.Folder;
import com.terramail.model.Message;
import com.terramail.model.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageRepositoryImplTest {

    private MessageRepositoryImpl repository;
    private Folder testFolder;

    @BeforeEach
    void setUp() {
        repository = new MessageRepositoryImpl(TestDatabaseUtil.getDataSource());
        TestDatabaseUtil.resetDatabase();

        testFolder = new Folder(1L, "TestFolder", Folder.Type.INBOX);
        testFolder = new FolderRepositoryImpl(TestDatabaseUtil.getDataSource()).save(testFolder);
    }

    @Test
    void testSaveMessage() {
        Message message = new Message(testFolder.getId(), "sender@test.com", "recipient@test.com", null, "Test Subject", Instant.now(), "Test body");
        Message saved = repository.save(message);

        assertNotNull(saved);
        assertTrue(saved.getId() > 0);
        assertEquals("Test Subject", saved.getSubject());
        assertEquals("Test body", saved.getBody());
    }

    @Test
    void testFindById() {
        Message message = new Message(testFolder.getId(), "sender@test.com", "recipient@test.com", null, "Test Subject", Instant.now(), "Test body");
        Message saved = repository.save(message);

        Message found = repository.findById(saved.getId());
        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("Test Subject", found.getSubject());
    }

    @Test
    void testFindByFolderId() {
        Message m1 = new Message(testFolder.getId(), "sender1@test.com", "r@test.com", null, "Subject 1", Instant.now(), "Body 1");
        Message m2 = new Message(testFolder.getId(), "sender2@test.com", "r@test.com", null, "Subject 2", Instant.now(), "Body 2");
        repository.save(m1);
        repository.save(m2);

        SortOrder sort = new SortOrder(SortOrder.Field.DATE, SortOrder.Direction.DESCENDING);
        List<Message> messages = repository.findByFolderId(testFolder.getId(), sort);
        assertEquals(2, messages.size());
    }

    @Test
    void testUpdateSeen() {
        Message message = new Message(testFolder.getId(), "sender@test.com", "r@test.com", null, "Subject", Instant.now(), "Body");
        Message saved = repository.save(message);
        assertFalse(saved.isSeen());

        repository.updateSeen(saved.getId(), true);
        Message found = repository.findById(saved.getId());
        assertTrue(found.isSeen());
    }

    @Test
    void testUpdateFlagged() {
        Message message = new Message(testFolder.getId(), "sender@test.com", "r@test.com", null, "Subject", Instant.now(), "Body");
        Message saved = repository.save(message);
        assertFalse(saved.isFlagged());

        repository.updateFlagged(saved.getId(), true);
        Message found = repository.findById(saved.getId());
        assertTrue(found.isFlagged());
    }

    @Test
    void testDeleteById() {
        Message message = new Message(testFolder.getId(), "sender@test.com", "r@test.com", null, "Subject", Instant.now(), "Body");
        Message saved = repository.save(message);

        repository.deleteById(saved.getId());
        assertNull(repository.findById(saved.getId()));
    }

    @Test
    void testCountByFolderId() {
        repository.save(new Message(testFolder.getId(), "s1@test.com", "r@test.com", null, "S1", Instant.now(), "B1"));
        repository.save(new Message(testFolder.getId(), "s2@test.com", "r@test.com", null, "S2", Instant.now(), "B2"));

        assertEquals(2, repository.countByFolderId(testFolder.getId()));
    }

    @Test
    void testSearchBySubject() {
        repository.save(new Message(testFolder.getId(), "s@test.com", "r@test.com", null, "Important Meeting", Instant.now(), "Body"));
        repository.save(new Message(testFolder.getId(), "s@test.com", "r@test.com", null, "Regular Email", Instant.now(), "Body"));

        SortOrder sort = new SortOrder(SortOrder.Field.DATE, SortOrder.Direction.DESCENDING);
        List<Message> results = repository.searchBySubject(testFolder.getId(), "Meeting", sort);
        assertEquals(1, results.size());
        assertEquals("Important Meeting", results.get(0).getSubject());
    }

    @Test
    void testSortBySubject() {
        repository.save(new Message(testFolder.getId(), "s1@test.com", "r@test.com", null, "Zebra", Instant.now(), "Body"));
        repository.save(new Message(testFolder.getId(), "s2@test.com", "r@test.com", null, "Apple", Instant.now(), "Body"));

        SortOrder ascSort = new SortOrder(SortOrder.Field.SUBJECT, SortOrder.Direction.ASCENDING);
        List<Message> messages = repository.findByFolderId(testFolder.getId(), ascSort);
        assertEquals("Apple", messages.get(0).getSubject());
    }
}
