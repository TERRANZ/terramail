package ru.terra.mail.core.domain;

import com.beust.jcommander.internal.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.terra.mail.storage.ModificationNotify;
import ru.terra.mail.storage.db.entity.FolderEntity;

import javax.mail.Folder;
import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by terranz on 18.10.16.
 */
@Data
@NoArgsConstructor
@ToString
public class MailFolder {
    private UUID guid;
    private List<MailFolder> childFolders = new ArrayList<>();
    private String name;
    private String fullName;
    private Integer unreadMessages = 0;
    private Folder folder;
    private Boolean deleted;
    private List<ModificationNotify> subscribers = Lists.newArrayList();

    public MailFolder(Folder folder) {
        this.name = folder.getName();
        this.fullName = folder.getFullName();
        this.childFolders = new ArrayList<>();
        this.folder = folder;
        try {
            this.unreadMessages = folder.getUnreadMessageCount();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public MailFolder(FolderEntity f) {
        this.name = f.getName();
        this.fullName = f.getFullName();
        this.childFolders = new ArrayList<>();
        this.guid = f.getGuid();
        this.deleted = f.getDeleted();
        this.unreadMessages = f.getUnreadMessages();
    }

    public void notifyModified() {
        subscribers.forEach(m -> m.onModified(this));
    }
}
