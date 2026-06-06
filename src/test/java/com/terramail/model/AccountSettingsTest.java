package com.terramail.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountSettingsTest {

    @Test
    void testCreateSettings() {
        AccountSettings settings = new AccountSettings();
        settings.setAccountName("MyAccount");
        settings.setImapHost("imap.example.com");
        settings.setImapPort(993);
        settings.setImapUser("user@example.com");
        settings.setImapPassword("password");
        settings.setImapSsl(true);
        settings.setSmtpHost("smtp.example.com");
        settings.setSmtpPort(587);
        settings.setSmtpUser("user@example.com");
        settings.setSmtpPassword("password");
        settings.setSmtpSsl(true);
        settings.setDbUrl("jdbc:mysql://localhost:3306/terramail");
        settings.setDbUser("root");
        settings.setDbPassword("dbpassword");

        assertEquals("MyAccount", settings.getAccountName());
        assertEquals("imap.example.com", settings.getImapHost());
        assertEquals(993, settings.getImapPort());
        assertEquals("user@example.com", settings.getImapUser());
        assertEquals("password", settings.getImapPassword());
        assertTrue(settings.isImapSsl());
        assertEquals("smtp.example.com", settings.getSmtpHost());
        assertEquals(587, settings.getSmtpPort());
        assertEquals("user@example.com", settings.getSmtpUser());
        assertEquals("password", settings.getSmtpPassword());
        assertTrue(settings.isSmtpSsl());
        assertEquals("jdbc:mysql://localhost:3306/terramail", settings.getDbUrl());
        assertEquals("root", settings.getDbUser());
        assertEquals("dbpassword", settings.getDbPassword());
    }

    @Test
    void testSetId() {
        AccountSettings settings = new AccountSettings();
        settings.setId(5L);
        assertEquals(5L, settings.getId());
    }

    @Test
    void testEquals() {
        AccountSettings s1 = new AccountSettings();
        s1.setId(1L);
        AccountSettings s2 = new AccountSettings();
        s2.setId(1L);
        AccountSettings s3 = new AccountSettings();
        s3.setId(2L);
        assertEquals(s1, s2);
        assertNotEquals(s1, s3);
    }

    @Test
    void testHashCode() {
        AccountSettings s1 = new AccountSettings();
        s1.setId(1L);
        AccountSettings s2 = new AccountSettings();
        s2.setId(1L);
        assertEquals(s1.hashCode(), s2.hashCode());
    }
}
