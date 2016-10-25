package ru.terra.mail.core;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.terra.mail.storage.domain.MailFolder;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by terranz on 18.10.16.
 */
public abstract class AbstractMailProtocol {
	protected Store store;

	public abstract void login(String user, String pass, String server)
			throws MessagingException, GeneralSecurityException;

	public ObservableList<MailFolder> listFolders() throws MessagingException {
		ObservableList<MailFolder> ret = FXCollections.observableArrayList();
		Arrays.stream(store.getPersonalNamespaces()).map(f -> {
			try {
				return getFolders(f);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			return null;
		}).forEach(e -> ret.add(e));
		return ret;
	}

	protected MailFolder getFolders(Folder folder) throws MessagingException {
		MailFolder mailFolder = new MailFolder(folder);
		if (folder.list().length > 0) {
			Arrays.stream(folder.list()).forEach(f -> {
				try {
					mailFolder.getChildFolders().add(getFolders(f));
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			});
		}
		return mailFolder;
	}
}
