package ru.terra.mail.storage;

import ru.terra.mail.core.domain.MailFolder;
import ru.terra.mail.core.domain.MailFoldersTree;
import ru.terra.mail.core.domain.MailMessage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface AbstractStorage {
    List<MailFolder> getAllFoldersTree();

    void storeFolders(List<MailFolder> mailFolders, UUID parentId);

    Set<MailMessage> getFolderMessages(final UUID folderGuid);

    void storeFolderMessageInFolder(final UUID folderId, final MailMessage m);

    Integer countMessagesInFolder(final UUID folderId);

    void loadFromFolder(final MailFolder folder);

    MailFoldersTree processFolder(final MailFoldersTree parent, final MailFolder mailFolder);

    List<MailFolder> merge(List<MailFolder> storedFolders,
                           List<MailFolder> serverFolders);

    void mergeFoldersTree(final List<MailFolder> storedFolders, final Map<String, MailFolder> serverFoldersMap);

    List<MailFolder> expandFoldersTree(final MailFolder mailFolder);
}
