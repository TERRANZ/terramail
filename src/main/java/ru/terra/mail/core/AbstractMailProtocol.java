package ru.terra.mail.core;

import ru.terra.mail.storage.entity.MailFolder;

import javax.mail.MessagingException;
import javax.mail.Store;
import java.util.List;

/**
 * Created by terranz on 18.10.16.
 */
public abstract class AbstractMailProtocol {
    protected Store store;

    public abstract void login(String user, String pass, String server) throws MessagingException;

    public abstract List<MailFolder> listFolders() throws MessagingException;
}
