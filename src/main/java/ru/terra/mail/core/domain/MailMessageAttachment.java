package ru.terra.mail.core.domain;

import ru.terra.mail.storage.db.entity.AttachmentEntity;

/**
 * Created by terranz on 20.10.16.
 */
public class MailMessageAttachment {
    private String type;
    private String fileName;
    private Boolean downloaded;
    private String localFileName;

    public MailMessageAttachment() {
    }

    public MailMessageAttachment(String type, String fileName, String localFileName, Boolean downloaded) {
        this.type = type;
        this.fileName = fileName;
        this.localFileName = localFileName;
        this.downloaded = downloaded;
    }

    public MailMessageAttachment(AttachmentEntity a) {
        this.type = a.getType();
        this.fileName = a.getFileName();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(Boolean downloaded) {
        this.downloaded = downloaded;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }
}
