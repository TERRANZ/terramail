package com.terramail.mail;

import com.terramail.model.Account;
import com.terramail.model.Recipient;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles SMTP operations for sending emails.
 */
public class SmtpHandler {
    private static final Logger logger = Logger.getLogger(SmtpHandler.class.getName());
    private final MailSessionManager sessionManager;

    public SmtpHandler(MailSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Sends an email message.
     *
     * @param account     The account to send from
     * @param to          Recipients
     * @param subject     Email subject
     * @param bodyPlain   Plain text body
     * @param bodyHtml    HTML body (can be null)
     * @param recipients  All recipients (To, CC, BCC)
     * @param attachments List of attachment paths (can be null)
     * @return The message ID of the sent message
     */
    public String sendEmail(Account account, String[] to, String cc, String bcc,
                            String subject, String bodyPlain, String bodyHtml,
                            List<Path> attachments) throws MessagingException, java.io.IOException {
        Session session = sessionManager.createSmtpSession(account);
        Transport transport = null;

        try {
            transport = sessionManager.createSmtpTransport(account);
            transport.connect(account.getSmtpHost(), account.getSmtpPort(),
                    account.getSmtpUser(), account.getSmtpPassword());

            // Create the message
            MimeMessage message = createMimeMessage(session, account, to, cc, bcc,
                    subject, bodyPlain, bodyHtml, attachments);

            // Send the message
            transport.sendMessage(message, message.getAllRecipients());

            String messageId = message.getMessageID();
            logger.log(Level.INFO, "Email sent successfully: {0}", messageId);
            return messageId;

        } finally {
            if (transport != null && transport.isConnected()) {
                transport.close();
            }
        }
    }

    /**
     * Sends an email message with all recipient types.
     */
    public String sendEmail(Account account, String[] to, String[] cc, String[] bcc,
                            String subject, String bodyPlain, String bodyHtml,
                            List<Path> attachments) throws MessagingException, java.io.IOException {
        StringBuilder ccBuilder = new StringBuilder();
        if (cc != null) {
            for (int i = 0; i < cc.length; i++) {
                if (i > 0) ccBuilder.append(", ");
                ccBuilder.append(cc[i]);
            }
        }

        StringBuilder bccBuilder = new StringBuilder();
        if (bcc != null) {
            for (int i = 0; i < bcc.length; i++) {
                if (i > 0) bccBuilder.append(", ");
                bccBuilder.append(bcc[i]);
            }
        }

        return sendEmail(account, to, ccBuilder.toString(), bccBuilder.toString(),
                subject, bodyPlain, bodyHtml, attachments);
    }

    /**
     * Sends an email with recipients model objects.
     */
    public String sendEmail(Account account, String[] to, List<Recipient> cc, List<Recipient> bcc,
                            String subject, String bodyPlain, String bodyHtml,
                            List<Path> attachments) throws MessagingException, java.io.IOException {
        String ccStr = null;
        if (cc != null && !cc.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cc.size(); i++) {
                if (i > 0) sb.append(", ");
                Recipient r = cc.get(i);
                sb.append(r.getDisplayString());
            }
            ccStr = sb.toString();
        }

        String bccStr = null;
        if (bcc != null && !bcc.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bcc.size(); i++) {
                if (i > 0) sb.append(", ");
                Recipient r = bcc.get(i);
                sb.append(r.getEmail());
            }
            bccStr = sb.toString();
        }

        return sendEmail(account, to, ccStr, bccStr, subject, bodyPlain, bodyHtml, attachments);
    }

    /**
     * Creates a MimeMessage for sending.
     */
    private MimeMessage createMimeMessage(Session session, Account account,
                                          String[] to, String cc, String bcc,
                                          String subject, String bodyPlain,
                                          String bodyHtml, List<Path> attachments)
            throws MessagingException, java.io.IOException {
        MimeMessage message = new MimeMessage(session);

        // Set from address
        String fromAddress = account.getEmail();
        String displayName = account.getDisplayName();
        if (displayName != null && !displayName.isEmpty()) {
            message.setFrom(new InternetAddress(fromAddress, displayName));
        } else {
            message.setFrom(new InternetAddress(fromAddress));
        }

        // Set recipients
        if (to != null && to.length > 0) {
            InternetAddress[] toAddresses = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++) {
                toAddresses[i] = new InternetAddress(to[i]);
            }
            message.setRecipients(Message.RecipientType.TO, toAddresses);
        }

        // Set CC
        if (cc != null && !cc.isEmpty()) {
            message.setRecipients(Message.RecipientType.CC,
                    InternetAddress.parse(cc));
        }

        // Set BCC
        if (bcc != null && !bcc.isEmpty()) {
            message.setRecipients(Message.RecipientType.BCC,
                    InternetAddress.parse(bcc));
        }

        // Set subject and date
        message.setSubject(subject, "UTF-8");
        message.setSentDate(new java.util.Date());

        // Create the multipart message
        Multipart multipart = new MimeMultipart();

        // Create the alternative text/HTML part
        if (bodyHtml != null && !bodyHtml.isEmpty()) {
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(bodyPlain != null ? bodyPlain : "", "UTF-8");

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(bodyHtml, "text/html; charset=utf-8");

            MimeMultipart alternativePart = new MimeMultipart("alternative");
            alternativePart.addBodyPart(textPart);
            alternativePart.addBodyPart(htmlPart);

            // Wrap the alternative part in a BodyPart before adding to multipart
            MimeBodyPart alternativeWrapper = new MimeBodyPart();
            alternativeWrapper.setContent(alternativePart);
            multipart.addBodyPart(alternativeWrapper);
        } else {
            // Plain text only
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(bodyPlain != null ? bodyPlain : "", "UTF-8");
            multipart.addBodyPart(textPart);
        }

        // Add attachments
        if (attachments != null) {
            for (Path attachmentPath : attachments) {
                if (Files.exists(attachmentPath)) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(attachmentPath.toFile());
                    multipart.addBodyPart(attachmentPart);
                }
            }
        }

        message.setContent(multipart);
        return message;
    }

    /**
     * Reads the content of an input stream as a string.
     */
    private String readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }
}
