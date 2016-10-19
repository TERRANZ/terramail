package ru.terra.mail.gui.model;

import ru.terra.mail.storage.entity.MailFolder;
import ru.terra.mail.storage.entity.MailMessage;

import javax.mail.Folder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by terranz on 19.10.16.
 */
public class MessagesModel extends AbstractModel<MailMessage> {

    public List<MailMessage> getStoredMessages(MailFolder folder) {
        return storage.getFolderMessages(folder);
    }

    public List<MailMessage> getFolderMessages(MailFolder folder) {
        List<MailMessage> stored = storage.getFolderMessages(folder);
        if (stored == null) {
            stored = loadFromFolder(folder);
        } else {
            List<MailMessage> loaded = loadFromFolder(folder);
            List<MailMessage> toAdd = new ArrayList<>();
            List<MailMessage> toDel = new ArrayList<>();
            for (MailMessage loadedMessage : loaded) {
                MailMessage storedMessage = messageExists(loadedMessage.getCreateDate().getTime(), stored);
                if (storedMessage == null)
                    toAdd.add(loadedMessage);
            }
            for (MailMessage storedMessage : stored)
                if (messageExists(storedMessage.getCreateDate().getTime(), loaded) == null)
                    toDel.add(storedMessage);
        }

        storage.storeFolderMessages(folder, stored);
        return stored;
    }

    private MailMessage messageExists(long date, List<MailMessage> messages) {
        for (MailMessage m : messages)
            if (m.getCreateDate().getTime() == date)
                return m;
        return null;
    }

    private List<MailMessage> loadFromFolder(MailFolder folder) {
        List<MailMessage> ret = new ArrayList<>();
        try {
            folder.getFolder().open(Folder.READ_ONLY);
            ret.addAll(Arrays.stream(folder.getFolder().getMessages())
                    .map(m -> new MailMessage(m, folder)).collect(Collectors.toList()));
        } catch (Exception e) {
            logger.error("Unable to load messages from server", e);
        }
        return ret;
    }
}
