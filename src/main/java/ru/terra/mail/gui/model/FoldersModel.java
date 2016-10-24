package ru.terra.mail.gui.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import ru.terra.mail.gui.controller.beans.FoldersTreeItem;
import ru.terra.mail.storage.entity.MailFolder;

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
        List<MailFolder> folders = storage.getRootFolders();
        if (folders != null) {
            treeRoot = new TreeItem<>(new FoldersTreeItem(folders.get(0)));
            folders.get(0).getChildFolders().forEach(cf -> processFolder(treeRoot, cf));
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
        folders.get(0).getChildFolders().forEach(cf -> processFolder(treeRoot, cf));
        return treeRoot;
    }

    public ObservableList<MailFolder> getFolders() {
    	ObservableList<MailFolder> storedFolders = storage.getRootFolders();
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
        storedFolders = merge(storedFolders, serverFolders);
        return storedFolders;
    }

    private TreeItem<FoldersTreeItem> processFolder(TreeItem<FoldersTreeItem> parent, MailFolder mailFolder) {
        TreeItem<FoldersTreeItem> ret = new TreeItem<>(new FoldersTreeItem(mailFolder));
        parent.getChildren().add(ret);
        mailFolder.getChildFolders().forEach(cf -> processFolder(ret, cf));
        return ret;
    }

    protected ObservableList<MailFolder> merge(ObservableList<MailFolder> storedFolders, ObservableList<MailFolder> serverFolders) {
        if (storedFolders == null) {
            storedFolders = FXCollections.observableArrayList();
            storedFolders.addAll(serverFolders);
        } else {
            Map<String, MailFolder> serverFoldersMap = new HashMap<>();
            List<MailFolder> expandedServerfolders = new ArrayList<>();
            List<MailFolder> expandedStoredfolders = new ArrayList<>();
            serverFolders.forEach(sf -> expandedServerfolders.addAll(expandFoldersTree(sf)));
            storedFolders.forEach(sf -> expandedStoredfolders.addAll(expandFoldersTree(sf)));
            expandedServerfolders.forEach(f -> serverFoldersMap.put(f.getFullName(), f));
            for (MailFolder storedFolder : expandedStoredfolders) {
                MailFolder serverFolder = serverFoldersMap.get(storedFolder.getFullName());
                if (serverFolder != null)
                    storedFolder.setFolder(serverFolder.getFolder());
                else
                    storedFolder.setDeleted(true);
            }
            mergeFoldersTree(storedFolders, serverFoldersMap);
        }

        storage.storeFolders(storedFolders);
        return storedFolders;
    }

    private void mergeFoldersTree(List<MailFolder> storedFolders, Map<String, MailFolder> serverFoldersMap) {
        List<MailFolder> foldersToAdd = new ArrayList<>();
        for (MailFolder storedFolder : storedFolders)
            if (!serverFoldersMap.containsKey(storedFolder.getFullName()))
                foldersToAdd.add(serverFoldersMap.get(storedFolder.getFullName()));
            else
                mergeFoldersTree(storedFolder.getChildFolders(), serverFoldersMap);

        storedFolders.addAll(foldersToAdd);
    }

    private List<MailFolder> expandFoldersTree(MailFolder mailFolder) {
        List<MailFolder> folders = new ArrayList<>();
        folders.add(mailFolder);
        if (mailFolder.getChildFolders().size() > 0)
            mailFolder.getChildFolders().forEach(mf -> folders.addAll(expandFoldersTree(mf)));
        return folders;
    }

}
