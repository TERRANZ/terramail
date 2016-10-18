package ru.terra.mail.storage.entity;

import javax.mail.Folder;
import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by terranz on 18.10.16.
 */
public class MailFolder {
    private List<MailMessage> messages;
    private String guid;
    private String name;
    private String fullName;
    private List<MailFolder> childFolders;
    private Integer unreadMessages = 0;

    public MailFolder() {
        guid = UUID.randomUUID().toString();
    }

    public MailFolder(Folder folder) {
        this.name = folder.getName();
        this.fullName = folder.getFullName();
        try {
            this.unreadMessages = folder.getUnreadMessageCount();
            Arrays.stream(folder.getMessages()).forEach(m -> this.messages.add(new MailMessage(m, this)));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        this.messages = new ArrayList<>();
        this.childFolders = new ArrayList<>();
        this.guid = UUID.randomUUID().toString();

    }

    public List<MailMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<MailMessage> messages) {
        this.messages = messages;
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
}
