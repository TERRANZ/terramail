package ru.terra.mail.storage.db.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.terra.mail.storage.db.entity.AttachmentEntity;

import java.util.List;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
public interface AttachmentsRepo extends JpaRepository<AttachmentEntity, String> {
    List<AttachmentEntity> findByMessageId(String guid);
}
