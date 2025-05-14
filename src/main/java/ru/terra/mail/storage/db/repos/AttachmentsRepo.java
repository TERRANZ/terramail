package ru.terra.mail.storage.db.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.terra.mail.storage.db.entity.AttachmentEntity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
public interface AttachmentsRepo extends JpaRepository<AttachmentEntity, UUID> {
    List<AttachmentEntity> findByMessageId(UUID guid);
}
