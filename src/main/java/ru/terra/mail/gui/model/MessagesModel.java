package ru.terra.mail.gui.model;

import javafx.collections.ObservableSet;
import org.springframework.stereotype.Component;
import ru.terra.mail.core.domain.MailFolder;
import ru.terra.mail.core.domain.MailMessage;

/**
 * Created by terranz on 19.10.16.
 */
@Component
public class MessagesModel extends AbstractModel<MailMessage> {
    public ObservableSet<MailMessage> getStoredMessages(String folderGuid) {
        return getStorage().getFolderMessages(folderGuid);
    }

    public ObservableSet<MailMessage> getFolderMessages(MailFolder folder, ObservableSet<MailMessage> stored) {
        getStorage().loadFromFolder(folder);
        return stored;
    }

    public void loadFromFolder(MailFolder folder) {
        getStorage().loadFromFolder(folder);
    }
}
