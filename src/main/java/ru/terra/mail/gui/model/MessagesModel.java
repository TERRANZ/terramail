package ru.terra.mail.gui.model;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import ru.terra.mail.storage.ModificationObserver;
import ru.terra.mail.storage.domain.MailFolder;
import ru.terra.mail.storage.domain.MailMessage;

/**
 * Created by terranz on 19.10.16.
 */
public class MessagesModel extends AbstractModel<MailMessage> {

    public ObservableSet<MailMessage> getStoredMessages(MailFolder folder) {
        return storage.getFolderMessages(folder);
    }

    public ObservableSet<MailMessage> getFolderMessages(MailFolder folder, ObservableSet<MailMessage> stored) {
        ModificationObserver.getInstance().startObserve(stored, folder);
        storage.loadFromFolder(folder);
        return stored;
    }
}
