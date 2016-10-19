package ru.terra.mail.storage.entity;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.mail.Folder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by terranz on 18.10.16.
 */
public class MailFolder {
    private String guid;
    private String name;
    private String fullName;
    private List<MailFolder> childFolders;
    private Integer unreadMessages = 0;
    @JsonIgnore
    private Folder folder;
    private Boolean deleted;

    public MailFolder() {
        guid = UUID.randomUUID().toString();
    }

    public MailFolder(Folder folder) {
        this.name = folder.getName();
        this.fullName = folder.getFullName();
        this.childFolders = new ArrayList<>();
        this.guid = UUID.randomUUID().toString();
        this.folder = folder;
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
