package ru.terra.mail.storage;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.control.TreeItem;
import ru.terra.mail.gui.controller.beans.FoldersTreeItem;
import ru.terra.mail.storage.domain.MailFolder;
import ru.terra.mail.storage.domain.MailMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
public interface AbstractStorage {
    ObservableList<MailFolder> getAllFoldersTree() throws Exception;

    void storeFoldersTree(List<MailFolder> mailFolders);

    void storeFolders(List<MailFolder> mailFolders, String parentId);

    ObservableSet<MailMessage> getFolderMessages(MailFolder mailFolder);

    void storeFolderMessage(MailMessage m);

    void storeFolderMessageInFolder(String parentId, MailMessage m);

    Integer countMessagesInFolder(String folderId);

    void loadFromFolder(MailFolder folder);

    TreeItem<FoldersTreeItem> processFolder(TreeItem<FoldersTreeItem> parent, MailFolder mailFolder);

    ObservableList<MailFolder> merge(ObservableList<MailFolder> storedFolders,
                                     ObservableList<MailFolder> serverFolders);

    void mergeFoldersTree(List<MailFolder> storedFolders, Map<String, MailFolder> serverFoldersMap);

    List<MailFolder> expandFoldersTree(MailFolder mailFolder);
}
