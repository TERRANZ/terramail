package ru.terra.mail.gui.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn.CellDataFeatures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.terra.mail.config.Configuration;
import ru.terra.mail.config.StartUpParameters;
import ru.terra.mail.core.AbstractMailProtocol;
import ru.terra.mail.gui.controller.beans.FoldersTreeItem;
import ru.terra.mail.gui.controller.beans.MessagesTableItem;
import ru.terra.mail.gui.core.AbstractUIController;
import ru.terra.mail.gui.model.FoldersModel;
import ru.terra.mail.gui.model.MessagesModel;
import ru.terra.mail.storage.AbstractStorage;
import ru.terra.mail.storage.StorageSingleton;
import ru.terra.mail.storage.entity.MailFolder;
import ru.terra.mail.storage.entity.MailMessage;

import javax.mail.MessagingException;

import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import javafx.scene.control.TableColumn;

/**
 * Created by terranz on 18.10.16.
 */
public class MainWindow extends AbstractUIController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@FXML
	private TreeView<FoldersTreeItem> tvFolders;
	@FXML
	private Label lblStatus;
	@FXML
	private TableView<MessagesTableItem> tvMessages;
	@FXML
	WebView wvMailViewer;

	private TreeItem<FoldersTreeItem> treeRoot;
	private AbstractStorage storage = StorageSingleton.getInstance().getStorage();
	private AbstractMailProtocol protocol = Configuration.getInstance().getMailProtocol();
	private FoldersModel FoldersModel = new FoldersModel();
	private MessagesModel messagesModel = new MessagesModel();
	@FXML
	private TableColumn<MessagesTableItem, String> colSubject;
	@FXML
	private TableColumn<MessagesTableItem, String> colDate;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setColums();
		showFolders();
		setFolderSelectionEvents();
		setMessagesTableSelectionEvents();
	}

	private void setColums() {
		colSubject.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSubject()));
		colDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()));
	}

	private void setMessagesTableSelectionEvents() {
		tvMessages.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2)
					wvMailViewer.getEngine().loadContent(
							tvMessages.getSelectionModel().getSelectedItem().getMessage().getMessageBody());
			}
		});
	}

	private void setFolderSelectionEvents() {
		tvFolders.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {
					TreeItem<FoldersTreeItem> item = tvFolders.getSelectionModel().getSelectedItem();
					showMessages(item.getValue().getMailFolder());
				}
			}
		});
	}

	private void showFolders() {
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

	private void showMessages(MailFolder mailFolder) {
		updateStatus(mailFolder.getFullName());
		new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						List<MailMessage> folderMessages = messagesModel.getFolderMessages(mailFolder);
						if (folderMessages != null && folderMessages.size() > 0)
							Platform.runLater(() -> tvMessages.setItems(FXCollections.observableArrayList(
									folderMessages.stream().map(MessagesTableItem::new).collect(Collectors.toList()))));
						return null;
					}
				};
			}
		}.start();
	}

	private void updateStatus(String status) {
		Platform.runLater(() -> lblStatus.setText(status));
	}
}
