package ru.terra.mail.storage.db.repos;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ru.terra.mail.storage.db.entity.AttachmentEntity;

import java.util.List;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
public interface AttachmentsRepo extends ElasticsearchRepository<AttachmentEntity, String> {
    List<AttachmentEntity> findByMessageId(String guid);
}
