package ru.terra.mail.storage.db.entity;

import org.hibernate.annotations.GenericGenerator;
import ru.terra.mail.storage.domain.MailMessageAttachment;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
@Entity
public class AttachmentEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false, length = 35)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String guid;
    @Lob
    private byte[] body;
    private String type;
    private String fileName;
    private String messageId;

    public AttachmentEntity() {
    }

    public AttachmentEntity(MailMessageAttachment mma, String parentId) {
        this.messageId = parentId;
        this.body = mma.getBody();
        this.type = mma.getType();
        this.fileName = mma.getFileName();
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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
