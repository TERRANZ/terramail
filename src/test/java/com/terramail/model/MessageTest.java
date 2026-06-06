package com.terramail.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    private Message message;

    @BeforeEach
    void setUp() {
        message = new Message(1L, "sender@example.com", "recipient@example.com", "cc@example.com", "Test Subject", Instant.now(), "Test body");
    }

    @Test
    void testCreateMessage() {
        assertNotNull(message);
        assertEquals(1L, message.getFolderId());
        assertEquals("sender@example.com", message.getFrom());
        assertEquals("recipient@example.com", message.getTo());
        assertEquals("cc@example.com", message.getCc());
        assertEquals("Test Subject", message.getSubject());
        assertEquals("Test body", message.getBody());
        assertFalse(message.isSeen());
        assertFalse(message.isFlagged());
        assertFalse(message.hasAttachments());
    }

    @Test
    void testSetAndGetId() {
        message.setId(42L);
        assertEquals(42L, message.getId());
    }

    @Test
    void testSetAndGetFolderId() {
        message.setFolderId(99L);
        assertEquals(99L, message.getFolderId());
    }

    @Test
    void testSetAndGetFrom() {
        message.setFrom("new@example.com");
        assertEquals("new@example.com", message.getFrom());
    }

    @Test
    void testSetAndGetSubject() {
        message.setSubject("New Subject");
        assertEquals("New Subject", message.getSubject());
    }

    @Test
    void testSetAndGetDate() {
        Instant testDate = Instant.ofEpochSecond(1000L);
        message.setDate(testDate);
        assertEquals(testDate, message.getDate());
    }

    @Test
    void testSetAndGetBody() {
        message.setBody("Updated body");
        assertEquals("Updated body", message.getBody());
    }

    @Test
    void testSetSeen() {
        message.setSeen(true);
        assertTrue(message.isSeen());
        message.setSeen(false);
        assertFalse(message.isSeen());
    }

    @Test
    void testSetFlagged() {
        message.setFlagged(true);
        assertTrue(message.isFlagged());
        message.setFlagged(false);
        assertFalse(message.isFlagged());
    }

    @Test
    void testSetAttachments() {
        List<AttachmentInfo> attachments = List.of(
            new AttachmentInfo("file1.txt", 100L, "text/plain"),
            new AttachmentInfo("file2.pdf", 200L, "application/pdf")
        );
        message.setAttachments(attachments);
        assertEquals(2, message.getAttachments().size());
        assertTrue(message.hasAttachments());
    }

    @Test
    void testSetNullAttachments() {
        message.setAttachments(null);
        assertFalse(message.hasAttachments());
        assertEquals(0, message.getAttachments().size());
    }

    @Test
    void testEquals() {
        Message m1 = new Message();
        m1.setId(1L);
        Message m2 = new Message();
        m2.setId(1L);
        Message m3 = new Message();
        m3.setId(2L);
        assertEquals(m1, m2);
        assertNotEquals(m1, m3);
    }

    @Test
    void testHashCode() {
        Message m1 = new Message();
        m1.setId(1L);
        Message m2 = new Message();
        m2.setId(1L);
        assertEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    void testToString() {
        message.setId(1L);
        String str = message.toString();
        assertTrue(str.contains("Message"));
        assertTrue(str.contains("Test Subject"));
        assertTrue(str.contains("sender@example.com"));
    }
}
