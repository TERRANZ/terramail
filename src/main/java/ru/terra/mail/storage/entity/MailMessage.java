package ru.terra.mail.storage.entity;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by terranz on 18.10.16.
 */
public class MailMessage {
    @JsonIgnore
    private MailFolder folder;
    private Date createDate;
    private String subject;
    private String from;
    private String to;
    private String messageBody;
    private String guid;

    public MailMessage() {
        guid = UUID.randomUUID().toString();
    }

    public MailMessage(Message msg, MailFolder mailFolder) {
        this.guid = UUID.randomUUID().toString();
        this.folder = mailFolder;
        try {
            this.subject = msg.getSubject();
            this.createDate = msg.getReceivedDate();
            this.from = msg.getFrom()[0].toString();
            this.to = msg.getRecipients(Message.RecipientType.TO)[0].toString();
            this.messageBody = msg.getContent().toString();
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    public MailFolder getFolder() {
        return folder;
    }

    public void setFolder(MailFolder folder) {
        this.folder = folder;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
