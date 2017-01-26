package ru.terra.mail.gui.controller;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.terra.mail.Main;
import ru.terra.mail.gui.StageHelper;
import ru.terra.mail.gui.controller.beans.FoldersTreeItem;
import ru.terra.mail.gui.controller.beans.MessagesTableItem;
import ru.terra.mail.gui.core.AbstractUIController;
import ru.terra.mail.gui.model.FoldersModel;
import ru.terra.mail.gui.model.MessagesModel;
import ru.terra.mail.storage.domain.MailFolder;
import ru.terra.mail.storage.domain.MailMessage;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by terranz on 18.10.16.
 */
public class MainWindow extends AbstractUIController {
    @FXML
    private WebView wvMailViewer;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @FXML
    private TreeView<FoldersTreeItem> tvFolders;
    @FXML
    private Label lblStatus;
    @FXML
    private TableView<MessagesTableItem> tvMessages;
    private TreeItem<FoldersTreeItem> treeRoot;
    @FXML
    private TableColumn<MessagesTableItem, String> colSubject;
    @FXML
    private TableColumn<MessagesTableItem, Date> colDate;

    private FoldersModel foldersModel;
    private MessagesModel messagesModel;
    private FullLoadService fullLoadService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        foldersModel = Main.getContext().getBean(FoldersModel.class);
        messagesModel = Main.getContext().getBean(MessagesModel.class);
        setColums();
        showFolders();
    }

    private void setColums() {
        colSubject.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSubject()));
        colDate.setCellFactory(param -> new TableCell<MessagesTableItem, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                if (item != null && !empty)
                    setText(messageDateFormat.format(item));
            }
        });
        colDate.setCellValueFactory(cellData -> {
            try {
                return new SimpleObjectProperty<>(cellData.getValue().getDate());
            } catch (Exception e) {
                logger.error("Error", e);
            }
            return new SimpleObjectProperty<>(new Date());
        });
        colDate.setComparator(Date::compareTo);
    }

    private void setMessagesTableSelectionEvents() {
        tvMessages.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                wvMailViewer.getEngine().loadContent("");
                MessagesTableItem item = tvMessages.getSelectionModel().getSelectedItem();
                if (item != null) {
                    MailMessage msg = item.getMessage();
                    wvMailViewer.setUserData(msg);
                    if (msg.getMessageBody() != null)
                        wvMailViewer.getEngine().loadContent(msg.getMessageBody());
                    else {
                        if (msg.getAttachments() != null && msg.getAttachments().size() > 0) {
                            msg.getAttachments().stream()
                                    .filter(attachment -> attachment.getType().contains("text/html"))
                                    .forEach(attachment -> wvMailViewer.getEngine()
                                            .loadContent(new String(attachment.getBody())));
                        }
                    }
                }
            }
        });
    }

    private void setFolderSelectionEvents() {
        tvFolders.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TreeItem<FoldersTreeItem> item = tvFolders.getSelectionModel().getSelectedItem();
                showMessages(item.getValue().getMailFolder());
                event.consume();
            }
        });
    }

    private void showFolders() {
        updateStatus("Folders loading...");
        treeRoot = foldersModel.getStoredFolders();
        treeRoot.setExpanded(true);
        if (treeRoot != null)
            tvFolders.setRoot(treeRoot);
        LoadFolderService loadFolderService = new LoadFolderService();
        loadFolderService.start();
        loadFolderService.setOnSucceeded((e) -> {
            updateStatus("Folders loaded");
            setFolderSelectionEvents();
            setMessagesTableSelectionEvents();
        });
    }

    private void showMessages(MailFolder mailFolder) {
        updateStatus(mailFolder.getFullName());
        updateStatus("Messages loading");
        ObservableSet<MailMessage> storedMessages = messagesModel.getStoredMessages(mailFolder);
        ObservableList<MessagesTableItem> displayItems = FXCollections.observableArrayList();
        if (storedMessages != null && storedMessages.size() > 0) {
            displayItems.addAll(storedMessages.stream().map(MessagesTableItem::new).collect(Collectors.toList()));
            tvMessages.setItems(displayItems);
        }
        LoadMessagesService loadMessagesService = new LoadMessagesService(mailFolder, storedMessages);
        loadMessagesService.start();
        loadMessagesService.setOnSucceeded((e) -> {
            displayItems.clear();
            displayItems.addAll(
                    loadMessagesService.getValue().stream().map(MessagesTableItem::new).collect(Collectors.toList()));
            tvMessages.setItems(displayItems);
            updateStatus("Messages loaded");
        });
        storedMessages.addListener((InvalidationListener) observable -> {
            displayItems.clear();
            displayItems.addAll(storedMessages.stream().map(MessagesTableItem::new).collect(Collectors.toList()));
            tvMessages.setItems(displayItems);
        });
    }

    private void updateStatus(String status) {
        Platform.runLater(() -> lblStatus.setText(status));
    }

    public void close(ActionEvent actionEvent) {
        currStage.close();
    }

    public void config(ActionEvent actionEvent) {

    }

    public void showSource(ActionEvent actionEvent) {
        MessagesTableItem selected = tvMessages.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        StageHelper.<MailSourceWindow>openWindow("w_source.fxml", "Source", false).getValue().loadMailSource(selected.getMessage().getHeaders());
    }

    public void showAttachments(ActionEvent actionEvent) {

    }

    public void fullDownload(ActionEvent actionEvent) {
        if (fullLoadService != null && !fullLoadService.isRunning()) {
            fullLoadService.start();
        }
    }

    private class LoadFolderService extends Service<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    treeRoot = foldersModel.getTreeRoot();
                    treeRoot.setExpanded(true);
                    Platform.runLater(() -> tvFolders.setRoot(treeRoot));
                    return null;
                }
            };
        }
    }

    private class LoadMessagesService extends Service<ObservableSet<MailMessage>> {
        private MailFolder mailFolder;
        private ObservableSet<MailMessage> storedMessages;

        public LoadMessagesService(MailFolder mailFolder, ObservableSet<MailMessage> displayedMessages) {
            this.mailFolder = mailFolder;
            this.storedMessages = displayedMessages;
        }

        @Override
        protected Task<ObservableSet<MailMessage>> createTask() {
            return new Task<ObservableSet<MailMessage>>() {
                @Override
                protected ObservableSet<MailMessage> call() throws Exception {
                    return messagesModel.getFolderMessages(mailFolder, storedMessages);
                }
            };
        }
    }

    private class FullLoadService extends Service<Void> {

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    foldersModel.getFolders().forEach(f -> messagesModel.getFolderMessages(f, FXCollections.emptyObservableSet()));
                    return null;
                }
            };
        }
    }
}
