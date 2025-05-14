package ru.terra.mail.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.terra.mail.storage.db.entity.AttachmentEntity;

/**
 * Created by terranz on 20.10.16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailMessageAttachment {
    private String type;
    private String fileName;
    private Boolean downloaded;
    private String localFileName;

    public MailMessageAttachment(final AttachmentEntity a) {
        this.type = a.getType();
        this.fileName = a.getFileName();
    }

}
