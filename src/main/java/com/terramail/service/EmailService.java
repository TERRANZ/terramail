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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class EmailService {

    private static final Logger logger = Logger.getLogger(EmailService.class.getName());

    private AccountSettings settings;
    private final AttachmentService attachmentService;
    private final int messageLoadingThreads;

    public EmailService(AccountSettings settings, AttachmentService attachmentService, int messageLoadingThreads) {
        this.settings = settings;
        this.attachmentService = attachmentService;
        this.messageLoadingThreads = messageLoadingThreads;
    }

    public EmailService(AccountSettings settings, AttachmentService attachmentService) {
        this(settings, attachmentService, 10);
    }

    public long getAccountId() {
        return settings.getId();
    }

    public void updateSettings(AccountSettings newSettings) {
        this.settings = newSettings;
    }

    public List<Message> fetchMessages(Folder folder) {
        logger.fine(() -> "fetchMessages called for folder: " + folder.getName() + " (id=" + folder.getId() + ")");
        Session session = createImapSession();
        List<Message> messages = Collections.synchronizedList(new ArrayList<>());

        try (Store store = session.getStore("imap")) {
            logger.fine(() -> "Connecting to IMAP store for account: " + settings.getImapUser());
            store.connect(settings.getImapUser(), settings.getImapPassword());
            logger.info("Successfully connected to IMAP server: " + settings.getImapHost() + ":" + settings.getImapPort());

            jakarta.mail.Folder imapFolder = store.getFolder(folder.getName());
            if (imapFolder == null) {
                logger.warning("IMAP folder not found: " + folder.getName());
                return messages;
            }
            logger.fine(() -> "Opening IMAP folder: " + imapFolder.getName() + " in READ_ONLY mode");
            imapFolder.open(jakarta.mail.Folder.READ_ONLY);

            jakarta.mail.Message[] emails = imapFolder.getMessages();
            logger.info("Folder '" + folder.getName() + "' contains " + emails.length + " messages, using " + messageLoadingThreads + " threads for processing");

            if (emails.length == 0) {
                logger.info("Folder '" + folder.getName() + "' is empty, skipping message processing");
                imapFolder.close(false);
                return messages;
            }

            // Use thread pool for parallel message processing
            ExecutorService executor = Executors.newFixedThreadPool(messageLoadingThreads);
            List<CompletableFuture<Message>> futures = new ArrayList<>();

            for (int i = 0; i < emails.length; i++) {
                final int index = i;
                final jakarta.mail.Message email = emails[i];
                CompletableFuture<Message> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        logger.fine(() -> "Thread-" + Thread.currentThread().getName() + " processing message " + (index + 1) + "/" + emails.length);
                        Message msg = new Message();
                        msg.setFolderId(folder.getId());
                        msg.setFrom(formatAddress(email.getFrom()));
                        msg.setTo(formatAddress(email.getRecipients(jakarta.mail.Message.RecipientType.TO)));
                        msg.setCc(formatAddress(email.getRecipients(jakarta.mail.Message.RecipientType.CC)));
                        msg.setSubject(email.getSubject() != null ? email.getSubject() : "(No Subject)");
                        msg.setDate(email.getSentDate() != null ? email.getSentDate().toInstant() : Instant.now());

                        logger.fine(() -> "Message " + (index + 1) + " - From: " + msg.getFrom() + ", Subject: " + msg.getSubject());

                        String body = extractBody(email);
                        msg.setBody(body);
                        logger.fine(() -> "Message " + (index + 1) + " body length: " + (body != null ? body.length() : 0));

                        msg.setSeen(email.isSet(Flags.Flag.SEEN));
                        msg.setFlagged(email.isSet(Flags.Flag.FLAGGED));
                        logger.fine(() -> "Message " + (index + 1) + " - Seen: " + msg.isSeen() + ", Flagged: " + msg.isFlagged());

                        List<AttachmentInfo> attachments = extractAttachments(email, folder.getId());
                        msg.setAttachments(attachments);
                        if (!attachments.isEmpty()) {
                            logger.info(() -> "Message " + (index + 1) + " has " + attachments.size() + " attachment(s): " +
                                    String.join(", ", attachments.stream().map(AttachmentInfo::getName).toList()));
                        }

                        logger.fine(() -> "Successfully processed message " + (index + 1));
                        return msg;
                    } catch (Exception e) {
                        logger.severe(() -> "Error processing message " + index + ": " + e.getMessage());
                        return null;
                    }
                }, executor);
                futures.add(future);
            }

            // Wait for all futures to complete
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            try {
                allFutures.get();
                // Collect results
                for (CompletableFuture<Message> future : futures) {
                    Message msg = future.get();
                    if (msg != null) {
                        messages.add(msg);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.severe("Error waiting for message processing to complete: " + e.getMessage());
                Thread.currentThread().interrupt();
            } finally {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }

            logger.info("Folder '" + folder.getName() + "' fetch complete: " + messages.size() + " messages processed with " + messageLoadingThreads + " threads");
            imapFolder.close(false);
        } catch (MessagingException e) {
            logger.severe(() -> "Failed to fetch messages from folder '" + folder.getName() + "': " + e.getMessage());
            throw new RuntimeException("Failed to fetch messages from folder " + folder.getName(), e);
        }
        return messages;
    }

    public List<Folder> listAvailableFolders() {
        logger.fine("listAvailableFolders called");
        Session session = createImapSession();
        List<Folder> folders = new ArrayList<>();

        try (Store store = session.getStore("imap")) {
            logger.fine("Connecting to IMAP store for folder listing");
            store.connect(settings.getImapUser(), settings.getImapPassword());
            logger.info("Connected to IMAP server for folder listing");

            jakarta.mail.Folder[] imapFolders = store.getDefaultFolder().list();
            logger.info("Found " + imapFolders.length + " top-level folders in default folder");

            for (jakarta.mail.Folder imapFolder : imapFolders) {
                logger.fine(() -> "Processing top-level folder: " + imapFolder.getName());
                fetchFoldersRecursive(imapFolder, "", folders);
            }
            logger.info("Folder listing complete: " + folders.size() + " total folders found");
        } catch (MessagingException e) {
            logger.severe("Failed to list folders from mail server: " + e.getMessage());
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

        logger.fine(() -> "Processing folder: " + name + " (path: " + imapPath + ", parent: " + parentPath + ")");

        Folder.Type type = detectFolderType(name, imapFolder);
        logger.fine(() -> "Detected folder type for '" + name + "': " + type);

        Folder folder = new Folder();
        folder.setAccountId(settings.getId());
        folder.setName(name);
        folder.setType(type);
        folder.setImapPath(imapPath);
        folders.add(folder);
        logger.fine(() -> "Added folder to list: " + name + " (total: " + folders.size() + ")");

        try {
            jakarta.mail.Folder[] subFolders = imapFolder.list();
            logger.fine(() -> "Folder '" + name + "' has " + subFolders.length + " subfolder(s)");
            for (jakarta.mail.Folder subFolder : subFolders) {
                fetchFoldersRecursive(subFolder, imapPath, folders);
            }
        } catch (MessagingException e) {
            logger.severe(() -> "Error fetching subfolders of '" + name + "': " + e.getMessage());
        }
    }

    private Folder.Type detectFolderType(String name, jakarta.mail.Folder imapFolder) {
        String lowerName = name.toLowerCase(Locale.ENGLISH);
        logger.fine(() -> "Detecting folder type for: " + name + " (lowercase: " + lowerName + ")");

        if (lowerName.equals("inbox") || lowerName.equals("in")) {
            logger.fine(() -> "Folder '" + name + "' detected as INBOX");
            return Folder.Type.INBOX;
        }
        if (lowerName.contains("sent") || lowerName.contains("sent items") || lowerName.equals("sentmail")) {
            logger.fine(() -> "Folder '" + name + "' detected as SENT");
            return Folder.Type.SENT;
        }
        if (lowerName.contains("draft")) {
            logger.fine(() -> "Folder '" + name + "' detected as DRAFTS");
            return Folder.Type.DRAFTS;
        }
        if (lowerName.contains("trash") || lowerName.contains("deleted")) {
            logger.fine(() -> "Folder '" + name + "' detected as TRASH");
            return Folder.Type.TRASH;
        }

        logger.fine(() -> "Folder '" + name + "' detected as CUSTOM");
        return Folder.Type.CUSTOM;
    }

    public boolean sendMessage(String to, String cc, String subject, String body) {
        logger.info(() -> "sendMessage called - To: " + to + ", CC: " + cc + ", Subject: " + subject);
        Session session = createSmtpSession();
        try {
            logger.fine("Creating new MIME message");
            jakarta.mail.Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(settings.getImapUser()));
            logger.fine(() -> "Set From: " + settings.getImapUser());
            msg.setRecipients(jakarta.mail.Message.RecipientType.TO, InternetAddress.parse(to));
            logger.fine(() -> "Set To: " + to);
            if (cc != null && !cc.isBlank()) {
                msg.setRecipients(jakarta.mail.Message.RecipientType.CC, InternetAddress.parse(cc));
                logger.fine(() -> "Set CC: " + cc);
            }
            msg.setSubject(subject);
            msg.setText(body);
            msg.setSentDate(new Date());

            logger.fine("Sending message via SMTP");
            Transport.send(msg);
            logger.info("Email successfully sent to: " + to);
            return true;
        } catch (MessagingException e) {
            logger.severe(() -> "Failed to send email to " + to + ": " + e.getMessage());
            throw new RuntimeException("Failed to send email to " + to, e);
        }
    }

    private Session createImapSession() {
        logger.fine("Creating IMAP session");
        Properties props = new Properties();
        props.put("mail.imap.host", settings.getImapHost());
        props.put("mail.imap.port", String.valueOf(settings.getImapPort()));
        props.put("mail.store.protocol", "imap");
        if (settings.isImapSsl()) {
            props.put("mail.imap.ssl.enable", "true");
            logger.fine("IMAP SSL enabled");
        } else {
            logger.fine("IMAP SSL disabled");
        }
        logger.fine(() -> "IMAP session created for host: " + settings.getImapHost() + " port: " + settings.getImapPort());
        return Session.getInstance(props);
    }

    private Session createSmtpSession() {
        logger.fine("Creating SMTP session");
        Properties props = new Properties();
        props.put("mail.smtp.host", settings.getSmtpHost());
        props.put("mail.smtp.port", String.valueOf(settings.getSmtpPort()));
        if (settings.isSmtpSsl()) {
            props.put("mail.smtp.ssl.enable", "true");
            logger.fine("SMTP SSL enabled");
        } else {
            logger.fine("SMTP SSL disabled");
        }
        props.put("mail.smtp.auth", "true");
        logger.fine("SMTP authentication enabled");
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(settings.getSmtpUser(), settings.getSmtpPassword());
            }
        });
        logger.fine(() -> "SMTP session created for host: " + settings.getSmtpHost() + " port: " + settings.getSmtpPort());
        return session;
    }

    private String extractBody(jakarta.mail.Message email) throws MessagingException, IOException {
        logger.fine("Extracting body from message");
        Object content = email.getContent();
        logger.fine("Message content type: " + (content != null ? content.getClass().getName() : "null"));
        if (content instanceof String str) {
            logger.fine("Message body is plain text, length: " + str.length());
            return str;
        }
        if (content instanceof MimeMultipart multipart) {
            int partCount = multipart.getCount();
            logger.fine("Message has multipart content with " + partCount + " parts");
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                String contentType = bodyPart.getContentType();
                logger.fine("Part " + (i + 1) + " - disposition: " + disposition + ", contentType: " + contentType);
                if (disposition == null || disposition.equalsIgnoreCase(Part.INLINE)) {
                    if (contentType.startsWith("text/plain")) {
                        String plainBody = bodyPart.getContent() instanceof String ? (String) bodyPart.getContent() : "";
                        logger.fine("Found text/plain part, length: " + plainBody.length());
                        return plainBody;
                    }
                    if (contentType.startsWith("text/html")) {
                        String html = bodyPart.getContent() instanceof String ? (String) bodyPart.getContent() : "";
                        logger.fine("Found text/html part, length: " + html.length() + ", converting to plain text");
                        return htmlToPlain(html);
                    }
                } else {
                    logger.fine("Part " + (i + 1) + " skipped (disposition: " + disposition + ")");
                }
            }
            logger.warning("No suitable body part found in multipart message");
            return "";
        }
        logger.fine("Message content is not String or MimeMultipart, using toString()");
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
        logger.fine("Extracting attachments for folderId: " + folderId);
        List<AttachmentInfo> attachments = new ArrayList<>();

        if (!(email.getContent() instanceof MimeMultipart multipart)) {
            logger.fine("Message content is not multipart, no attachments");
            return attachments;
        }

        int partCount = multipart.getCount();
        logger.fine("Checking " + partCount + " multipart parts for attachments");
        for (int i = 0; i < partCount; i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            String disposition = bodyPart.getDisposition();
            logger.fine("Part " + (i + 1) + " disposition: " + disposition);
            if (disposition != null && disposition.equalsIgnoreCase(Part.ATTACHMENT)) {
                String filename = bodyPart.getFileName();
                logger.fine("Part " + (i + 1) + " is an attachment, filename: " + filename);
                if (filename != null && !filename.isBlank()) {
                    Path tempFile = Files.createTempFile("terrmail-", "-" + filename);
                    logger.fine("Created temp file: " + tempFile);
                    try (OutputStream os = Files.newOutputStream(tempFile)) {
                        int size = bodyPart.getSize();
                        logger.fine("Extracting attachment: " + filename + " (" + size + " bytes)");
                        bodyPart.getInputStream().transferTo(os);
                    }
                    logger.fine("Saving attachment to database: " + filename);
                    attachmentService.saveAttachment(folderId, filename, Files.newInputStream(tempFile));
                    attachments.add(new AttachmentInfo(
                            filename,
                            bodyPart.getSize() > 0 ? bodyPart.getSize() : Files.size(tempFile),
                            bodyPart.getContentType()
                    ));
                    logger.info("Saved attachment: " + filename);
                } else {
                    logger.fine("Attachment part has no filename, skipping");
                }
            }
        }
        logger.info("Extracted " + attachments.size() + " attachment(s) from message");
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
