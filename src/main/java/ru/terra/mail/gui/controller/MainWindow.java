package ru.terra.mail.gui.controller;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.terra.mail.config.Configuration;
import ru.terra.mail.config.StartUpParameters;
import ru.terra.mail.core.AbstractMailProtocol;
import ru.terra.mail.gui.controller.beans.FoldersTreeItem;
import ru.terra.mail.gui.core.AbstractUIController;
import ru.terra.mail.gui.model.FoldersModel;
import ru.terra.mail.storage.AbstractStorage;
import ru.terra.mail.storage.StorageSingleton;
import ru.terra.mail.storage.entity.MailFolder;

import javax.mail.MessagingException;

import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.input.MouseEvent;

/**
 * Created by terranz on 18.10.16.
 */
public class MainWindow extends AbstractUIController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@FXML
	private TreeView<FoldersTreeItem> tvFolders;
	@FXML
	private Label lblStatus;

	private TreeItem<FoldersTreeItem> treeRoot;
	private AbstractStorage storage = StorageSingleton.getInstance().getStorage();
	private AbstractMailProtocol protocol = Configuration.getInstance().getMailProtocol();
	private FoldersModel FoldersModel = new FoldersModel();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadFolders();
		setFolderSelectionEvents();
	}

	private void setFolderSelectionEvents() {
		tvFolders.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {
					TreeItem<FoldersTreeItem> item = tvFolders.getSelectionModel().getSelectedItem();
					showFolder(item.getValue().getMailFolder());
				}
			}
		});
	}

	private void loadFolders() {
		new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {						
						treeRoot = FoldersModel.getTreeRoot();
						Platform.runLater(() -> tvFolders.setRoot(treeRoot));
						return null;
					}
				};
			}
		}.start();
	}



	private void performLogin() throws GeneralSecurityException, MessagingException {
		protocol.login(StartUpParameters.getInstance().getUser(), StartUpParameters.getInstance().getPass(),
				StartUpParameters.getInstance().getServ());
	}

	private void showFolder(MailFolder mailFolder) {
		updateStatus(mailFolder.getFullName());
	}

	private void updateStatus(String status) {
		Platform.runLater(() -> lblStatus.setText(status));
	}
}
