package ru.terra.mail.storage;

import ru.terra.mail.core.domain.MailFolder;
import ru.terra.mail.core.domain.MailFoldersTree;
import ru.terra.mail.core.domain.MailMessage;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AbstractStorage {
    List<MailFolder> getAllFoldersTree();

    void storeFolders(List<MailFolder> mailFolders, String parentId);

    Set<MailMessage> getFolderMessages(final String folderGuid);

    void storeFolderMessageInFolder(final String folderId, final MailMessage m);

    Integer countMessagesInFolder(final String folderId);

    void loadFromFolder(final MailFolder folder);

    MailFoldersTree processFolder(final MailFoldersTree parent, final MailFolder mailFolder);

    List<MailFolder> merge(List<MailFolder> storedFolders,
                           List<MailFolder> serverFolders);

    void mergeFoldersTree(final List<MailFolder> storedFolders, final Map<String, MailFolder> serverFoldersMap);

    List<MailFolder> expandFoldersTree(final MailFolder mailFolder);
}
