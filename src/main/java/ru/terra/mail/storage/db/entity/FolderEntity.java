package ru.terra.mail.storage.db.entity;

import ru.terra.mail.storage.domain.MailFolder;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
@Entity
public class FolderEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String guid;
    private String name;
    private String fullName;
    private Integer unreadMessages = 0;
    private Boolean deleted;
    private String parentFolderId;

    public FolderEntity() {
        this.guid = UUID.randomUUID().toString();
    }

    public FolderEntity(MailFolder f, String parentFolderId) {
        this.guid = UUID.randomUUID().toString();
        this.name = f.getName();
        this.fullName = f.getFullName();
        this.unreadMessages = f.getUnreadMessages();
        this.deleted = f.getDeleted();
        this.parentFolderId = parentFolderId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(Integer unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(String parentFolderId) {
        this.parentFolderId = parentFolderId;
    }
}
