package ru.terra.mail.gui.model;

import org.springframework.stereotype.Component;
import ru.terra.mail.core.domain.MailFolder;
import ru.terra.mail.core.domain.MailMessage;

import java.util.Set;

/**
 * Created by terranz on 19.10.16.
 */
@Component
public class MessagesModel extends AbstractModel<MailMessage> {
    public Set<MailMessage> getStoredMessages(String folderGuid) {
        return getStorage().getFolderMessages(folderGuid);
    }

    public Set<MailMessage> getFolderMessages(MailFolder folder, Set<MailMessage> stored) {
        getStorage().loadFromFolder(folder);
        return stored;
    }

    public void loadFromFolder(MailFolder folder) {
        getStorage().loadFromFolder(folder);
    }
}
