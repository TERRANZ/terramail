package ru.terra.mail.gui.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import ru.terra.mail.gui.controller.beans.FoldersTreeItem;
import ru.terra.mail.storage.domain.MailFolder;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by terranz on 19.10.16.
 */
public class FoldersModel extends AbstractModel<MailFolder> {

    private TreeItem<FoldersTreeItem> treeRoot;

    public TreeItem<FoldersTreeItem> getStoredFolders() {
        List<MailFolder> folders = null;
        try {
            folders = storage.getRootFolders();
        } catch (Exception e) {
            logger.error("unable to get folders", e);
        }
        if (folders != null && folders.size() > 0) {
            treeRoot = new TreeItem<>(new FoldersTreeItem(folders.get(0)));
            folders.get(0).getChildFolders().forEach(cf -> storage.processFolder(treeRoot, cf));
        } else {
            MailFolder mf = new MailFolder();
            mf.setName("Loading...");
            treeRoot = new TreeItem<>(new FoldersTreeItem(mf));
        }
        return treeRoot;
    }

    public TreeItem<FoldersTreeItem> getTreeRoot() {
        List<MailFolder> folders = getFolders();
        treeRoot = new TreeItem<>(new FoldersTreeItem(folders.get(0)));
        folders.get(0).getChildFolders().forEach(cf -> storage.processFolder(treeRoot, cf));
        return treeRoot;
    }
    
    public ObservableList<MailFolder> getFolders() {
        ObservableList<MailFolder> storedFolders = null;
        try {
            storedFolders = storage.getRootFolders();
        } catch (Exception e) {
            logger.error("unable to get folders", e);
        }
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
        storedFolders = storage.merge(storedFolders, serverFolders);
        return storedFolders;
    }

}
