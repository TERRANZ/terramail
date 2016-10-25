package ru.terra.mail.storage.domain;

/**
 * Created by terranz on 20.10.16.
 */
public class MailMessageAttachment {
    private byte[] body;
    private String type;
    private String fileName;

    public MailMessageAttachment() {
    }

    public MailMessageAttachment(byte[] body, String type, String fileName) {
        this.body = body;
        this.type = type;
        this.fileName = fileName;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
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
}
