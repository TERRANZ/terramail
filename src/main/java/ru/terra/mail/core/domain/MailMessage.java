package ru.terra.mail.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.terra.mail.storage.db.entity.MessageEntity;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.*;

/**
 * Created by terranz on 18.10.16.
 */
@Data
public class MailMessage {
    private Date createDate;
    private String subject;
    private String from;
    private String to;
    private String messageBody;
    private List<MailMessageAttachment> attachments;
    private UUID guid;
    private UUID folderGuid;
    @JsonIgnore
    private Message message;
    private String headers;

    public MailMessage() {
        guid = UUID.randomUUID();
    }

    public MailMessage(final MessageEntity me, final UUID folderGuid) {
        this.guid = UUID.randomUUID();
        this.folderGuid = folderGuid;
        this.subject = me.getSubject();
        this.createDate = new Date(me.getCreateDate());
        this.from = me.getFrom();
        this.to = me.getTo();
        this.messageBody = me.getMessageBody();
        this.attachments = new ArrayList<>();
        this.headers = me.getHeaders();
    }

    public MailMessage(final Message msg, final UUID folderGuid) {
//        LoggerFactory.getLogger(this.getClass()).info("Generating mail message from transport message");
        this.guid = UUID.randomUUID();
        this.folderGuid = folderGuid;
        try {
            this.subject = msg.getSubject();
            this.createDate = msg.getReceivedDate();
            this.from = msg.getFrom()[0].toString();
            if (msg.getRecipients(Message.RecipientType.TO) != null && msg.getRecipients(Message.RecipientType.TO).length > 0)
                this.to = msg.getRecipients(Message.RecipientType.TO)[0].toString();
            Enumeration iter = msg.getAllHeaders();
            headers = "";
            while (iter.hasMoreElements()) {
                Header h = (Header) iter.nextElement();
                headers += h.getName() + " : " + h.getValue() + "\n";
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        this.message = msg;
        this.attachments = new ArrayList<>();
    }
}
