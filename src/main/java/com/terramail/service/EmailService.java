package com.terramail.service;

import com.terramail.model.AccountSettings;
import com.terramail.model.AttachmentInfo;
import com.terramail.model.Folder;
import com.terramail.model.Message;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EmailService {

    private AccountSettings settings;
    private final AttachmentService attachmentService;

    public EmailService(AccountSettings settings, AttachmentService attachmentService) {
        this.settings = settings;
        this.attachmentService = attachmentService;
    }

    public void updateSettings(AccountSettings newSettings) {
        this.settings = newSettings;
    }

    public List<Message> fetchMessages(Folder folder) {
        Session session = createImapSession();
        List<Message> messages = new ArrayList<>();

        try (Store store = session.getStore("imap")) {
            store.connect(settings.getImapUser(), settings.getImapPassword());

            jakarta.mail.Folder imapFolder = store.getFolder(folder.getName());
            if (imapFolder == null) {
                return messages;
            }
            imapFolder.open(jakarta.mail.Folder.READ_ONLY);

            jakarta.mail.Message[] emails = imapFolder.getMessages();
            AtomicInteger index = new AtomicInteger(0);

            for (jakarta.mail.Message email : emails) {
                try {
                    Message msg = new Message();
                    msg.setFolderId(folder.getId());
                    msg.setFrom(formatAddress(email.getFrom()));
                    msg.setTo(formatAddress(email.getRecipients(jakarta.mail.Message.RecipientType.TO)));
                    msg.setCc(formatAddress(email.getRecipients(jakarta.mail.Message.RecipientType.CC)));
                    msg.setSubject(email.getSubject() != null ? email.getSubject() : "(No Subject)");
                    msg.setDate(email.getSentDate() != null ? email.getSentDate().toInstant() : Instant.now());

                    String body = extractBody(email);
                    msg.setBody(body);

                    msg.setSeen(email.isSet(Flags.Flag.SEEN));
                    msg.setFlagged(email.isSet(Flags.Flag.FLAGGED));

                    List<AttachmentInfo> attachments = extractAttachments(email, folder.getId());
                    msg.setAttachments(attachments);

                    messages.add(msg);
                } catch (Exception e) {
                    System.err.println("Error processing message " + index.get() + ": " + e.getMessage());
                }
                index.incrementAndGet();
            }

            imapFolder.close(false);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to fetch messages from folder " + folder.getName(), e);
        }
        return messages;
    }

    public List<Folder> listAvailableFolders() {
        Session session = createImapSession();
        List<Folder> folders = new ArrayList<>();

        try (Store store = session.getStore("imap")) {
            store.connect(settings.getImapUser(), settings.getImapPassword());

            jakarta.mail.Folder[] imapFolders = store.getDefaultFolder().list();

            for (jakarta.mail.Folder imapFolder : imapFolders) {
                fetchFoldersRecursive(imapFolder, "", folders);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to list folders from mail server", e);
        }
        return folders;
    }

    private void fetchFoldersRecursive(jakarta.mail.Folder imapFolder, String parentPath, List<Folder> folders) {
        String name = imapFolder.getName();
        char separatorChar = '/';
        try {
            char sep = imapFolder.getSeparator();
            if (sep != 0) separatorChar = sep;
        } catch (MessagingException e) {
            separatorChar = '/';
        }
        String imapPath = name == null || name.isEmpty() ? parentPath :
                          (parentPath.isEmpty() ? name : parentPath + separatorChar + name);

        Folder.Type type = detectFolderType(name, imapFolder);

        Folder folder = new Folder();
        folder.setName(name);
        folder.setType(type);
        folder.setImapPath(imapPath);
        folders.add(folder);

        try {
            jakarta.mail.Folder[] subFolders = imapFolder.list();
            for (jakarta.mail.Folder subFolder : subFolders) {
                fetchFoldersRecursive(subFolder, imapPath, folders);
            }
        } catch (MessagingException e) {
            System.err.println("Error fetching subfolders of '" + name + "': " + e.getMessage());
        }
    }

    private Folder.Type detectFolderType(String name, jakarta.mail.Folder imapFolder) {
        String lowerName = name.toLowerCase(Locale.ENGLISH);

        if (lowerName.equals("inbox") || lowerName.equals("in")) {
            return Folder.Type.INBOX;
        }
        if (lowerName.contains("sent") || lowerName.contains("sent items") || lowerName.equals("sentmail")) {
            return Folder.Type.SENT;
        }
        if (lowerName.contains("draft")) {
            return Folder.Type.DRAFTS;
        }
        if (lowerName.contains("trash") || lowerName.contains("deleted")) {
            return Folder.Type.TRASH;
        }

        return Folder.Type.CUSTOM;
    }

    public boolean sendMessage(String to, String cc, String subject, String body) {
        Session session = createSmtpSession();
        try {
            jakarta.mail.Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(settings.getImapUser()));
            msg.setRecipients(jakarta.mail.Message.RecipientType.TO, InternetAddress.parse(to));
            if (cc != null && !cc.isBlank()) {
                msg.setRecipients(jakarta.mail.Message.RecipientType.CC, InternetAddress.parse(cc));
            }
            msg.setSubject(subject);
            msg.setText(body);
            msg.setSentDate(new Date());

            Transport.send(msg);
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to " + to, e);
        }
    }

    private Session createImapSession() {
        Properties props = new Properties();
        props.put("mail.imap.host", settings.getImapHost());
        props.put("mail.imap.port", String.valueOf(settings.getImapPort()));
        props.put("mail.store.protocol", "imap");
        if (settings.isImapSsl()) {
            props.put("mail.imap.ssl.enable", "true");
        }
        return Session.getInstance(props);
    }

    private Session createSmtpSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", settings.getSmtpHost());
        props.put("mail.smtp.port", String.valueOf(settings.getSmtpPort()));
        if (settings.isSmtpSsl()) {
            props.put("mail.smtp.ssl.enable", "true");
        }
        props.put("mail.smtp.auth", "true");
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(settings.getSmtpUser(), settings.getSmtpPassword());
            }
        });
    }

    private String extractBody(jakarta.mail.Message email) throws MessagingException, IOException {
        Object content = email.getContent();
        if (content instanceof String str) {
            return str;
        }
        if (content instanceof MimeMultipart multipart) {
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                if (disposition == null || disposition.equalsIgnoreCase(Part.INLINE)) {
                    String contentType = bodyPart.getContentType();
                    if (contentType.startsWith("text/plain")) {
                        return bodyPart.getContent() instanceof String ? (String) bodyPart.getContent() : "";
                    }
                    if (contentType.startsWith("text/html")) {
                        String html = bodyPart.getContent() instanceof String ? (String) bodyPart.getContent() : "";
                        return htmlToPlain(html);
                    }
                }
            }
            return "";
        }
        return content != null ? content.toString() : "";
    }

    private String htmlToPlain(String html) {
        return html.replaceAll("<br\\s*/?>", "\n")
                .replaceAll("</p>", "\n\n")
                .replaceAll("<[^>]+>", "")
                .replaceAll("&nbsp;", " ")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&")
                .replaceAll("&quot;", "\"")
                .trim();
    }

    private List<AttachmentInfo> extractAttachments(jakarta.mail.Message email, long folderId) throws MessagingException, IOException {
        List<AttachmentInfo> attachments = new ArrayList<>();

        if (!(email.getContent() instanceof MimeMultipart multipart)) {
            return attachments;
        }

        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            String disposition = bodyPart.getDisposition();
            if (disposition != null && disposition.equalsIgnoreCase(Part.ATTACHMENT)) {
                String filename = bodyPart.getFileName();
                if (filename != null && !filename.isBlank()) {
                    Path tempFile = Files.createTempFile("terrmail-", "-" + filename);
                    try (OutputStream os = Files.newOutputStream(tempFile)) {
                        bodyPart.getInputStream().transferTo(os);
                    }
                    attachmentService.saveAttachment(folderId, filename, Files.newInputStream(tempFile));
                    attachments.add(new AttachmentInfo(
                            filename,
                            bodyPart.getSize() > 0 ? bodyPart.getSize() : Files.size(tempFile),
                            bodyPart.getContentType()
                    ));
                }
            }
        }
        return attachments;
    }

    private String formatAddress(Address[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return "";
        }
        List<String> emails = new ArrayList<>();
        for (Address addr : addresses) {
            emails.add(addr.toString());
        }
        return String.join(", ", emails);
    }
}
