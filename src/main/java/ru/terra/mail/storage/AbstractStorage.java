package ru.terra.mail.storage;

import ru.terra.mail.core.domain.MailFolder;
import ru.terra.mail.core.domain.MailFoldersTree;
import ru.terra.mail.core.domain.MailMessage;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AbstractStorage {
    List<MailFolder> getAllFoldersTree() throws Exception;

    void storeFolders(List<MailFolder> mailFolders, String parentId);

    Set<MailMessage> getFolderMessages(String folderGuid);

    void storeFolderMessageInFolder(String folderId, MailMessage m);

    Integer countMessagesInFolder(String folderId);

    void loadFromFolder(MailFolder folder);

    MailFoldersTree processFolder(MailFoldersTree parent, MailFolder mailFolder);

    List<MailFolder> merge(List<MailFolder> storedFolders,
                           List<MailFolder> serverFolders);

    void mergeFoldersTree(List<MailFolder> storedFolders, Map<String, MailFolder> serverFoldersMap);

    List<MailFolder> expandFoldersTree(MailFolder mailFolder);
}
