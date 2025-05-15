package ru.terra.mail.gui.view.beans;

import lombok.Getter;
import ru.terra.mail.core.domain.MailMessage;

import java.util.Date;

/**
 * Created by terranz on 19.10.16.
 */
@Getter
public class MessagesTableItem {
    private final MailMessage message;

    public MessagesTableItem(MailMessage message) {
        this.message = message;
    }

    public String getSubject() {
        return message.getSubject();
    }

    public Date getDate() {
        return message.getCreateDate();
    }

}
