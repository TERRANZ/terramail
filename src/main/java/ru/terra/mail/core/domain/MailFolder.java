package ru.terra.mail.core.domain;

import com.beust.jcommander.internal.Lists;
import ru.terra.mail.storage.ModificationNotify;
import ru.terra.mail.storage.db.entity.FolderEntity;

import javax.mail.Folder;
import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by terranz on 18.10.16.
 */
public class MailFolder {
    private String guid;
    private List<MailFolder> childFolders;
    private String name;
    private String fullName;
    private Integer unreadMessages = 0;
    private Folder folder;
    private Boolean deleted;
    private List<ModificationNotify> subscribers = Lists.newArrayList();

    public MailFolder() {
        this.childFolders = new ArrayList<>();
    }

    public MailFolder(Folder folder) {
        this.name = folder.getName();
        this.fullName = folder.getFullName();
        this.childFolders = new ArrayList<>();
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

    public List<ModificationNotify> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(final List<ModificationNotify> subscribers) {
        this.subscribers = subscribers;
    }

    public void notifyModified() {
        subscribers.forEach(m -> m.onModified(this));
    }

    @Override
    public String toString() {
        return "MailFolder{" +
                "guid='" + guid + '\'' +
                ", childFolders=" + childFolders +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", unreadMessages=" + unreadMessages +
                ", folder=" + folder +
                ", deleted=" + deleted +
                '}';
    }
}
