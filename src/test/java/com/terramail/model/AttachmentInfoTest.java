package com.terramail.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttachmentInfoTest {

    @Test
    void testCreateAttachmentInfo() {
        AttachmentInfo info = new AttachmentInfo("document.pdf", 1024L, "application/pdf");
        assertEquals("document.pdf", info.getName());
        assertEquals(1024L, info.getSize());
        assertEquals("application/pdf", info.getContentType());
    }

    @Test
    void testDefaultContentType() {
        AttachmentInfo info = new AttachmentInfo("file.bin", 512L, null);
        assertEquals("file.bin", info.getName());
        assertEquals(512L, info.getSize());
        assertEquals("application/octet-stream", info.getContentType());
    }

    @Test
    void testNullNameThrows() {
        assertThrows(NullPointerException.class, () -> new AttachmentInfo(null, 100L, "text/plain"));
    }

    @Test
    void testEquals() {
        AttachmentInfo a1 = new AttachmentInfo("file.txt", 100L, "text/plain");
        AttachmentInfo a2 = new AttachmentInfo("file.txt", 100L, "text/plain");
        AttachmentInfo a3 = new AttachmentInfo("other.txt", 100L, "text/plain");
        assertEquals(a1, a2);
        assertNotEquals(a1, a3);
    }

    @Test
    void testHashCode() {
        AttachmentInfo a1 = new AttachmentInfo("file.txt", 100L, "text/plain");
        AttachmentInfo a2 = new AttachmentInfo("file.txt", 100L, "text/plain");
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void testToString() {
        AttachmentInfo info = new AttachmentInfo("document.pdf", 2048L, "application/pdf");
        String str = info.toString();
        assertTrue(str.contains("document.pdf"));
        assertTrue(str.contains("2048 bytes"));
    }
}
