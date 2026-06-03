package com.terramail.service;

import com.terramail.model.Account;
import com.terramail.model.EmailFolder;
import com.terramail.repository.FolderRepository;
import com.terramail.repository.MessageRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for managing email folders.
 */
public class FolderService {
    private static final Logger logger = Logger.getLogger(FolderService.class.getName());
    private final FolderRepository folderRepository;
    private final MessageRepository messageRepository;

    public FolderService() {
        this.folderRepository = new FolderRepository();
        this.messageRepository = new MessageRepository();
    }

    /**
     * Creates a new folder for an account.
     */
    public EmailFolder createFolder(long accountId, String name, String path) throws SQLException {
        EmailFolder folder = new EmailFolder(accountId, name, path);
        return folderRepository.create(folder);
    }

    /**
     * Updates an existing folder.
     */
    public void updateFolder(EmailFolder folder) throws SQLException {
        folderRepository.update(folder);
    }

    /**
     * Deletes a folder.
     */
    public void deleteFolder(long folderId) throws SQLException {
        folderRepository.delete(folderId);
    }

    /**
     * Finds a folder by ID.
     */
    public EmailFolder findFolderById(long folderId) throws SQLException {
        return folderRepository.findById(folderId);
    }

    /**
     * Gets all folders for an account.
     */
    public List<EmailFolder> getFoldersByAccountId(long accountId) throws SQLException {
        return folderRepository.findByAccountId(accountId);
    }

    /**
     * Gets all unsynchronized folders for an account.
     */
    public List<EmailFolder> getUnsynchedFolders(long accountId) throws SQLException {
        return folderRepository.findUnsynched(accountId);
    }

    /**
     * Marks a folder as synchronized.
     */
    public void markFolderSynched(long folderId, LocalDateTime lastSynched) throws SQLException {
        folderRepository.updateSynchedState(folderId, true, lastSynched);
    }

    /**
     * Updates the message count for a folder.
     */
    public void updateMessageCount(long folderId, int messageCount) throws SQLException {
        folderRepository.updateMessageCount(folderId, messageCount);
    }

    /**
     * Gets the unread message count for a folder.
     */
    public int getUnreadMessageCount(long folderId) throws SQLException {
        return messageRepository.countUnreadByFolderId(folderId);
    }

    /**
     * Synchronizes local folders with server folders.
     * Creates local folders that don't exist and updates existing ones.
     */
    public void syncFoldersWithServer(Account account, List<EmailFolder> serverFolders)
            throws SQLException {
        List<EmailFolder> localFolders = getFoldersByAccountId(account.getId());

        for (EmailFolder serverFolder : serverFolders) {
            // Check if folder exists locally
            EmailFolder existingFolder = null;
            for (EmailFolder local : localFolders) {
                if (local.getName().equals(serverFolder.getName())) {
                    existingFolder = local;
                    break;
                }
            }

            if (existingFolder != null) {
                // Update existing folder
                existingFolder.setMessageCount(serverFolder.getMessageCount());
                existingFolder.setPath(serverFolder.getPath());
                existingFolder.setSynched(true);
                existingFolder.setLastSynched(LocalDateTime.now());
                folderRepository.update(existingFolder);
            } else {
                // Create new folder
                EmailFolder newFolder = new EmailFolder();
                newFolder.setAccountId(account.getId());
                newFolder.setName(serverFolder.getName());
                newFolder.setPath(serverFolder.getPath());
                newFolder.setMessageCount(serverFolder.getMessageCount());
                newFolder.setSubscribed(true);
                newFolder.setSynched(true);
                newFolder.setLastSynched(LocalDateTime.now());
                folderRepository.create(newFolder);
            }
        }

        logger.log(Level.INFO, "Synchronized folders for account: {0}", account.getEmail());
    }
}
