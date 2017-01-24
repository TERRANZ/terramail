package ru.terra.mail.storage.db.repos;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ru.terra.mail.storage.db.entity.MessageEntity;

import java.util.Date;
import java.util.List;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
public interface MessagesRepo extends ElasticsearchRepository<MessageEntity, String> {
    List<MessageEntity> findByFolderId(String folderId);

    @Query("{\"bool\" : {\"must\" : {\"term\" : {\"createDate\" : \"?0\"}}}}")
    MessageEntity findByCreateDate(Date createDate);
}
