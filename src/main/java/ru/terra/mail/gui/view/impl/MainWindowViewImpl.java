package ru.terra.mail.gui.view.impl;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.terra.mail.core.domain.MailFoldersTree;
import ru.terra.mail.core.domain.MailMessage;
import ru.terra.mail.gui.StageHelper;
import ru.terra.mail.gui.controller.MainWindowController;
import ru.terra.mail.gui.core.AbstractUIView;
import ru.terra.mail.gui.view.MailSourceWindow;
import ru.terra.mail.gui.view.MainWindowView;
import ru.terra.mail.gui.view.beans.FoldersTreeItem;
import ru.terra.mail.gui.view.beans.MessagesTableItem;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by terranz on 18.10.16.
 */
public class MainWindowViewImpl extends AbstractUIView implements MainWindowView {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @FXML
    private WebView wvMailViewer;
    @FXML
    private TreeView<FoldersTreeItem> tvFolders;
    @FXML
    private Label lblStatus;
    @FXML
    private TableView<MessagesTableItem> tvMessages;
    @FXML
    private TableColumn<MessagesTableItem, String> colSubject;
    @FXML
    private TableColumn<MessagesTableItem, Date> colDate;

    private TreeItem<FoldersTreeItem> treeRoot;
    private MainWindowController controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller = new MainWindowController(this);
        setColums();

        controller.initialize();

        setFolderSelectionEvents();
        setMessagesTableSelectionEvents();
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
                MessagesTableItem item = tvMessages.getSelectionModel().getSelectedItem();
                if (item != null) {
                    controller.showMessage(item.getMessage());
                }
            }
        });
    }

    private void setFolderSelectionEvents() {
        tvFolders.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TreeItem<FoldersTreeItem> item = tvFolders.getSelectionModel().getSelectedItem();
                controller.folderSelected(item.getValue().getMailFolder());
                event.consume();
            }
        });
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
        controller.fullDownload();
    }

    @Override
    public void setStatusText(final String status) {
        lblStatus.setText(status);
    }

    @Override
    public void setFoldersTreeRoot(final MailFoldersTree mailFoldersTree) {
        treeRoot = new TreeItem<>(new FoldersTreeItem(mailFoldersTree.getCurrentFolder()));
        mailFoldersTree.getChildrens().forEach(mft -> processFolders(treeRoot, mft));
        tvFolders.setRoot(treeRoot);
    }

    private void processFolders(final TreeItem<FoldersTreeItem> rootItem, MailFoldersTree mailFoldersTree) {
        rootItem.setExpanded(true);
        final TreeItem<FoldersTreeItem> treeItem = new TreeItem<>(new FoldersTreeItem(mailFoldersTree.getCurrentFolder()));
        mailFoldersTree.getChildrens().forEach(mft -> processFolders(treeItem, mft));
        rootItem.getChildren().add(treeItem);
    }

    @Override
    public void runOnUIThread(final Runnable runnable) {
        Platform.runLater(runnable);
    }

    @Override
    public void showMessagesList(final Set<MailMessage> storedMessages) {
        tvMessages.getItems().clear();
        ObservableList<MessagesTableItem> displayItems = FXCollections.observableArrayList();
        if (storedMessages != null && storedMessages.size() > 0) {
            displayItems.addAll(storedMessages.stream().map(MessagesTableItem::new).collect(Collectors.toList()));
            tvMessages.setItems(displayItems);
        }
    }

    @Override
    public void showMailMesage(final MailMessage msg) {
        wvMailViewer.getEngine().loadContent("");

        if (msg != null) {
            wvMailViewer.setUserData(msg);
            if (msg.getMessageBody() != null)
                wvMailViewer.getEngine().loadContent(msg.getMessageBody());
            else {
                if (msg.getAttachments() != null && msg.getAttachments().size() > 0) {
                    msg.getAttachments().stream()
                            .filter(attachment -> attachment.getType().contains("text"))
                            .forEach(attachment -> wvMailViewer.getEngine()
                                    .loadContent(msg.getMessageBody()));
                }
            }
        }
    }

//    private class LoadMessagesService extends Service<ObservableSet<MailMessage>> {
//        private MailFolder mailFolder;
//        private ObservableSet<MailMessage> storedMessages;
//
//        public LoadMessagesService(MailFolder mailFolder, ObservableSet<MailMessage> displayedMessages) {
//            this.mailFolder = mailFolder;
//            this.storedMessages = displayedMessages;
//        }
//
//        @Override
//        protected Task<ObservableSet<MailMessage>> createTask() {
//            return new Task<ObservableSet<MailMessage>>() {
//                @Override
//                protected ObservableSet<MailMessage> call() throws Exception {
//                    return messagesModel.getFolderMessages(mailFolder, storedMessages);
//                }
//            };
//        }
//    }
//
//    private class FullLoadService extends Service<Void> {
//
//        @Override
//        protected Task<Void> createTask() {
//            return new Task<Void>() {
//                @Override
//                protected Void call() throws Exception {
//                    ExecutorService saverService = Executors.newFixedThreadPool(10);
//                    foldersModel.getAllFolders().forEach(f -> saverService.submit(() -> messagesModel.loadFromFolder(f)));
//                    saverService.shutdown();
//                    return null;
//                }
//            };
//        }
//    }
}
