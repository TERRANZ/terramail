package ru.terra.mail.gui.view.beans;

import lombok.Getter;
import lombok.Setter;
import ru.terra.mail.core.domain.MailFolder;

/**
 * Created by terranz on 19.10.16.
 */
@Getter
@Setter
public class FoldersTreeItem {
    private MailFolder mailFolder;

    public FoldersTreeItem(MailFolder folder) {
        this.mailFolder = folder;
    }

    @Override
    public String toString() {
        String unread = mailFolder.getUnreadMessages() > 0 ? " " + mailFolder.getUnreadMessages() : "";
        return mailFolder.getName() + unread;
    }
}
