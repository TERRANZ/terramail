package ru.terra.mail.storage.db.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.terra.mail.storage.db.entity.FolderEntity;

import java.util.UUID;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
public interface FoldersRepo extends JpaRepository<FolderEntity, UUID> {
    FolderEntity findByFullName(String fullName);
}
