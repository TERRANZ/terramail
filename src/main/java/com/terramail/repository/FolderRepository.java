package com.terramail.repository;

import com.terramail.model.Folder;

import java.util.List;

public interface FolderRepository {

    List<Folder> findByAccountId(long accountId);

    Folder findById(long id);

    Folder save(Folder folder);

    void deleteById(long id);

    Folder findByName(long accountId, String name);

    long countByAccountId(long accountId);

    List<Folder> findByAccountIdAndParentFolderId(long accountId, long parentFolderId);
}
