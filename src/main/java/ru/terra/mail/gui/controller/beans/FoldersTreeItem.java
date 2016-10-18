package ru.terra.mail.gui.controller.beans;

import ru.terra.mail.storage.entity.MailFolder;

/**
 * Created by terranz on 19.10.16.
 */
public class FoldersTreeItem extends MailFolder {
    @Override
    public String toString() {
        return this.getName();
    }
}
