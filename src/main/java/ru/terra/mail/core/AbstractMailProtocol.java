package ru.terra.mail.core;

import ru.terra.mail.storage.entity.MailFolder;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by terranz on 18.10.16.
 */
public abstract class AbstractMailProtocol {
    protected Store store;

    public abstract void login(String user, String pass, String server) throws MessagingException, GeneralSecurityException;

    public List<MailFolder> listFolders() throws MessagingException {
        return Arrays.stream(store.getPersonalNamespaces()).map(f -> {
            MailFolder mailFolder = new MailFolder();
            mailFolder.setName(f.getName());
            mailFolder.setChildFolders(new ArrayList<>());
            return mailFolder;
        }).collect(Collectors.toList());
    }

    protected MailFolder getFolders(Folder folder) throws MessagingException {
        MailFolder mailFolder = new MailFolder(folder);
        if (folder.list().length > 0) {
            Arrays.stream(folder.list()).forEach(f -> {
                try {
                    mailFolder.getChildFolders().add(getFolders(f));
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
        }
        return mailFolder;
    }
}
