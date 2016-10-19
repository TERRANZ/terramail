package ru.terra.mail.gui.controller.beans;

import ru.terra.mail.storage.entity.MailFolder;

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
        return mailFolder.getName();
    }

    public MailFolder getMailFolder() {
        return mailFolder;
    }

    public void setMailFolder(MailFolder mailFolder) {
        this.mailFolder = mailFolder;
    }

}
