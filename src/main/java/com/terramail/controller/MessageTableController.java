package com.terramail.controller;

import com.terramail.model.EmailFolder;
import com.terramail.model.EmailMessage;
import com.terramail.model.Recipient;
import com.terramail.service.MessageService;
import com.terramail.service.SyncService;
import com.terramail.util.DateTimeUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the message table panel.
 */
public class MessageTableController {
    private static final Logger logger = Logger.getLogger(MessageTableController.class.getName());

    private final SyncService syncService = new SyncService();
    private final MessageService messageService = new MessageService();

    private EmailFolder selectedFolder;
    private ObservableList<EmailMessage> messages = FXCollections.observableArrayList();
    private final SimpleObjectProperty<EmailMessage> messageSelected = new SimpleObjectProperty<>();

    @FXML
    private Label folderNameLabel;

    @FXML
    private Label messageCountLabel;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<EmailMessage> messageTableView;

    @FXML
    private TableColumn<EmailMessage, String> flagColumn;

    @FXML
    private TableColumn<EmailMessage, String> readColumn;

    @FXML
    private TableColumn<EmailMessage, String> subjectColumn;

    @FXML
    private TableColumn<EmailMessage, String> fromColumn;

    @FXML
    private TableColumn<EmailMessage, String> dateColumn;

    @FXML
    private Button replyButton;

    @FXML
    private Button forwardButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button syncButton;

    /**
     * Initializes the controller.
     */
    public void initialize() {
        setupTableColumns();
        setupEventHandlers();
    }

    /**
     * Sets up the table columns with cell value factories.
     */
    private void setupTableColumns() {
        flagColumn.setCellValueFactory(cellData -> {
            EmailMessage msg = cellData.getValue();
            return new SimpleObjectProperty<>(msg.isFlagged() ? "!" : "");
        });

        readColumn.setCellValueFactory(cellData -> {
            EmailMessage msg = cellData.getValue();
            return new SimpleObjectProperty<>(msg.isRead() ? "" : "●");
        });

        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        subjectColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-font-weight: normal;");
                } else {
                    setText(item);
                    EmailMessage msg = getTableView().getItems().get(getIndex());
                    if (!msg.isRead()) {
                        setStyle("-fx-font-weight: bold;");
                    }
                }
            }
        });

        fromColumn.setCellValueFactory(cellData -> {
            EmailMessage msg = cellData.getValue();
            String sender = "";
            if (msg.getRecipients() != null) {
                for (Recipient r : msg.getRecipients()) {
                    if ("FROM".equals(r.getType()) || "TO".equals(r.getType())) {
                        sender = r.getName() != null && !r.getName().isEmpty()
                                ? r.getName() + " <" + r.getEmail() + ">"
                                : r.getEmail();
                        break;
                    }
                }
            }
            return new SimpleObjectProperty<>(sender);
        });

        dateColumn.setCellValueFactory(cellData -> {
            EmailMessage msg = cellData.getValue();
            return new SimpleObjectProperty<>(
                    DateTimeUtil.formatShort(msg.getReceivedDate()));
        });
    }

    /**
     * Sets up event handlers.
     */
    private void setupEventHandlers() {
        messageTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                messageSelected.set(newVal);
                showActionButtons(true);
            } else {
                showActionButtons(false);
            }
        });

        messageTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                EmailMessage msg = messageTableView.getSelectionModel().getSelectedItem();
                if (msg != null) {
                    // Mark as read when double-clicked
                    try {
                        messageService.markAsRead(msg.getId(), true);
                        msg.setRead(true);
                    } catch (SQLException e) {
                        logger.log(Level.WARNING, "Failed to mark message as read", e);
                    }
                }
            }
        });

        syncButton.setOnAction(e -> handleSync());
        replyButton.setOnAction(e -> handleReply());
        forwardButton.setOnAction(e -> handleForward());
        deleteButton.setOnAction(e -> handleDelete());
    }

    /**
     * Shows/hides action buttons.
     */
    private void showActionButtons(boolean show) {
        replyButton.setVisible(show);
        forwardButton.setVisible(show);
        deleteButton.setVisible(show);
    }

    /**
     * Loads messages for the given folder.
     */
    public void loadFolder(EmailFolder folder) {
        selectedFolder = folder;
        folderNameLabel.setText(folder.getName());

        try {
            int count = messageService.getMessageCount(folder.getId());
            messageCountLabel.setText(count + " messages");

            messages.setAll(messageService.getMessagesByFolder(folder.getId(), 0, 100));
            messageTableView.setItems(messages);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to load messages for folder: " + folder.getName(), e);
            messageCountLabel.setText("Error loading messages");
        }
    }

    /**
     * Handles the sync button click.
     */
    public void handleSync() {
        if (selectedFolder != null && selectedFolder.getAccountId() > 0) {
            try {
                var account = syncService.getAccountService().findAccountById(selectedFolder.getAccountId());
                if (account != null) {
                    syncService.syncAccount(account);
                    loadFolder(selectedFolder);
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Failed to sync", e);
            }
        }
    }

    /**
     * Handles the search field action.
     */
    public void handleSearch() {
        String query = searchField.getText();
        if (query == null || query.isEmpty() || selectedFolder == null) {
            return;
        }

        try {
            List<EmailMessage> results = messageService.searchBySubject(selectedFolder.getId(), query);
            messages.setAll(results);
            messageTableView.setItems(messages);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Search failed", e);
        }
    }

    /**
     * Handles the reply button click.
     */
    public void handleReply() {
        EmailMessage msg = messageTableView.getSelectionModel().getSelectedItem();
        if (msg != null) {
            logger.info("Reply to: " + msg.getMessageId());
            // In a full implementation, this would open a compose window
        }
    }

    /**
     * Handles the forward button click.
     */
    public void handleForward() {
        EmailMessage msg = messageTableView.getSelectionModel().getSelectedItem();
        if (msg != null) {
            logger.info("Forward message: " + msg.getMessageId());
            // In a full implementation, this would open a compose window
        }
    }

    /**
     * Handles the delete button click.
     */
    public void handleDelete() {
        EmailMessage msg = messageTableView.getSelectionModel().getSelectedItem();
        if (msg != null) {
            try {
                messageService.deleteMessage(msg.getId());
                messages.remove(msg);
                messageTableView.setItems(messages);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Failed to delete message", e);
            }
        }
    }

    /**
     * Gets the currently selected message.
     */
    public EmailMessage getSelectedMessage() {
        return messageSelected.get();
    }

    /**
     * Gets the message selected property.
     */
    public SimpleObjectProperty<EmailMessage> messageSelectedProperty() {
        return messageSelected;
    }

    /**
     * Gets the currently selected folder.
     */
    public EmailFolder getSelectedFolder() {
        return selectedFolder;
    }
}
