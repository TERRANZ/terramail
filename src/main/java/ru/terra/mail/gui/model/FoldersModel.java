package ru.terra.mail.gui.model;

import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;
import ru.terra.mail.core.domain.MailFolder;
import ru.terra.mail.core.domain.MailFoldersTree;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by terranz on 19.10.16.
 */
@Component
public class FoldersModel extends AbstractModel<MailFolder> {

    private MailFoldersTree treeRoot;

    public MailFoldersTree getStoredFolders() {
        List<MailFolder> folders = null;
        try {
            folders = getStorage().getAllFoldersTree();
        } catch (Exception e) {
            logger.error("unable to get folders", e);
        }
        if (folders != null && folders.size() > 0) {
            treeRoot = new MailFoldersTree(folders.get(0));
            folders.get(0).getChildFolders().forEach(cf -> getStorage().processFolder(treeRoot, cf));
        } else {
            final MailFolder mf = new MailFolder();
            mf.setName("Loading...");
            treeRoot = new MailFoldersTree(mf);
        }
        return treeRoot;
    }

    public MailFoldersTree getTreeRoot() {
        final List<MailFolder> folders = getFolders();
        treeRoot = new MailFoldersTree(folders.get(0));
        folders.get(0).getChildFolders().forEach(cf -> getStorage().processFolder(treeRoot, cf));
        return treeRoot;
    }

    private List<MailFolder> getFolders() {
        List<MailFolder> storedFolders = getStorage().getAllFoldersTree();
        ObservableList<MailFolder> serverFolders = null;
        boolean loggedIn = false;
        try {
            performLogin();
            loggedIn = true;
        } catch (Exception e) {
            logger.error("Unable to login", e);
        }
        if (loggedIn) {
            try {
                serverFolders = protocol.listFolders();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        storedFolders = getStorage().merge(storedFolders, serverFolders);
        return storedFolders;
    }

    public List<MailFolder> getAllFolders() {
        List<MailFolder> folders = new ArrayList<>();
        folders.add(getTreeRoot().getCurrentFolder());
        folders.addAll(listFolders(folders.get(0)));
        return folders;
    }

    private List<MailFolder> listFolders(final MailFolder mailFolder) {
        final List<MailFolder> ret = new ArrayList<>(mailFolder.getChildFolders());
        mailFolder.getChildFolders().forEach(mf -> ret.addAll(listFolders(mf)));
        return ret;
    }

}
