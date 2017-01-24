package ru.terra.mail.storage;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.control.TreeItem;
import org.springframework.stereotype.Component;
import ru.terra.mail.gui.controller.beans.FoldersTreeItem;
import ru.terra.mail.storage.domain.MailFolder;
import ru.terra.mail.storage.domain.MailMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
@Component
public class ElasticSearchStorage implements AbstractStorage {
    @Override
    public ObservableList<MailFolder> getRootFolders() throws Exception {
        return null;
    }

    @Override
    public void storeFolders(List<MailFolder> mailFolders) {

    }

    @Override
    public void storeFolders(List<MailFolder> mailFolders, Integer parentId) {

    }

    @Override
    public ObservableSet<MailMessage> getFolderMessages(MailFolder mailFolder) {
        return null;
    }

    @Override
    public void storeFolderMessage(MailMessage m) {

    }

    @Override
    public void storeFolderMessage(Integer parentId, MailMessage m) {

    }

    @Override
    public void storeFolderMessages(MailFolder mailFolder, List<MailMessage> messages) {

    }

    @Override
    public Integer countMessages(MailFolder mailFolder) {
        return null;
    }

    @Override
    public void loadFromFolder(MailFolder folder) {

    }

    @Override
    public TreeItem<FoldersTreeItem> processFolder(TreeItem<FoldersTreeItem> parent, MailFolder mailFolder) {
        return null;
    }

    @Override
    public ObservableList<MailFolder> merge(ObservableList<MailFolder> storedFolders, ObservableList<MailFolder> serverFolders) {
        return null;
    }

    @Override
    public void mergeFoldersTree(List<MailFolder> storedFolders, Map<String, MailFolder> serverFoldersMap) {

    }

    @Override
    public List<MailFolder> expandFoldersTree(MailFolder mailFolder) {
        return null;
    }
}
