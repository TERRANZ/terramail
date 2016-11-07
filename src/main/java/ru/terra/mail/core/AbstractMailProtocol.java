package ru.terra.mail.core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.terra.mail.storage.domain.MailFolder;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * Created by terranz on 18.10.16.
 */
public abstract class AbstractMailProtocol {
    protected Store store;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public abstract void login(String user, String pass, String server)
            throws MessagingException, GeneralSecurityException;

    public ObservableList<MailFolder> listFolders() throws MessagingException {
        ObservableList<MailFolder> ret = FXCollections.observableArrayList();
        Arrays.stream(store.getPersonalNamespaces()).map(f -> {
            try {
                return getFolders(f);
            } catch (MessagingException e) {
                logger.error("Unable to list folders", e);
            }
            return null;
        }).forEach(e -> ret.add(e));
        return ret;
    }

    protected MailFolder getFolders(Folder folder) throws MessagingException {
        MailFolder mailFolder = new MailFolder(folder);
        if (folder.list().length > 0) {
            Arrays.stream(folder.list()).forEach(f -> {
                try {
                    mailFolder.getChildFolders().add(getFolders(f));
                } catch (MessagingException e) {
                    logger.error("Unable to get folders", e);
                }
            });
        }
        return mailFolder;
    }
}
