package ru.terra.mail.storage.impl;

import ru.terra.mail.storage.AbstractStorage;
import ru.terra.mail.storage.entity.MailFolder;

import java.util.List;

/**
 * Created by terranz on 18.10.16.
 */
public class DBStorage extends AbstractStorage {
    @Override
    public List<MailFolder> getRootFolders() {
        return null;
    }

    @Override
    public void storeFolders(List<MailFolder> mailFolders) {

    }

    @Override
    public List<MailFolder> getFolderMessages(MailFolder mailFolder) {
        return null;
    }
}
