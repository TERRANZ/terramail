package ru.terra.mail.gui.model;

import javafx.collections.ObservableList;
import org.apache.commons.io.IOUtils;
import ru.terra.mail.storage.ModificationObserver;
import ru.terra.mail.storage.domain.MailFolder;
import ru.terra.mail.storage.domain.MailMessage;
import ru.terra.mail.storage.domain.MailMessageAttachment;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.internet.MimeMultipart;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by terranz on 19.10.16.
 */
public class MessagesModel extends AbstractModel<MailMessage> {

    private ExecutorService service = Executors.newFixedThreadPool(1);

    public ObservableList<MailMessage> getStoredMessages(MailFolder folder) {
        return storage.getFolderMessages(folder);
    }

    public ObservableList<MailMessage> getFolderMessages(MailFolder folder) {
        ObservableList<MailMessage> stored = storage.getFolderMessages(folder);
        ModificationObserver.getInstance().startObserve(stored, folder);

        // if (stored == null || stored.size() == 0) {
        // stored = loadFromFolder(folder);
        // } else {
        // List<MailMessage> loaded = loadFromFolder(folder);
        // if (loaded == null)
        // return stored;
        // List<MailMessage> toAdd = new ArrayList<>();
        // List<MailMessage> toDel = new ArrayList<>();
        // for (MailMessage loadedMessage : loaded) {
        // MailMessage storedMessage =
        // messageExists(loadedMessage.getCreateDate().getTime(), stored);
        // if (storedMessage == null)
        // toAdd.add(loadedMessage);
        // }
        // for (MailMessage storedMessage : stored)
        // if (messageExists(storedMessage.getCreateDate().getTime(), loaded) ==
        // null)
        // toDel.add(storedMessage);
        //
        // stored.addAll(toAdd);
        // stored.removeAll(toDel);
        // }
        //
        // storage.storeFolderMessages(folder, stored);
        service.submit(() -> loadFromFolder(folder));
        return stored;
    }

    private MailMessage messageExists(long date, List<MailMessage> messages) {
        for (MailMessage m : messages)
            if (m.getCreateDate().getTime() == date)
                return m;
        return null;
    }

    private void loadFromFolder(MailFolder folder) {
        if (folder.getFolder() == null)
            return;
        try {
            if (!folder.getFolder().isOpen())
                folder.getFolder().open(Folder.READ_ONLY);
            storage.storeFolderMessages(folder, Arrays.stream(folder.getFolder().getMessages()).map(m -> {
                MailMessage msg = new MailMessage(m, folder);
                processMailMessage(msg);
                return msg;
            }).collect(Collectors.toList()));
        } catch (Exception e) {
            logger.error("Unable to load messages from server", e);
        }
    }

    private void processMailMessage(MailMessage mm) {
        Message msg = mm.getMessage();
        try {
            if (msg.getContent() instanceof MimeMultipart) {
                MimeMultipart multipart = (MimeMultipart) msg.getContent();
                for (int j = 0; j < multipart.getCount(); j++) {
                    BodyPart bodyPart = multipart.getBodyPart(j);
                    DataHandler handler = bodyPart.getDataHandler();
                    mm.getAttachments().add(new MailMessageAttachment(IOUtils.toByteArray(handler.getInputStream()),
                            handler.getContentType(), handler.getName()));
                }
            } else
                mm.setMessageBody(msg.getContent().toString());
        } catch (Exception e) {
            logger.error("Unable to process mail message", e);
        }
    }
}
