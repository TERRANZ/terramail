package ru.terra.mail.storage.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.terra.mail.core.domain.MailMessage;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */
@Entity
@Table(name = "message_entity",
        indexes = {@Index(columnList = "createDate")}
)
@Data
@NoArgsConstructor
@ToString
public class MessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "system-uuid")
    private UUID guid;
    private Long createDate;
    @Lob
    private String subject;
    @Lob
    @Column(name = "from_address")
    private String from;
    @Lob
    @Column(name = "to_address")
    private String to;
    @Lob
    private String messageBody;
    @Lob
    private String headers;
    @Column(name = "folder_id")
    private UUID folderId;

    public MessageEntity(final MailMessage m, final UUID folderId) {
        this.createDate = m.getCreateDate().getTime();
        this.subject = m.getSubject();
        this.from = m.getFrom();
        this.to = m.getTo();
        this.messageBody = m.getMessageBody();
        this.folderId = folderId;
        this.headers = m.getHeaders();
    }
}
