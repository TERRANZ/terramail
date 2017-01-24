package ru.terra.mail.storage.db.repos;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ru.terra.mail.storage.db.entity.FolderEntity;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
public interface FoldersRepo extends ElasticsearchRepository<FolderEntity, String> {
    FolderEntity findByFullName(String fullName);
}
