package com.terramail.ui.model;

import com.terramail.model.Message;
import com.terramail.model.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageTableModelTest {

    private MessageTableModel tableModel;

    @BeforeEach
    void setUp() {
        tableModel = new MessageTableModel();
    }

    @Test
    void testInitialColumnCount() {
        assertEquals(7, tableModel.getColumnCount());
    }

    @Test
    void testInitialRowCount() {
        assertEquals(0, tableModel.getRowCount());
    }

    @Test
    void testSetMessages() {
        Message m1 = new Message(1L, "from1@test.com", "to@test.com", "", "Subject 1", Instant.now(), "Body 1");
        Message m2 = new Message(1L, "from2@test.com", "to@test.com", "", "Subject 2", Instant.now(), "Body 2");
        tableModel.setMessages(List.of(m1, m2));

        assertEquals(2, tableModel.getRowCount());
    }

    @Test
    void testGetMessageAt() {
        Message m1 = new Message(1L, "from@test.com", "to@test.com", "", "Subject", Instant.now(), "Body");
        tableModel.setMessages(List.of(m1));

        Message retrieved = tableModel.getMessageAt(0);
        assertNotNull(retrieved);
        assertEquals(m1.getId(), retrieved.getId());
    }

    @Test
    void testGetNullMessageAt() {
        assertNull(tableModel.getMessageAt(5));
    }

    @Test
    void testSortByDateDescending() {
        Instant date1 = Instant.ofEpochSecond(100L);
        Instant date2 = Instant.ofEpochSecond(200L);
        Instant date3 = Instant.ofEpochSecond(50L);

        Message m1 = new Message(1L, "s1@test.com", "t@test.com", "", "S1", date1, "B1");
        Message m2 = new Message(1L, "s2@test.com", "t@test.com", "", "S2", date2, "B2");
        Message m3 = new Message(1L, "s3@test.com", "t@test.com", "", "S3", date3, "B3");

        tableModel.setMessages(List.of(m1, m2, m3));

        assertEquals(m2.getId(), tableModel.getMessageAt(0).getId());
        assertEquals(m1.getId(), tableModel.getMessageAt(1).getId());
        assertEquals(m3.getId(), tableModel.getMessageAt(2).getId());
    }

    @Test
    void testSortBySubjectAscending() {
        tableModel.setSortOrder(new SortOrder(SortOrder.Field.SUBJECT, SortOrder.Direction.ASC));

        Message apple = new Message(1L, "s@test.com", "t@test.com", "", "Apple", Instant.now(), "B");
        Message banana = new Message(1L, "s@test.com", "t@test.com", "", "Banana", Instant.now(), "B");
        Message cherry = new Message(1L, "s@test.com", "t@test.com", "", "Cherry", Instant.now(), "B");

        tableModel.setMessages(List.of(banana, cherry, apple));

        assertEquals("Apple", tableModel.getMessageAt(0).getSubject());
        assertEquals("Banana", tableModel.getMessageAt(1).getSubject());
        assertEquals("Cherry", tableModel.getMessageAt(2).getSubject());
    }

    @Test
    void testSortBySubjectDescending() {
        tableModel.setSortOrder(new SortOrder(SortOrder.Field.SUBJECT, SortOrder.Direction.DESC));

        Message apple = new Message(1L, "s@test.com", "t@test.com", "", "Apple", Instant.now(), "B");
        Message banana = new Message(1L, "s@test.com", "t@test.com", "", "Banana", Instant.now(), "B");
        Message cherry = new Message(1L, "s@test.com", "t@test.com", "", "Cherry", Instant.now(), "B");

        tableModel.setMessages(List.of(apple, banana, cherry));

        assertEquals("Cherry", tableModel.getMessageAt(0).getSubject());
        assertEquals("Banana", tableModel.getMessageAt(1).getSubject());
        assertEquals("Apple", tableModel.getMessageAt(2).getSubject());
    }

    @Test
    void testGetColumnName() {
        assertEquals("Subject", tableModel.getColumnName(0));
        assertEquals("From", tableModel.getColumnName(1));
        assertEquals("Date", tableModel.getColumnName(2));
        assertEquals("To", tableModel.getColumnName(3));
        assertEquals("Cc", tableModel.getColumnName(4));
        assertEquals("Seen", tableModel.getColumnName(5));
        assertEquals("Flagged", tableModel.getColumnName(6));
    }

    @Test
    void testGetValueAt() {
        Message m = new Message(1L, "from@test.com", "to@test.com", "cc@test.com", "Test Subject", Instant.ofEpochSecond(1000L), "Test Body");
        m.setSeen(true);
        m.setFlagged(true);
        tableModel.setMessages(List.of(m));

        assertEquals("Test Subject", tableModel.getValueAt(0, 0));
        assertEquals("from@test.com", tableModel.getValueAt(0, 1));
        assertEquals("to@test.com", tableModel.getValueAt(0, 3));
        assertEquals("cc@test.com", tableModel.getValueAt(0, 4));
        assertEquals("Yes", tableModel.getValueAt(0, 5));
        assertEquals("Yes", tableModel.getValueAt(0, 6));
    }

    @Test
    void testNullSubject() {
        Message m = new Message(1L, "from@test.com", "to@test.com", null, null, Instant.now(), "Body");
        tableModel.setMessages(List.of(m));

        assertEquals("", tableModel.getValueAt(0, 0));
        assertEquals("", tableModel.getValueAt(0, 4));
    }

    @Test
    void testAddMessage() {
        Message m = new Message(1L, "from@test.com", "to@test.com", "", "Subject", Instant.now(), "Body");
        tableModel.addMessage(m);
        assertEquals(1, tableModel.getRowCount());
    }

    @Test
    void testRemoveMessage() {
        Message m = new Message(1L, "from@test.com", "to@test.com", "", "Subject", Instant.now(), "Body");
        tableModel.setMessages(List.of(m));
        tableModel.removeMessage(m);
        assertEquals(0, tableModel.getRowCount());
    }

    @Test
    void testIsCellEditable() {
        Message m = new Message(1L, "from@test.com", "to@test.com", "", "Subject", Instant.now(), "Body");
        tableModel.setMessages(List.of(m));
        assertFalse(tableModel.isCellEditable(0, 0));
    }

    @Test
    void testSetNullMessages() {
        tableModel.setMessages(null);
        assertEquals(0, tableModel.getRowCount());
    }

    @Test
    void testFindRowIndex() {
        Message m1 = new Message(1L, "from@test.com", "to@test.com", "", "S1", Instant.now(), "B1");
        Message m2 = new Message(2L, "from@test.com", "to@test.com", "", "S2", Instant.now(), "B2");
        tableModel.setMessages(List.of(m1, m2));

        assertEquals(0, tableModel.findRowIndex(m1));
        assertEquals(1, tableModel.findRowIndex(m2));
    }

    @Test
    void testGetMessageCount() {
        Message m1 = new Message(1L, "from@test.com", "to@test.com", "", "S1", Instant.now(), "B1");
        Message m2 = new Message(2L, "from@test.com", "to@test.com", "", "S2", Instant.now(), "B2");
        Message m3 = new Message(3L, "from@test.com", "to@test.com", "", "S3", Instant.now(), "B3");
        tableModel.setMessages(List.of(m1, m2, m3));

        assertEquals(3, tableModel.getMessageCount());
    }
}
