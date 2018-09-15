package ru.terra.mail.storage.db.entity;

import org.hibernate.annotations.GenericGenerator;
import ru.terra.mail.core.domain.MailMessageAttachment;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
@Entity
public class AttachmentEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String guid;
    private String type;
    private String fileName;
    private String messageId;
    private String localFileName;

    public AttachmentEntity() {
    }

    public AttachmentEntity(MailMessageAttachment mma, String parentId) {
        this.messageId = parentId;
        this.type = mma.getType();
        this.fileName = mma.getFileName();
        this.localFileName = mma.getLocalFileName();
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

    public String getLocalFileName() {
        return localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }
}
