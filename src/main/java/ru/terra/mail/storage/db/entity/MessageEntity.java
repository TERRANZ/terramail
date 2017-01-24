package ru.terra.mail.storage.db.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import ru.terra.mail.storage.domain.MailMessage;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */

@Document(indexName = "mailmessage", type = "mailmessage", shards = 1, replicas = 0)
public class MessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String guid;
    private Date createDate;
    private String subject;
    private String from;
    private String to;
    private String messageBody;
    private String headers;
    private String folderId;

    public MessageEntity() {
    }

    public MessageEntity(MailMessage m, String folderId) {
        this.guid = UUID.randomUUID().toString();
        this.createDate = m.getCreateDate();
        this.subject = m.getSubject();
        this.from = m.getFrom();
        this.to = m.getTo();
        this.messageBody = m.getMessageBody();
        this.folderId = folderId;
        this.headers = m.getHeaders();
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }
}
