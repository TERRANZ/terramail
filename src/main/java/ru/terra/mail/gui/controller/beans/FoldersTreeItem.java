package ru.terra.mail.gui.controller.beans;

import ru.terra.mail.storage.domain.MailFolder;

/**
 * Created by terranz on 19.10.16.
 */
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

    public MailFolder getMailFolder() {
        return mailFolder;
    }

    public void setMailFolder(MailFolder mailFolder) {
        this.mailFolder = mailFolder;
    }

}
