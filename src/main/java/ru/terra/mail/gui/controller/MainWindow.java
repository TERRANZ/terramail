package ru.terra.mail.gui.controller;

import com.sun.mail.imap.IMAPMessage;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.terra.mail.gui.StageHelper;
import ru.terra.mail.gui.controller.beans.FoldersTreeItem;
import ru.terra.mail.gui.controller.beans.MessagesTableItem;
import ru.terra.mail.gui.core.AbstractUIController;
import ru.terra.mail.gui.model.FoldersModel;
import ru.terra.mail.gui.model.MessagesModel;
import ru.terra.mail.storage.entity.MailFolder;
import ru.terra.mail.storage.entity.MailMessage;

import javax.mail.Header;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by terranz on 18.10.16.
 */
public class MainWindow extends AbstractUIController {
    @FXML
    WebView wvMailViewer;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @FXML
    private TreeView<FoldersTreeItem> tvFolders;
    @FXML
    private Label lblStatus;
    @FXML
    private TableView<MessagesTableItem> tvMessages;
    private TreeItem<FoldersTreeItem> treeRoot;
    private FoldersModel foldersModel = new FoldersModel();
    private MessagesModel messagesModel = new MessagesModel();
    @FXML
    private TableColumn<MessagesTableItem, String> colSubject;
    @FXML
    private TableColumn<MessagesTableItem, Date> colDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setColums();
        showFolders();
        setFolderSelectionEvents();
        setMessagesTableSelectionEvents();
    }

    private void setColums() {
        colSubject.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSubject()));
        colDate.setCellFactory(
                param -> {
                    TableCell<MessagesTableItem, Date> cell = new TableCell<MessagesTableItem, Date>() {
                        @Override
                        protected void updateItem(Date item, boolean empty) {
                            if (item != null && !empty)
                                setText(messageDateFormat.format(item));
                        }
                    };
                    return cell;
                });
        colDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()));
        colDate.setComparator((o1, o2) -> o1.compareTo(o2));
    }

    private void setMessagesTableSelectionEvents() {
        tvMessages.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                MessagesTableItem item = tvMessages.getSelectionModel().getSelectedItem();
                if (item != null) {
                    String body = "";
                    MailMessage msg = item.getMessage();
                    wvMailViewer.setUserData(msg);
                    if (msg.getMessageBody() != null)
                        wvMailViewer.getEngine().loadContent(msg.getMessageBody());
                    else {
                        if (msg.getAttachments().size() > 0) {
                            msg.getAttachments()
                                    .stream()
                                    .filter(attachment -> attachment.getType().contains("text/plain"))
                                    .forEach(attachment ->
                                            wvMailViewer.getEngine().loadContent(new String(attachment.getBody()))
                                    );
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
        updateStatus("Folders loadeding...");
        treeRoot = foldersModel.getStoredFolders();
        treeRoot.setExpanded(true);
        if (treeRoot != null)
            tvFolders.setRoot(treeRoot);
        LoadFolderService loadFolderService = new LoadFolderService();
        loadFolderService.start();
        loadFolderService.setOnSucceeded((e) -> updateStatus("Folders loaded"));
    }

    private void showMessages(MailFolder mailFolder) {
        updateStatus(mailFolder.getFullName());
        updateStatus("Messages loading");
        List<MailMessage> storedMessages = messagesModel.getStoredMessages(mailFolder);
        if (storedMessages != null && storedMessages.size() > 0)
            tvMessages.setItems(FXCollections.observableArrayList(
                    storedMessages.stream().map(MessagesTableItem::new).collect(Collectors.toList())));
        LoadMessagesService loadMessagesService = new LoadMessagesService(mailFolder);
        loadMessagesService.start();
        loadMessagesService.setOnSucceeded((e) -> {
            tvMessages.setItems(FXCollections.observableArrayList(
                    loadMessagesService.getValue().stream().map(MessagesTableItem::new).collect(Collectors.toList())));
            updateStatus("Messages loaded");
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
        MailMessage msg = (MailMessage) wvMailViewer.getUserData();
        if (msg.getMessage() == null)
            return;
        String source = "";
        try {
            Enumeration iter = msg.getMessage().getAllHeaders();
            while (iter.hasMoreElements()) {
                Header h = (Header) iter.nextElement();
                source += h.getName() + " : " + h.getValue() + "\n";
            }
            source += IOUtils.toString(((IMAPMessage) msg.getMessage()).getRawInputStream(), ((IMAPMessage) msg.getMessage()).getEncoding());
        } catch (Exception e) {
            logger.error("Unable to parse message", e);
        }
        StageHelper.<MailSourceWindow>openWindow("w_source.fxml", "Source", false).getValue().loadMailSource(source);
    }

    public void showAttachments(ActionEvent actionEvent) {

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

    private class LoadMessagesService extends Service<List<MailMessage>> {
        private MailFolder mailFolder;

        public LoadMessagesService(MailFolder mailFolder) {
            this.mailFolder = mailFolder;
        }

        @Override
        protected Task<List<MailMessage>> createTask() {
            return new Task<List<MailMessage>>() {
                @Override
                protected List<MailMessage> call() throws Exception {
                    return messagesModel.getFolderMessages(mailFolder);
                }
            };
        }
    }
}
