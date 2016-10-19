package ru.terra.mail.storage;

import ru.terra.mail.storage.entity.MailFolder;
import ru.terra.mail.storage.entity.MailMessage;

import java.util.List;

/**
 * Created by terranz on 18.10.16.
 */
public abstract class AbstractStorage {
    public abstract List<MailFolder> getRootFolders();

    public abstract void storeFolders(List<MailFolder> mailFolders);

    public abstract List<MailMessage> getFolderMessages(MailFolder mailFolder);

    public abstract void storeFolderMessages(MailFolder mailFolder, List<MailMessage> messages);
}
