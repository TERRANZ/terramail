package com.terramail.service;

import com.terramail.model.AccountSettings;
import com.terramail.model.Folder;
import com.terramail.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    @TempDir
    Path tempDir;

    private EmailService emailService;
    private AttachmentService attachmentService;
    private AccountSettings settings;

    @BeforeEach
    void setUp() {
        attachmentService = new AttachmentService(tempDir);
        settings = new AccountSettings();
        settings.setImapHost("localhost");
        settings.setImapPort(143);
        settings.setImapUser("test@test.com");
        settings.setImapPassword("password");
        settings.setSmtpHost("localhost");
        settings.setSmtpPort(25);
        settings.setSmtpUser("test@test.com");
        settings.setSmtpPassword("password");
        emailService = new EmailService(settings, attachmentService);
    }

    @Test
    void testUpdateSettings() {
        AccountSettings newSettings = new AccountSettings();
        newSettings.setImapHost("newhost.com");
        newSettings.setImapPort(993);
        newSettings.setImapUser("newuser");
        newSettings.setImapPassword("newpass");
        newSettings.setSmtpHost("newsmtp.com");
        newSettings.setSmtpPort(587);
        newSettings.setSmtpUser("newuser");
        newSettings.setSmtpPassword("newpass");

        emailService.updateSettings(newSettings);
        assertNotNull(emailService);
    }

    @Test
    void testFetchMessagesNoFolder() {
        settings.setImapHost("nonexistent.invalid.host");
        emailService.updateSettings(settings);

        Folder folder = new Folder(1L, "Inbox", Folder.Type.INBOX);
        assertThrows(RuntimeException.class, () -> emailService.fetchMessages(folder));
    }

    @Test
    void testSendToInvalidHost() {
        settings.setSmtpHost("nonexistent.invalid.host");
        emailService.updateSettings(settings);

        assertThrows(RuntimeException.class,
            () -> emailService.sendMessage("test@test.com", "", "Subject", "Body"));
    }

    @Test
    void testCreateService() {
        AttachmentService as = new AttachmentService(tempDir);
        AccountSettings s = new AccountSettings();
        s.setImapHost("imap.test.com");
        s.setImapPort(993);
        s.setImapUser("user@test.com");
        s.setImapPassword("pass");
        s.setSmtpHost("smtp.test.com");
        s.setSmtpPort(587);
        s.setSmtpUser("user@test.com");
        s.setSmtpPassword("pass");

        EmailService es = new EmailService(s, as);
        assertNotNull(es);
    }
}
