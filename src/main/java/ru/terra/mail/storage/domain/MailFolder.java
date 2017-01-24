package ru.terra.mail.storage.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import ru.terra.mail.storage.db.entity.FolderEntity;

import javax.mail.Folder;
import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by terranz on 18.10.16.
 */
public class MailFolder {
    private String guid;
    private List<MailFolder> childFolders;
    private String name;
    private String fullName;
    private Integer unreadMessages = 0;
    @JsonIgnore
    private Folder folder;
    private Boolean deleted;

    public MailFolder() {
        this.guid = UUID.randomUUID().toString();
        this.childFolders = new ArrayList<>();
    }

    public MailFolder(Folder folder) {
        this.name = folder.getName();
        this.fullName = folder.getFullName();
        this.childFolders = new ArrayList<>();
        this.guid = UUID.randomUUID().toString();
        this.folder = folder;
        try {
            this.unreadMessages = folder.getUnreadMessageCount();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public MailFolder(FolderEntity f) {
        this.name = f.getName();
        this.fullName = f.getFullName();
        this.childFolders = new ArrayList<>();
        this.guid = f.getGuid();
        this.deleted = f.getDeleted();
        this.unreadMessages = f.getUnreadMessages();
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

    public List<MailFolder> getChildFolders() {
        return childFolders;
    }

    public void setChildFolders(List<MailFolder> childFolders) {
        this.childFolders = childFolders;
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

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
