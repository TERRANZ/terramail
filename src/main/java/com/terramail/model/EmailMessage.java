package com.terramail.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an email message.
 */
public class EmailMessage {
    private long id;
    private long folderId;
    private String messageId;
    private String inReplyTo;
    private String subject;
    private String bodyPlain;
    private String bodyHtml;
    private LocalDateTime receivedDate;
    private LocalDateTime sentDate;
    private boolean isRead;
    private boolean isFlagged;
    private boolean isDeleted;
    private boolean hasAttachments;
    private int attachmentCount;
    private Long sizeBytes;
    private LocalDateTime localReceivedAt;
    private List<Recipient> recipients;
    private List<Attachment> attachments;

    public EmailMessage() {
        this.id = 0;
        this.messageId = "";
        this.inReplyTo = "";
        this.subject = "";
        this.bodyPlain = "";
        this.bodyHtml = "";
        this.receivedDate = null;
        this.sentDate = null;
        this.isRead = false;
        this.isFlagged = false;
        this.isDeleted = false;
        this.hasAttachments = false;
        this.attachmentCount = 0;
        this.sizeBytes = null;
        this.localReceivedAt = LocalDateTime.now();
        this.recipients = new ArrayList<>();
        this.attachments = new ArrayList<>();
    }

    public EmailMessage(String subject, LocalDateTime receivedDate) {
        this();
        this.subject = subject;
        this.receivedDate = receivedDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFolderId() {
        return folderId;
    }

    public void setFolderId(long folderId) {
        this.folderId = folderId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getInReplyTo() {
        return inReplyTo;
    }

    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }

    public String getSubject() {
        return subject != null ? subject : "(No Subject)";
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyPlain() {
        return bodyPlain;
    }

    public void setBodyPlain(String bodyPlain) {
        this.bodyPlain = bodyPlain;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDateTime receivedDate) {
        this.receivedDate = receivedDate;
    }

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean hasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public LocalDateTime getLocalReceivedAt() {
        return localReceivedAt;
    }

    public void setLocalReceivedAt(LocalDateTime localReceivedAt) {
        this.localReceivedAt = localReceivedAt;
    }

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }

    public void addRecipient(Recipient recipient) {
        this.recipients.add(recipient);
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public void addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
    }

    public Recipient getSender() {
        return recipients.stream()
                .filter(r -> "FROM".equals(r.getType()))
                .findFirst()
                .orElse(null);
    }

    public List<Recipient> getToRecipients() {
        return recipients.stream()
                .filter(r -> "TO".equals(r.getType()))
                .toList();
    }

    public List<Recipient> getCcRecipients() {
        return recipients.stream()
                .filter(r -> "CC".equals(r.getType()))
                .toList();
    }

    public String getDisplaySubject() {
        String subj = getSubject();
        if (subj.startsWith("Re:") || subj.startsWith("R:")) {
            return "▶ " + subj;
        }
        return subj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailMessage that = (EmailMessage) o;
        return messageId.equals(that.messageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId);
    }

    @Override
    public String toString() {
        return "EmailMessage{" +
                "id=" + id +
                ", messageId='" + messageId + '\'' +
                ", subject='" + getSubject() + '\'' +
                ", receivedDate=" + receivedDate +
                ", isRead=" + isRead +
                ", isFlagged=" + isFlagged +
                ", attachmentCount=" + attachmentCount +
                '}';
    }
}
