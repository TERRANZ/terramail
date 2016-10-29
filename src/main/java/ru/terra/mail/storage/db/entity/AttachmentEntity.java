package ru.terra.mail.storage.db.entity;

import ru.terra.mail.storage.domain.MailMessageAttachment;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "AttachmentEntity.findByParentId", query = "SELECT f FROM AttachmentEntity f WHERE f.parentId = :parentId")})
public class AttachmentEntity extends AbstractEntity {

    private static final long serialVersionUID = 1L;
    private Integer parentId;
    @Column(name = "blob_body", nullable = true)
    @Lob
    private byte[] body;
    @Column(name = "ae_type")
    private String type;
    private String fileName;

    public AttachmentEntity() {
    }

    public AttachmentEntity(MailMessageAttachment mma, Integer id) {
        this.parentId = id;
        this.body = mma.getBody();
        this.type = mma.getType();
        this.fileName = mma.getFileName();
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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
