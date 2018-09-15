package ru.terra.mail.storage;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import ru.terra.mail.core.domain.MailFolder;
import ru.terra.mail.core.domain.MailFoldersTree;
import ru.terra.mail.core.domain.MailMessage;

import java.util.List;
import java.util.Map;

public interface AbstractStorage {
    ObservableList<MailFolder> getAllFoldersTree() throws Exception;

    void storeFolders(List<MailFolder> mailFolders, String parentId);

    ObservableSet<MailMessage> getFolderMessages(String folderGuid);

    void storeFolderMessageInFolder(String folderId, MailMessage m);

    Integer countMessagesInFolder(String folderId);

    void loadFromFolder(MailFolder folder);

    MailFoldersTree processFolder(MailFoldersTree parent, MailFolder mailFolder);

    ObservableList<MailFolder> merge(ObservableList<MailFolder> storedFolders,
                                     ObservableList<MailFolder> serverFolders);

    void mergeFoldersTree(List<MailFolder> storedFolders, Map<String, MailFolder> serverFoldersMap);

    List<MailFolder> expandFoldersTree(MailFolder mailFolder);
}
