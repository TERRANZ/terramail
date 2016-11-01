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

	public ObservableList<MailMessage> getStoredMessages(MailFolder folder) {
		return storage.getFolderMessages(folder);
	}

	public ObservableList<MailMessage> getFolderMessages(MailFolder folder, ObservableList<MailMessage> stored) {
		ModificationObserver.getInstance().startObserve(stored, folder);
		storage.loadFromFolder(folder);
		return stored;
	}
}
