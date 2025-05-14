package ru.terra.mail.storage.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.terra.mail.core.domain.MailFolder;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
@Entity
@Data
@NoArgsConstructor
public class FolderEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "system-uuid")
    private UUID guid;
    @Lob
    private String name;
    @Lob
    private String fullName;
    private Integer unreadMessages = 0;
    private Boolean deleted;
    private UUID parentFolderId;

    public FolderEntity(final MailFolder f, final UUID parentFolderId) {
        this.name = f.getName();
        this.fullName = f.getFullName();
        this.unreadMessages = f.getUnreadMessages();
        this.deleted = f.getDeleted();
        this.parentFolderId = parentFolderId;
    }

}
