package ru.terra.mail.gui.model;

import javafx.collections.ObservableList;
import ru.terra.mail.storage.ModificationObserver;
import ru.terra.mail.storage.domain.MailFolder;
import ru.terra.mail.storage.domain.MailMessage;

/**
 * Created by terranz on 19.10.16.
 */
public class MessagesModel extends AbstractModel<MailMessage> {

    public ObservableList<MailMessage> getStoredMessages(MailFolder folder) {
        return storage.getFolderMessages(folder);
    }

    public ObservableList<MailMessage> getFolderMessages(MailFolder folder, ObservableList<MailMessage> stored) {
        ModificationObserver.getInstance().startObserve(stored, folder);
        storage.loadFromFolder(folder);
        return stored;
    }
}
