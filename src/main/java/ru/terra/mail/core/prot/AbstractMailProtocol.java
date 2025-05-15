package ru.terra.mail.core.prot;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.terra.mail.core.domain.MailFolder;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * Created by terranz on 18.10.16.
 */
@Slf4j
public abstract class AbstractMailProtocol {
    protected Store store;

    public abstract void login(String user, String pass, String server)
            throws MessagingException, GeneralSecurityException;

    public ObservableList<MailFolder> listFolders() throws MessagingException {
        val ret = FXCollections.<MailFolder>observableArrayList();
        Arrays.stream(store.getPersonalNamespaces()).map(f -> {
            try {
                return getFolders(f);
            } catch (MessagingException e) {
                log.error("Unable to list folders", e);
            }
            return null;
        }).forEach(ret::add);
        return ret;
    }

    protected MailFolder getFolders(Folder folder) throws MessagingException {
        val mailFolder = new MailFolder(folder);
        if (folder.list().length > 0) {
            Arrays.stream(folder.list()).forEach(f -> {
                try {
                    mailFolder.getChildFolders().add(getFolders(f));
                } catch (MessagingException e) {
                    log.error("Unable to get folders", e);
                }
            });
        }
        return mailFolder;
    }
}
