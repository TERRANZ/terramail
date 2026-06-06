package com.terramail.repository;

import com.terramail.model.AttachmentInfo;
import com.terramail.model.Message;
import com.terramail.model.SortOrder;

import java.util.List;

public interface MessageRepository {

    List<Message> findByFolderId(long folderId, SortOrder sortOrder);

    Message findById(long id);

    List<Message> findByAccountId(long accountId);

    Message save(Message message);

    void deleteById(long id);

    void updateSeen(long id, boolean seen);

    void updateFlagged(long id, boolean flagged);

    void updateAttachments(long messageId, List<AttachmentInfo> attachments);

    long countByFolderId(long folderId);

    List<Message> searchBySubject(long folderId, String keyword, SortOrder sortOrder);
}
