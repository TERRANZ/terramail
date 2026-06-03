package com.terramail.service;

import com.terramail.config.AppConfig;
import com.terramail.mail.ImapHandler;
import com.terramail.mail.MailSessionManager;
import com.terramail.model.Account;
import com.terramail.model.EmailFolder;
import com.terramail.model.EmailMessage;

import javax.mail.MessagingException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for synchronizing email with remote servers.
 */
public class SyncService {
    private static final Logger logger = Logger.getLogger(SyncService.class.getName());

    private final MailSessionManager sessionManager;
    private final ImapHandler imapHandler;
    private final AccountService accountService;
    private final FolderService folderService;
    private final MessageService messageService;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> syncTask;
    private volatile boolean running = false;

    public SyncService() {
        this.sessionManager = new MailSessionManager();
        this.imapHandler = new ImapHandler(sessionManager);
        this.accountService = new AccountService();
        this.folderService = new FolderService();
        this.messageService = new MessageService();
        this.scheduler = Executors.newScheduledThreadPool(2);
    }

    /**
     * Starts the automatic sync scheduler.
     */
    public void startAutoSync() {
        if (running) {
            logger.warning("Auto-sync is already running");
            return;
        }

        int intervalSeconds = AppConfig.getSyncIntervalSeconds();
        logger.info("Starting auto-sync with interval: " + intervalSeconds + " seconds");

        syncTask = scheduler.scheduleAtFixedRate(
                this::performSync,
                0,
                intervalSeconds,
                TimeUnit.SECONDS
        );

        running = true;
    }

    /**
     * Stops the automatic sync scheduler.
     */
    public void stopAutoSync() {
        if (syncTask != null) {
            syncTask.cancel(false);
            running = false;
            logger.info("Auto-sync stopped");
        }
    }

    /**
     * Performs a full synchronization for all accounts.
     */
    public void performSync() {
        try {
            List<Account> accounts = accountService.getAllAccounts();
            logger.info("Starting sync for " + accounts.size() + " accounts");

            for (Account account : accounts) {
                if (account.isImapEnabled()) {
                    syncAccount(account);
                }
            }

            logger.info("Sync completed");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Sync failed", e);
        }
    }

    /**
     * Synchronizes a single account.
     */
    public void syncAccount(Account account) {
        try {
            logger.info("Syncing account: " + account.getEmail());

            // List remote folders
            List<EmailFolder> serverFolders = imapHandler.listFolders(account);

            // Sync folder structure
            folderService.syncFoldersWithServer(account, serverFolders);

            // Get local folders
            List<EmailFolder> localFolders = folderService.getFoldersByAccountId(account.getId());

            // Sync messages for each folder
            for (EmailFolder folder : localFolders) {
                syncFolderMessages(account, folder);
            }

        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "Failed to sync account " + account.getEmail() + ": " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during sync: " + e.getMessage(), e);
        }
    }

    /**
     * Synchronizes messages for a specific folder.
     */
    private void syncFolderMessages(Account account, EmailFolder folder) {
        try {
            List<EmailMessage> messages = imapHandler.syncFolder(account, folder);

            for (EmailMessage message : messages) {
                // Set folder ID
                message.setFolderId(folder.getId());

                // Check if message already exists
                EmailMessage existing = messageService.findMessageByMessageId(message.getMessageId());

                if (existing != null) {
                    // Update existing message
                    existing.setSubject(message.getSubject());
                    existing.setBodyPlain(message.getBodyPlain());
                    existing.setBodyHtml(message.getBodyHtml());
                    existing.setRead(message.isRead());
                    existing.setFlagged(message.isFlagged());
                    existing.setHasAttachments(message.hasAttachments());
                    existing.setAttachmentCount(message.getAttachmentCount());
                    existing.setSizeBytes(message.getSizeBytes());
                    messageService.updateMessage(existing);
                } else {
                    // Create new message
                    messageService.createMessage(message);
                }
            }

            // Update folder message count
            folderService.updateMessageCount(folder.getId(), messages.size());

            // Mark folder as synchronized
            folderService.markFolderSynched(folder.getId(), LocalDateTime.now());

            logger.log(Level.FINE, "Synced {0} messages for folder: {1}",
                    new Object[]{messages.size(), folder.getName()});

        } catch (MessagingException e) {
            logger.log(Level.WARNING, "Failed to sync folder " + folder.getName() + ": " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Database error syncing folder " + folder.getName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Gets the mail session manager.
     */
    public MailSessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * Gets the IMAP handler.
     */
    public ImapHandler getImapHandler() {
        return imapHandler;
    }

    /**
     * Gets the account service.
     */
    public AccountService getAccountService() {
        return accountService;
    }

    /**
     * Gets the folder service.
     */
    public FolderService getFolderService() {
        return folderService;
    }

    /**
     * Gets the message service.
     */
    public MessageService getMessageService() {
        return messageService;
    }

    /**
     * Checks if sync is currently running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Shuts down the sync service.
     */
    public void shutdown() {
        stopAutoSync();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        sessionManager.clearSessions();
    }
}
