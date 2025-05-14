package ru.terra.mail.storage.db.entity;

import jakarta.persistence.*;
import ru.terra.mail.core.domain.MailMessage;

import java.io.Serializable;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
@Entity
@Table(name = "message_entity",
        indexes = {@Index(columnList = "createDate")}
)
public class MessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "system-uuid")
    private String guid;
    private Long createDate;
    @Lob
    private String subject;
    @Lob
    @Column(name = "from_address")
    private String from;
    @Lob
    @Column(name = "to_address")
    private String to;
    @Lob
    private String messageBody;
    @Lob
    private String headers;
    @Column(name = "folder_id")
    private String folderId;

    public MessageEntity() {
    }

    public MessageEntity(MailMessage m, String folderId) {
        this.createDate = m.getCreateDate().getTime();
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

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
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

    @Override
    public String toString() {
        return "MessageEntity{" +
                "guid='" + guid + '\'' +
                ", createDate=" + createDate +
                ", subject='" + subject + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", folderId='" + folderId + '\'' +
                '}';
    }
}
