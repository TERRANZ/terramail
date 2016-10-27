package ru.terra.mail.storage.db.entity;

import ru.terra.mail.storage.domain.MailMessage;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Entity
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "MessageEntity.findByFolderId", query = "SELECT f FROM MessageEntity f WHERE f.folderId = :folderId"),
        @NamedQuery(name = "MessageEntity.findByCreateDate", query = "SELECT f FROM MessageEntity f WHERE f.createDate = :createDate")})
public class MessageEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Column(name = "me_subj")
    private String subject;
    @Column(name = "me_from")
    private String from;
    @Column(name = "me_to")
    private String to;
    @Column(name = "me_mb")
    private String messageBody;
    private Integer folderId;

    public MessageEntity() {
    }

    public MessageEntity(MailMessage m, Integer folderId) {
        this.createDate = m.getCreateDate();
        this.subject = m.getSubject();
        this.from = m.getFrom();
        this.to = m.getTo();
        this.messageBody = m.getMessageBody();
        this.folderId = folderId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getFolderId() {
        return folderId;
    }

    public void setFolderId(Integer folderId) {
        this.folderId = folderId;
    }

	@Override
	public String toString() {
		return "MessageEntity [id=" + id + ", createDate=" + createDate + ", subject=" + subject + ", from=" + from
				+ ", to=" + to + ", messageBody=" + messageBody + ", folderId=" + folderId + "]";
	}

}
