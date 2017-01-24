package ru.terra.mail.storage.db.entity;

import ru.terra.mail.storage.domain.MailFolder;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "FolderEntity.findByFullName", query = "SELECT f FROM FolderEntity f WHERE f.fullName = :fullName")})
public class FolderEntity extends AbstractEntity {

    private String name;
    private String fullName;
    private Integer unreadMessages = 0;
    private Integer parentFolderId;

    public FolderEntity() {
    }

    public FolderEntity(MailFolder f, Integer parent) {
        this.name = f.getName();
        this.fullName = f.getFullName();
        this.deleted = f.getDeleted();
        this.unreadMessages = f.getUnreadMessages();
        this.parentFolderId = parent;
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

    public Integer getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(Integer parentFolderId) {
        this.parentFolderId = parentFolderId;
    }

}
