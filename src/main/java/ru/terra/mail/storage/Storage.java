package ru.terra.mail.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.terra.mail.storage.db.controllers.FoldersController;
import ru.terra.mail.storage.db.controllers.MessagesController;
import ru.terra.mail.storage.db.entity.FolderEntity;
import ru.terra.mail.storage.db.entity.MessageEntity;
import ru.terra.mail.storage.domain.MailFolder;
import ru.terra.mail.storage.domain.MailMessage;

/**
 * Created by terranz on 18.10.16.
 */
public class Storage {

	private FoldersController foldersController = new FoldersController();
	private MessagesController messagesController = new MessagesController();

	public ObservableList<MailFolder> getRootFolders() throws Exception {
		Map<String, MailFolder> foldersMap = new HashMap<>();
		MailFolder inbox = null;
		Map<Integer, FolderEntity> storedFoldersMap = new HashMap<>();
		foldersController.list(true, -1, -1).stream().forEach(f -> {
			storedFoldersMap.put(f.getId(), f);
			foldersMap.put(f.getFullName(), new MailFolder(f));
		});
		for (FolderEntity fe : storedFoldersMap.values()) {
			if (fe.getParentFolderId() > -1) {
				FolderEntity parentEntity = storedFoldersMap.get(fe.getParentFolderId());
				MailFolder parentFolder = foldersMap.get(parentEntity.getFullName());
				MailFolder childFolder = foldersMap.get(fe.getFullName());
				parentFolder.getChildFolders().add(childFolder);
			} else
				inbox = foldersMap.get(fe.getFullName());
		}
		if (inbox == null)
			return FXCollections.emptyObservableList();
		return FXCollections.observableArrayList(inbox);
	}

	public void storeFolders(List<MailFolder> mailFolders) {
		mailFolders.forEach(f -> {
			try {
				FolderEntity folderEntity = foldersController.findByFullName(f.getFullName());
				if (folderEntity == null) {
					folderEntity = new FolderEntity(f, -1);
					foldersController.create(folderEntity);
				} else {
					folderEntity.setUnreadMessages(f.getUnreadMessages());
					foldersController.update(folderEntity);
				}
				storeFolders(f.getChildFolders(), folderEntity.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void storeFolders(List<MailFolder> mailFolders, Integer parentId) {
		mailFolders.forEach(f -> {
			try {
				FolderEntity folderEntity = foldersController.findByFullName(f.getFullName());
				if (folderEntity == null) {
					folderEntity = new FolderEntity(f, parentId);
					foldersController.create(folderEntity);
				} else {
					folderEntity.setUnreadMessages(f.getUnreadMessages());
					foldersController.update(folderEntity);
				}
				storeFolders(f.getChildFolders(), folderEntity.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public ObservableList<MailMessage> getFolderMessages(MailFolder mailFolder) {
		return FXCollections.observableArrayList(
				messagesController.findByFolderId(foldersController.findByFullName(mailFolder.getFullName()).getId())
						.stream().map(m -> new MailMessage(m, mailFolder)).collect(Collectors.toList()));
	}

	public void storeFolderMessages(MailFolder mailFolder, List<MailMessage> messages) {
		Integer parentId = foldersController.findByFullName(mailFolder.getFullName()).getId();
		messages.stream().map(m -> new MessageEntity(m, parentId)).forEach(m -> {
			try {
				messagesController.create(m);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public Integer countMessages(MailFolder mailFolder) {
		return messagesController.findByFolderId(foldersController.findByFullName(mailFolder.getFullName()).getId())
				.size();
	}
}
