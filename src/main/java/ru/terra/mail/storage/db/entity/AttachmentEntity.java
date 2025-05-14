package ru.terra.mail.storage.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.terra.mail.core.domain.MailMessageAttachment;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
@Entity
@Data
@NoArgsConstructor
public class AttachmentEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "system-uuid")
    private UUID guid;
    @Lob
    private String type;
    @Lob
    private String fileName;
    @Lob
    private UUID messageId;
    @Lob
    private String localFileName;

    public AttachmentEntity(final MailMessageAttachment mma, final UUID parentId) {
        this.messageId = parentId;
        this.type = mma.getType();
        this.fileName = mma.getFileName();
        this.localFileName = mma.getLocalFileName();
    }
}
