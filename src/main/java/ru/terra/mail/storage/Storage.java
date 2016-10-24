package ru.terra.mail.storage;

import ru.terra.mail.storage.entity.MailFolder;
import ru.terra.mail.storage.entity.MailMessage;

import java.util.List;

import javafx.collections.ObservableList;

/**
 * Created by terranz on 18.10.16.
 */
public class Storage {

	public ObservableList<MailFolder> getRootFolders() {
		return null;
	}

	public void storeFolders(List<MailFolder> mailFolders) {

	}

	public ObservableList<MailMessage> getFolderMessages(MailFolder mailFolder) {
		return null;
	}

	public void storeFolderMessages(MailFolder mailFolder, List<MailMessage> messages) {
	}

	public Integer countMessages(MailFolder mailFolder) {
		return 0;
	}
}
