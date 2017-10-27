package ru.terra.mail.storage.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import ru.terra.mail.storage.db.entity.MessageEntity;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.*;

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
    private String folderGuid;
    @JsonIgnore
    private Message message;
    private String headers;

    public MailMessage() {
        guid = UUID.randomUUID().toString();
    }

    public MailMessage(Message msg, String folderGuid) {
//        LoggerFactory.getLogger(this.getClass()).info("Generating mail message from transport message");
        this.guid = UUID.randomUUID().toString();
        this.folderGuid = folderGuid;
        try {
            this.subject = msg.getSubject();
            this.createDate = msg.getReceivedDate();
            this.from = msg.getFrom()[0].toString();
            if (msg.getRecipients(Message.RecipientType.TO) != null && msg.getRecipients(Message.RecipientType.TO).length > 0)
                this.to = msg.getRecipients(Message.RecipientType.TO)[0].toString();
            Enumeration iter = msg.getAllHeaders();
            headers = "";
            while (iter.hasMoreElements()) {
                Header h = (Header) iter.nextElement();
                headers += h.getName() + " : " + h.getValue() + "\n";
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        this.message = msg;
        this.attachments = new ArrayList<>();
    }

    public MailMessage(MessageEntity me, String folderGuid) {
        this.guid = UUID.randomUUID().toString();
        this.folderGuid = folderGuid;
        this.subject = me.getSubject();
        this.createDate = new Date(me.getCreateDate());
        this.from = me.getFrom();
        this.to = me.getTo();
        this.messageBody = me.getMessageBody();
        this.attachments = new ArrayList<>();
        this.headers = me.getHeaders();
    }

    public String getFolderGuid() {
        return folderGuid;
    }

    public void setFolderGuid(String folderGuid) {
        this.folderGuid = folderGuid;
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

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }
}
