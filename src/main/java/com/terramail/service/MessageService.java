package com.terramail.service;

import com.terramail.model.Attachment;
import com.terramail.model.EmailMessage;
import com.terramail.model.Recipient;
import com.terramail.repository.AttachmentRepository;
import com.terramail.repository.MessageRepository;
import com.terramail.repository.RecipientRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service for managing email messages.
 */
public class MessageService {
    private static final Logger logger = Logger.getLogger(MessageService.class.getName());
    private final MessageRepository messageRepository;
    private final RecipientRepository recipientRepository;
    private final AttachmentRepository attachmentRepository;

    public MessageService() {
        this.messageRepository = new MessageRepository();
        this.recipientRepository = new RecipientRepository();
        this.attachmentRepository = new AttachmentRepository();
    }

    /**
     * Creates a new message.
     */
    public EmailMessage createMessage(EmailMessage message) throws SQLException {
        return messageRepository.create(message);
    }

    /**
     * Creates a message with recipients and attachments.
     */
    public EmailMessage createMessageWithDetails(EmailMessage message,
                                                 List<Recipient> recipients,
                                                 List<Attachment> attachments)
            throws SQLException {
        // Create the message first
        message = messageRepository.create(message);

        // Set the message ID for recipients
        for (Recipient recipient : recipients) {
            recipient.setMessageId(message.getId());
        }

        // Create recipients
        if (!recipients.isEmpty()) {
            recipientRepository.createBatch(recipients);
        }

        // Create attachments
        if (attachments != null && !attachments.isEmpty()) {
            for (Attachment attachment : attachments) {
                attachment.setMessageId(message.getId());
            }
            attachmentRepository.createBatch(attachments);
        }

        // Update message with attachment count
        message.setAttachmentCount(attachments != null ? attachments.size() : 0);
        message.setHasAttachments(attachments != null && !attachments.isEmpty());
        messageRepository.update(message);

        return message;
    }

    /**
     * Updates a message.
     */
    public void updateMessage(EmailMessage message) throws SQLException {
        messageRepository.update(message);
    }

    /**
     * Deletes a message.
     */
    public void deleteMessage(long messageId) throws SQLException {
        messageRepository.delete(messageId);
    }

    /**
     * Finds a message by ID.
     */
    public EmailMessage findMessageById(long messageId) throws SQLException {
        EmailMessage message = messageRepository.findById(messageId);
        if (message != null) {
            loadMessageDetails(message);
        }
        return message;
    }

    /**
     * Finds a message by message ID header.
     */
    public EmailMessage findMessageByMessageId(String messageId) throws SQLException {
        EmailMessage message = messageRepository.findByMessageId(messageId);
        if (message != null) {
            loadMessageDetails(message);
        }
        return message;
    }

    /**
     * Gets messages for a folder with pagination.
     */
    public List<EmailMessage> getMessagesByFolder(long folderId, int offset, int limit)
            throws SQLException {
        List<EmailMessage> messages = messageRepository.findByFolderId(folderId, offset, limit);
        for (EmailMessage message : messages) {
            loadMessageDetails(message);
        }
        return messages;
    }

    /**
     * Gets unread messages for a folder.
     */
    public List<EmailMessage> getUnreadMessagesByFolder(long folderId) throws SQLException {
        List<EmailMessage> messages = messageRepository.findByFolderIdUnread(folderId);
        for (EmailMessage message : messages) {
            loadMessageDetails(message);
        }
        return messages;
    }

    /**
     * Searches messages by subject in a folder.
     */
    public List<EmailMessage> searchBySubject(long folderId, String query) throws SQLException {
        List<EmailMessage> messages = messageRepository.searchBySubject(folderId, query);
        for (EmailMessage message : messages) {
            loadMessageDetails(message);
        }
        return messages;
    }

    /**
     * Marks a message as read.
     */
    public void markAsRead(long messageId, boolean isRead) throws SQLException {
        messageRepository.updateReadState(messageId, isRead);
    }

    /**
     * Toggles the flagged state of a message.
     */
    public void toggleFlagged(long messageId) throws SQLException {
        EmailMessage message = messageRepository.findById(messageId);
        if (message != null) {
            messageRepository.updateFlaggedState(messageId, !message.isFlagged());
        }
    }

    /**
     * Gets the message count for a folder.
     */
    public int getMessageCount(long folderId) throws SQLException {
        return messageRepository.countByFolderId(folderId);
    }

    /**
     * Gets the unread message count for a folder.
     */
    public int getUnreadMessageCount(long folderId) throws SQLException {
        return messageRepository.countUnreadByFolderId(folderId);
    }

    /**
     * Loads message details (recipients and attachments).
     */
    private void loadMessageDetails(EmailMessage message) throws SQLException {
        // Load recipients
        List<Recipient> recipients = recipientRepository.findByMessageId(message.getId());
        message.setRecipients(recipients);

        // Load attachments
        List<Attachment> attachments = attachmentRepository.findByMessageId(message.getId());
        message.setAttachments(attachments);
        message.setAttachmentCount(attachments.size());
        message.setHasAttachments(!attachments.isEmpty());
    }
}
