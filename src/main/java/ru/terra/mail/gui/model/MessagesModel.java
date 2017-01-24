package ru.terra.mail.gui.model;

import javafx.collections.ObservableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.terra.mail.storage.ModificationObserver;
import ru.terra.mail.storage.domain.MailFolder;
import ru.terra.mail.storage.domain.MailMessage;

/**
 * Created by terranz on 19.10.16.
 */
@Component
public class MessagesModel extends AbstractModel<MailMessage> {
    @Autowired
    private ModificationObserver modificationObserver;

    public ObservableSet<MailMessage> getStoredMessages(MailFolder folder) {
        return getStorage().getFolderMessages(folder);
    }

    public ObservableSet<MailMessage> getFolderMessages(MailFolder folder, ObservableSet<MailMessage> stored) {
        modificationObserver.startObserve(stored, folder);
        getStorage().loadFromFolder(folder);
        return stored;
    }
}
