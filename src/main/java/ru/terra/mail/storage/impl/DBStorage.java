package ru.terra.mail.storage.impl;

import ru.terra.mail.storage.AbstractStorage;
import ru.terra.mail.storage.entity.MailFolder;
import ru.terra.mail.storage.entity.MailMessage;

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
    public List<MailMessage> getFolderMessages(MailFolder mailFolder) {
        return null;
    }

    @Override
    public void storeFolderMessages(MailFolder mailFolder, List<MailMessage> messages) {
        // TODO Auto-generated method stub

    }
}
