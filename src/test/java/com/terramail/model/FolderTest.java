package com.terramail.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FolderTest {

    @Test
    void testCreateFolder() {
        Folder folder = new Folder(1L, "Inbox", Folder.Type.INBOX);
        assertEquals(1L, folder.getAccountId());
        assertEquals("Inbox", folder.getName());
        assertEquals(Folder.Type.INBOX, folder.getType());
    }

    @Test
    void testSetName() {
        Folder folder = new Folder(1L, "Inbox", Folder.Type.INBOX);
        folder.setName("Sent");
        assertEquals("Sent", folder.getName());
    }

    @Test
    void testSetType() {
        Folder folder = new Folder(1L, "Inbox", Folder.Type.INBOX);
        folder.setType(Folder.Type.SENT);
        assertEquals(Folder.Type.SENT, folder.getType());
    }

    @Test
    void testSetId() {
        Folder folder = new Folder(1L, "Inbox", Folder.Type.INBOX);
        folder.setId(42L);
        assertEquals(42L, folder.getId());
    }

    @Test
    void testEquals() {
        Folder f1 = new Folder();
        f1.setId(1L);
        Folder f2 = new Folder();
        f2.setId(1L);
        Folder f3 = new Folder();
        f3.setId(2L);
        assertEquals(f1, f2);
        assertNotEquals(f1, f3);
    }

    @Test
    void testHashCode() {
        Folder f1 = new Folder();
        f1.setId(1L);
        Folder f2 = new Folder();
        f2.setId(1L);
        assertEquals(f1.hashCode(), f2.hashCode());
    }

    @Test
    void testNullNameThrows() {
        assertThrows(NullPointerException.class, () -> new Folder(1L, null, Folder.Type.INBOX));
    }

    @Test
    void testNullTypeThrows() {
        assertThrows(NullPointerException.class, () -> new Folder(1L, "Inbox", null));
    }

    @Test
    void testTypeValues() {
        assertAll("Folder types",
            () -> assertEquals("INBOX", Folder.Type.INBOX.name()),
            () -> assertEquals("SENT", Folder.Type.SENT.name()),
            () -> assertEquals("DRAFTS", Folder.Type.DRAFTS.name()),
            () -> assertEquals("TRASH", Folder.Type.TRASH.name()),
            () -> assertEquals("CUSTOM", Folder.Type.CUSTOM.name())
        );
    }
}
