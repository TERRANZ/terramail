package ru.terra.mail.storage.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by terranz on 18.10.16.
 */
public class MailMessage {

    private Date createDate;
    private String subject;
    private String from;
    private String to;
    private String messageBody;
    private List<MailMessageAttachment> attachments;
    private String guid;
    @JsonIgnore
    private MailFolder folder;
    @JsonIgnore
    private Message message;

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
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        this.message = msg;
        this.attachments = new ArrayList<>();
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

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<MailMessageAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MailMessageAttachment> attachments) {
        this.attachments = attachments;
    }
}
