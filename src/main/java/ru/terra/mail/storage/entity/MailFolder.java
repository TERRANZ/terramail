package ru.terra.mail.storage.entity;

import java.util.List;
import java.util.UUID;

/**
 * Created by terranz on 18.10.16.
 */
public class MailFolder {
    private List<MailMessage> messages;
    private String guid;

    public MailFolder() {
        guid = UUID.randomUUID().toString();
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
}
