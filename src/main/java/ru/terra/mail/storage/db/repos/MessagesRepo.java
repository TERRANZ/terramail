package ru.terra.mail.storage.db.repos;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.terra.mail.storage.db.entity.MessageEntity;

import java.util.List;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
public interface MessagesRepo extends PagingAndSortingRepository<MessageEntity, String> {
    List<MessageEntity> findByFolderId(String folderId);

    MessageEntity findByCreateDate(Long createDate);

    @Query(value = "select count(guid) from message_entity where folder_id=?1", nativeQuery = true)
    Integer countByFolderId(String folderId);
}
