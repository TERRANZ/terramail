package com.terramail.model;

import java.util.Objects;

/**
 * Represents an email attachment.
 */
public class Attachment {
    private long id;
    private long messageId;
    private String filename;
    private String contentType;
    private Long sizeBytes;
    private String storagePath;
    private String charset;

    public Attachment() {
        this.id = 0;
        this.messageId = 0;
        this.filename = "";
        this.contentType = "application/octet-stream";
        this.sizeBytes = 0L;
        this.storagePath = "";
        this.charset = null;
    }

    public Attachment(String filename, String contentType, Long sizeBytes) {
        this();
        this.filename = filename;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getHumanReadableSize() {
        if (sizeBytes == null) {
            return "Unknown";
        }
        if (sizeBytes < 1024) {
            return sizeBytes + " B";
        } else if (sizeBytes < 1024 * 1024) {
            return String.format("%.2f KB", sizeBytes / 1024.0);
        } else if (sizeBytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", sizeBytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", sizeBytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attachment that = (Attachment) o;
        return filename.equals(that.filename) && messageId == that.messageId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, filename);
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", sizeBytes=" + sizeBytes +
                '}';
    }
}
