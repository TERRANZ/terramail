package ru.terra.mail.gui.model;

import java.security.GeneralSecurityException;
import java.util.List;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.TreeItem;
import ru.terra.mail.config.Configuration;
import ru.terra.mail.config.StartUpParameters;
import ru.terra.mail.core.AbstractMailProtocol;
import ru.terra.mail.gui.controller.beans.FoldersTreeItem;
import ru.terra.mail.storage.AbstractStorage;
import ru.terra.mail.storage.StorageSingleton;
import ru.terra.mail.storage.entity.MailFolder;

/**
 * Created by terranz on 19.10.16.
 */
public class FoldersModel {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private AbstractStorage storage = StorageSingleton.getInstance().getStorage();
	private AbstractMailProtocol protocol = Configuration.getInstance().getMailProtocol();
	private TreeItem<FoldersTreeItem> treeRoot;

	public TreeItem<FoldersTreeItem> getTreeRoot() {
		List<MailFolder> folders = getFolders();
		treeRoot = new TreeItem<>(new FoldersTreeItem(folders.get(0)));
		folders.get(0).getChildFolders().forEach(cf -> processFolder(treeRoot, cf));	
		return treeRoot;
	}

	public List<MailFolder> getFolders() {
		List<MailFolder> storedFolders = storage.getRootFolders();
		if (storedFolders == null) {
			boolean loggedIn = false;
			try {
				performLogin();
				loggedIn = true;
			} catch (Exception e) {
				logger.error("Unable to login", e);
			}
			if (loggedIn) {
				try {
					storedFolders = protocol.listFolders();
					mergeFolders(storedFolders);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}
		return storedFolders;
	}

	private void mergeFolders(List<MailFolder> folders) {
		//TODO: update only changed folders
		storage.storeFolders(folders);
	}

	private TreeItem<FoldersTreeItem> processFolder(TreeItem<FoldersTreeItem> parent, MailFolder mailFolder) {
		TreeItem<FoldersTreeItem> ret = new TreeItem<>(new FoldersTreeItem(mailFolder));
		parent.getChildren().add(ret);
		mailFolder.getChildFolders().forEach(cf -> processFolder(ret, cf));
		return ret;
	}

	private void performLogin() throws GeneralSecurityException, MessagingException {
		protocol.login(StartUpParameters.getInstance().getUser(), StartUpParameters.getInstance().getPass(),
				StartUpParameters.getInstance().getServ());
	}
}
