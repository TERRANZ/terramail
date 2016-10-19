package ru.terra.mail.gui.controller.beans;

import ru.terra.mail.storage.entity.MailMessage;

/**
 * Created by terranz on 19.10.16.
 */
public class MessagesTableItem {
    private MailMessage message;

    public MessagesTableItem(MailMessage message) {
        this.message = message;
    }

    public String getSubject() {
        return message.getSubject();
    }

    public String getDate() {
        return message.getCreateDate().toString();
    }

    public MailMessage getMessage() {
        return message;
    }
}
