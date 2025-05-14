package ru.terra.mail.storage.db.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.terra.mail.storage.db.entity.MessageEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
public interface MessagesRepo extends JpaRepository<MessageEntity, UUID> {
    List<MessageEntity> findByFolderId(UUID folderId);

    MessageEntity findByCreateDate(Long createDate);

    @Query(value = "select count(id) from message_entity where folder_id=?1", nativeQuery = true)
    Integer countByFolderId(UUID folderId);

    @Query(value = "from MessageEntity as me where createDate in :dates")
    List<MessageEntity> findByDatesInList(@Param("dates") Set<Long> dates);
}
