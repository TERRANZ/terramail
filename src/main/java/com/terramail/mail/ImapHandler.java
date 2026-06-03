package com.terramail.mail;

import com.terramail.model.Account;
import com.terramail.model.EmailFolder;
import com.terramail.model.EmailMessage;
import com.terramail.model.Recipient;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles IMAP operations for fetching and synchronizing emails.
 */
public class ImapHandler {
    private static final Logger logger = Logger.getLogger(ImapHandler.class.getName());
    private final MailSessionManager sessionManager;

    public ImapHandler(MailSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Lists all available folders for an account.
     */
    public List<EmailFolder> listFolders(Account account) throws MessagingException {
        List<EmailFolder> folders = new ArrayList<>();
        Store store = null;

        try {
            store = sessionManager.createImapStore(account);

            // Get namespace and folder
            Folder[] rootFolders = store.getDefaultFolder().list("");

            for (Folder imapFolder : rootFolders) {
                if (imapFolder.exists()) {
                    EmailFolder folder = new EmailFolder();
                    folder.setName(imapFolder.getName());
                    folder.setPath(imapFolder.getFullName());
                    folder.setSubscribed(imapFolder.isSubscribed());

                    try {
                        folder.setMessageCount(imapFolder.getMessageCount());
                    } catch (MessagingException e) {
                        folder.setMessageCount(0);
                    }

                    folders.add(folder);
                }
            }

            logger.log(Level.INFO, "Found {0} folders for account {1}",
                    new Object[]{folders.size(), account.getEmail()});

        } finally {
            if (store != null) {
                try {
                    store.close();
                } catch (Exception e) { /* ignore */ }
            }
        }

        return folders;
    }

    /**
     * Synchronizes messages from a specific folder.
     */
    public List<EmailMessage> syncFolder(Account account, EmailFolder folderInfo)
            throws MessagingException {
        List<EmailMessage> messages = new ArrayList<>();
        Store store = null;
        Folder folder = null;

        try {
            store = sessionManager.createImapStore(account);
            folder = store.getFolder(folderInfo.getPath());

            if (folder == null || !folder.exists()) {
                logger.warning("Folder does not exist: " + folderInfo.getPath());
                return messages;
            }

            folder.open(Folder.READ_ONLY);

            Message[] messagesArray = folder.getMessages();
            logger.log(Level.INFO, "Syncing {0} messages from folder: {1}",
                    new Object[]{messagesArray.length, folderInfo.getName()});

            for (int i = messagesArray.length - 1; i >= 0; i--) {
                try {
                    javax.mail.Message mailMessage = messagesArray[i];
                    EmailMessage emailMessage = convertToEmailMessage(mailMessage, folder);
                    messages.add(emailMessage);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error processing message " + i, e);
                }
            }

        } finally {
            if (folder != null && folder.isOpen()) {
                folder.close(false);
            }
            if (store != null) {
                try {
                    store.close();
                } catch (Exception e) { /* ignore */ }
            }
        }

        return messages;
    }

    /**
     * Fetches a single message by UID.
     */
    public EmailMessage fetchMessageByUid(Account account, EmailFolder folderInfo, int uid)
            throws MessagingException {
        Store store = null;
        Folder folder = null;

        try {
            store = sessionManager.createImapStore(account);
            folder = store.getFolder(folderInfo.getPath());

            if (folder == null || !folder.exists()) {
                return null;
            }

            folder.open(Folder.READ_ONLY);

            // Use UIDFolder extension to get message by UID
            if (folder instanceof UIDFolder) {
                UIDFolder uidFolder = (UIDFolder) folder;
                javax.mail.Message msg = uidFolder.getMessageByUID(uid);
                if (msg != null) {
                    return convertToEmailMessage(msg, folder);
                }
            } else {
                // Fallback: iterate through messages looking for matching UID
                Message[] messages = folder.getMessages();
                for (Message msg : messages) {
                    if (msg instanceof MimeMessage) {
                        // Try to get UID from message headers
                        String[] headers = msg.getHeader("Message-ID");
                        if (headers != null) {
                            for (int i = messages.length - msg.getMessageNumber(); i < messages.length; i++) {
                                if (messages[i] == msg) {
                                    return convertToEmailMessage(msg, folder);
                                }
                            }
                        }
                    }
                }
            }

            return null;

        } finally {
            if (folder != null && folder.isOpen()) {
                folder.close(false);
            }
            if (store != null) {
                try {
                    store.close();
                } catch (Exception e) { /* ignore */ }
            }
        }
    }

    /**
     * Fetches messages matching a search term.
     */
    public List<EmailMessage> searchMessages(Account account, EmailFolder folderInfo,
                                             SearchTerm searchTerm) throws MessagingException {
        List<EmailMessage> messages = new ArrayList<>();
        Store store = null;
        Folder folder = null;

        try {
            store = sessionManager.createImapStore(account);
            folder = store.getFolder(folderInfo.getPath());

            if (folder == null || !folder.exists()) {
                return messages;
            }

            folder.open(Folder.READ_ONLY);

            Message[] messagesArray = folder.search(searchTerm);

            for (javax.mail.Message mailMessage : messagesArray) {
                try {
                    messages.add(convertToEmailMessage(mailMessage, folder));
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error processing message", e);
                }
            }

        } finally {
            if (folder != null && folder.isOpen()) {
                folder.close(false);
            }
            if (store != null) {
                try {
                    store.close();
                } catch (Exception e) { /* ignore */ }
            }
        }

        return messages;
    }

    /**
     * Marks a message as deleted on the server.
     */
    public void deleteMessageOnServer(Account account, EmailFolder folderInfo, int messageNumber)
            throws MessagingException {
        Store store = null;
        Folder folder = null;

        try {
            store = sessionManager.createImapStore(account);
            folder = store.getFolder(folderInfo.getPath());

            if (folder == null || !folder.exists()) {
                return;
            }

            folder.open(Folder.READ_WRITE);
            // Get the message by index and mark it as deleted
            javax.mail.Message msg = folder.getMessage(messageNumber);
            if (msg != null) {
                msg.setFlag(Flags.Flag.DELETED, true);
            }
            folder.expunge();

        } finally {
            if (folder != null && folder.isOpen()) {
                folder.close(false);
            }
            if (store != null) {
                try {
                    store.close();
                } catch (Exception e) { /* ignore */ }
            }
        }
    }

    /**
     * Converts a JavaMail Message to our EmailMessage model.
     */
    private EmailMessage convertToEmailMessage(javax.mail.Message mailMessage, Folder folder)
            throws MessagingException {
        EmailMessage message = new EmailMessage();

        // Basic properties
        if (mailMessage instanceof MimeMessage) {
            MimeMessage mimeMessage = (MimeMessage) mailMessage;
            message.setMessageId(mimeMessage.getMessageID());
            // Get In-Reply-To header (returns Address[] in some versions, or String from header)
            String inReplyToHeader = "";
            try {
                Address[] inReplyTo = mimeMessage.getReplyTo(); // uses In-Reply-To header
                if (inReplyTo != null && inReplyTo.length > 0) {
                    inReplyToHeader = inReplyTo[0].toString();
                }
            } catch (MessagingException e) {
                // Fallback to header
                String[] headers = mimeMessage.getHeader("In-Reply-To");
                if (headers != null && headers.length > 0) {
                    inReplyToHeader = headers[0];
                }
            }
            message.setInReplyTo(inReplyToHeader);
            message.setSubject(mimeMessage.getSubject() != null
                    ? mimeMessage.getSubject() : "(No Subject)");
        }

        // Date
        Date sentDate = mailMessage.getSentDate();
        Date receivedDate = mailMessage.getReceivedDate();
        if (sentDate != null) {
            message.setSentDate(LocalDateTime.ofInstant(sentDate.toInstant(), ZoneId.systemDefault()));
        }
        if (receivedDate != null) {
            message.setReceivedDate(LocalDateTime.ofInstant(receivedDate.toInstant(), ZoneId.systemDefault()));
        }

        // Flags
        Flags flags = mailMessage.getFlags();
        message.setRead(!flags.contains(Flags.Flag.SEEN));
        message.setFlagged(flags.contains(Flags.Flag.FLAGGED));
        message.setDeleted(flags.contains(Flags.Flag.DELETED));

        // Message number for IMAP operations
        int messageNumber = 0; // Not directly available from Message object
        message.setSizeBytes((long) mailMessage.getSize());

        // Process content and recipients
        try {
            processContent(mailMessage, message);
        } catch (java.io.IOException e) {
            logger.log(Level.WARNING, "Error processing message content", e);
        }

        return message;
    }

    /**
     * Processes message content and extracts recipients.
     */
    private void processContent(javax.mail.Message mailMessage, EmailMessage message)
            throws MessagingException, java.io.IOException {
        Object content = mailMessage.getContent();
        boolean hasAttachments = false;

        if (content instanceof MimeMessage) {
            MimeMessage mimeMessage = (MimeMessage) content;
            extractRecipients(mimeMessage, message);
            hasAttachments = processMimeBodyPart(mimeMessage, message);
        } else if (content instanceof String) {
            message.setBodyPlain((String) content);
            message.setBodyHtml(escapeHtml((String) content));
        } else if (content instanceof javax.mail.internet.MimeMultipart) {
            hasAttachments = processMimeBodyPart(mailMessage, message);
        }

        message.setHasAttachments(hasAttachments);
    }

    /**
     * Extracts recipients from a MimeMessage.
     */
    private void extractRecipients(MimeMessage mimeMessage, EmailMessage message)
            throws MessagingException {
        // From
        Address[] fromAddresses = mimeMessage.getFrom();
        if (fromAddresses != null && fromAddresses.length > 0) {
            for (Address addr : fromAddresses) {
                Recipient recipient = toRecipient(addr, "FROM");
                if (recipient != null) {
                    message.addRecipient(recipient);
                }
            }
        }

        // To
        Address[] toAddresses = mimeMessage.getRecipients(Message.RecipientType.TO);
        if (toAddresses != null) {
            for (Address addr : toAddresses) {
                Recipient recipient = toRecipient(addr, "TO");
                if (recipient != null) {
                    message.addRecipient(recipient);
                }
            }
        }

        // CC
        Address[] ccAddresses = mimeMessage.getRecipients(Message.RecipientType.CC);
        if (ccAddresses != null) {
            for (Address addr : ccAddresses) {
                Recipient recipient = toRecipient(addr, "CC");
                if (recipient != null) {
                    message.addRecipient(recipient);
                }
            }
        }

        // BCC
        Address[] bccAddresses = mimeMessage.getRecipients(Message.RecipientType.BCC);
        if (bccAddresses != null) {
            for (Address addr : bccAddresses) {
                Recipient recipient = toRecipient(addr, "BCC");
                if (recipient != null) {
                    message.addRecipient(recipient);
                }
            }
        }
    }

    /**
     * Converts an Address to a Recipient model.
     */
    private Recipient toRecipient(Address addr, String type) {
        if (addr instanceof InternetAddress) {
            InternetAddress ia = (InternetAddress) addr;
            Recipient recipient = new Recipient();
            recipient.setEmail(ia.getAddress() != null ? ia.getAddress() : "");
            recipient.setName(ia.getPersonal() != null ? ia.getPersonal() : "");
            recipient.setType(type);
            return recipient;
        }
        Recipient recipient = new Recipient();
        recipient.setEmail(addr.toString());
        recipient.setName("");
        recipient.setType(type);
        return recipient;
    }

    /**
     * Processes a MimeMultipart body part.
     */
    private boolean processMimeBodyPart(javax.mail.Message mailMessage, EmailMessage message)
            throws MessagingException, java.io.IOException {
        Object content = mailMessage.getContent();
        boolean hasAttachments = false;

        if (content instanceof javax.mail.internet.MimeMultipart) {
            javax.mail.internet.MimeMultipart multipart =
                    (javax.mail.internet.MimeMultipart) content;

            boolean hasHtml = false;

            for (int i = 0; i < multipart.getCount(); i++) {
                javax.mail.BodyPart bodyPart = multipart.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                String contentType = bodyPart.getContentType();

                if (disposition != null && disposition.equalsIgnoreCase("attachment")) {
                    hasAttachments = true;
                    // In a full implementation, we would save the attachment here
                } else if (disposition == null || disposition.equalsIgnoreCase("inline")) {
                    // Check if it's text content
                    if (contentType != null && contentType.toLowerCase().contains("html")) {
                        message.setBodyHtml(getBodyText(bodyPart));
                        hasHtml = true;
                    } else if (contentType != null && contentType.toLowerCase().contains("plain")) {
                        message.setBodyPlain(getBodyText(bodyPart));
                    }
                }
            }

            // If no HTML found but we have plain text, use it as HTML too
            if (!hasHtml && message.getBodyPlain() != null && !message.getBodyPlain().isEmpty()) {
                message.setBodyHtml(escapeHtml(message.getBodyPlain()));
            }
        } else {
            String text = getBodyText(mailMessage);
            message.setBodyPlain(text);
            message.setBodyHtml(escapeHtml(text));
        }

        return hasAttachments;
    }

    /**
     * Gets the text content from a BodyPart.
     */
    private String getBodyText(javax.mail.BodyPart bodyPart) throws MessagingException, java.io.IOException {
        Object content = bodyPart.getContent();
        String charset = "UTF-8";

        if (bodyPart.getContentType().contains("charset=")) {
            String[] parts = bodyPart.getContentType().split(";");
            for (String part : parts) {
                if (part.trim().startsWith("charset=")) {
                    charset = part.trim().substring("charset=".length()).trim().replace("\"", "");
                }
            }
        }

        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof byte[]) {
            return new String((byte[]) content, charset);
        } else {
            return bodyPart.getContent().toString();
        }
    }

    /**
     * Gets the text content from a Message.
     */
    private String getBodyText(javax.mail.Message message) throws MessagingException, java.io.IOException {
        Object content = message.getContent();
        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof byte[]) {
            return new String((byte[]) content, "UTF-8");
        } else if (content instanceof javax.mail.BodyPart) {
            return getBodyText((javax.mail.BodyPart) content);
        } else {
            return content != null ? content.toString() : "";
        }
    }

    /**
     * Escapes plain text for HTML display.
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&")
                .replace("<", "<")
                .replace(">", ">")
                .replace("\"", "\"")
                .replace("'", "'");
    }
}
