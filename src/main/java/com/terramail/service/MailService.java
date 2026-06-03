package com.terramail.service;

import com.terramail.mail.ImapHandler;
import com.terramail.mail.MailSessionManager;
import com.terramail.mail.SmtpHandler;
import com.terramail.model.Account;
import com.terramail.model.EmailFolder;
import com.terramail.model.EmailMessage;
import com.terramail.model.Recipient;

import javax.mail.MessagingException;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service for sending and receiving emails.
 */
public class MailService {
    private static final Logger logger = Logger.getLogger(MailService.class.getName());

    private final SmtpHandler smtpHandler;
    private final ImapHandler imapHandler;
    private final MessageService messageService;

    public MailService() {
        MailSessionManager sessionManager = new MailSessionManager();
        this.smtpHandler = new SmtpHandler(sessionManager);
        this.imapHandler = new ImapHandler(sessionManager);
        this.messageService = new MessageService();
    }

    public MailService(SmtpHandler smtpHandler, ImapHandler imapHandler) {
        this.smtpHandler = smtpHandler;
        this.imapHandler = imapHandler;
        this.messageService = new MessageService();
    }

    /**
     * Sends an email.
     *
     * @param account   The account to send from
     * @param to        Array of recipient addresses
     * @param subject   Email subject
     * @param bodyPlain Plain text body
     * @param bodyHtml  HTML body (can be null)
     * @return The created message ID
     */
    public String sendEmail(Account account, String[] to, String subject,
                            String bodyPlain, String bodyHtml)
            throws MessagingException, SQLException, IOException {
        logger.info("Sending email from " + account.getEmail() + " to " + String.join(", ", to));

        // Send via SMTP
        String messageId = smtpHandler.sendEmail(account, to, (String) null, (String) null,
                subject, bodyPlain, bodyHtml, null);

        // Create a sent copy in the database
        EmailMessage sentMessage = new EmailMessage();
        sentMessage.setMessageId(messageId);
        sentMessage.setSubject(subject);
        sentMessage.setBodyPlain(bodyPlain);
        sentMessage.setBodyHtml(bodyHtml);
        sentMessage.setSentDate(java.time.LocalDateTime.now());

        // Add recipients
        for (String recipient : to) {
            Recipient r = new Recipient();
            r.setEmail(recipient);
            r.setType(Recipient.Type.TO.getValue());
            sentMessage.addRecipient(r);
        }

        // Save to database (will need to set folder_id to Sent folder)
        // This is simplified - in a real app, you'd find the Sent folder
        messageService.createMessage(sentMessage);

        return messageId;
    }

    /**
     * Sends an email with CC and BCC recipients.
     */
    public String sendEmail(Account account, String[] to, String[] cc, String[] bcc,
                            String subject, String bodyPlain, String bodyHtml)
            throws MessagingException, SQLException, IOException {
        logger.info("Sending email with CC/BCC from " + account.getEmail());

        String messageId = smtpHandler.sendEmail(account, to, cc, bcc,
                subject, bodyPlain, bodyHtml, null);

        // Create a sent copy
        EmailMessage sentMessage = new EmailMessage();
        sentMessage.setMessageId(messageId);
        sentMessage.setSubject(subject);
        sentMessage.setBodyPlain(bodyPlain);
        sentMessage.setBodyHtml(bodyHtml);
        sentMessage.setSentDate(java.time.LocalDateTime.now());

        // Add all recipients
        addRecipientsToMessage(sentMessage, to, Recipient.Type.TO);
        if (cc != null) {
            addRecipientsToMessage(sentMessage, cc, Recipient.Type.CC);
        }

        messageService.createMessage(sentMessage);

        return messageId;
    }

    /**
     * Sends an email with attachments.
     */
    public String sendEmailWithAttachments(Account account, String[] to,
                                           String subject, String bodyPlain, String bodyHtml,
                                           List<Path> attachmentPaths)
            throws MessagingException, SQLException, IOException {
        logger.info("Sending email with " + attachmentPaths.size() + " attachments");

        String messageId = smtpHandler.sendEmail(account, to, (String) null, (String) null,
                subject, bodyPlain, bodyHtml, attachmentPaths);

        // Create a sent copy
        EmailMessage sentMessage = new EmailMessage();
        sentMessage.setMessageId(messageId);
        sentMessage.setSubject(subject);
        sentMessage.setBodyPlain(bodyPlain);
        sentMessage.setBodyHtml(bodyHtml);
        sentMessage.setSentDate(java.time.LocalDateTime.now());
        sentMessage.setHasAttachments(!attachmentPaths.isEmpty());
        sentMessage.setAttachmentCount(attachmentPaths.size());

        addRecipientsToMessage(sentMessage, to, Recipient.Type.TO);

        messageService.createMessage(sentMessage);

        return messageId;
    }

    /**
     * Receives/syncs new emails for an account.
     */
    public void receiveEmail(Account account) throws MessagingException, SQLException {
        logger.info("Receiving emails for " + account.getEmail());

        // This is handled by SyncService
        // This method is provided for direct receive calls
    }

    /**
     * Fetches a specific message from the server.
     */
    public EmailMessage fetchMessage(Account account, EmailFolder folder, int uid)
            throws MessagingException {
        return imapHandler.fetchMessageByUid(account, folder, uid);
    }

    /**
     * Searches for messages matching a term.
     */
    public List<EmailMessage> searchMessages(Account account, EmailFolder folder,
                                             SearchTerm term) throws MessagingException {
        return imapHandler.searchMessages(account, folder, term);
    }

    /**
     * Marks a message as deleted on the server.
     */
    public void deleteMessageOnServer(Account account, EmailFolder folder, int messageNumber)
            throws MessagingException {
        imapHandler.deleteMessageOnServer(account, folder, messageNumber);
    }

    /**
     * Lists folders for an account.
     */
    public List<EmailFolder> listFolders(Account account) throws MessagingException {
        return imapHandler.listFolders(account);
    }

    private void addRecipientsToMessage(EmailMessage message, String[] emails, Recipient.Type type) {
        for (String email : emails) {
            Recipient r = new Recipient();
            r.setEmail(email);
            r.setType(type.getValue());
            message.addRecipient(r);
        }
    }
}
