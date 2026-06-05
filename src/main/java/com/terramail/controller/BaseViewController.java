package com.terramail.controller;

import com.terramail.TerramailApp;
import com.terramail.model.Account;
import com.terramail.model.EmailFolder;
import com.terramail.service.AccountService;
import com.terramail.service.SyncService;
import com.terramail.view.FolderTreePanel;
import com.terramail.view.MessageTablePanel;
import com.terramail.view.MessageViewerPanel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the main base view layout.
 */
public class BaseViewController {
    private static final Logger logger = Logger.getLogger(BaseViewController.class.getName());

    private final AccountService accountService = new AccountService();
    private final SyncService syncService = new SyncService();

    @FXML
    private SplitPane mainSplitPane;

    @FXML
    private FolderTreePanel folderTreePanel;

    @FXML
    private MessageTablePanel messageTablePanel;

    @FXML
    private MessageViewerPanel messageViewerPanel;

    // Menu items
    @FXML
    private MenuItem settingsMenuItem;

    @FXML
    private MenuItem connectMenuItem;

    @FXML
    private MenuItem refreshMenuItem;

    @FXML
    public void initialize() {
        // Initialize the view components
        if (folderTreePanel != null) {
            folderTreePanel.initialize();
        }

        if (messageTablePanel != null) {
            messageTablePanel.initialize();
        }

        if (messageViewerPanel != null) {
            messageViewerPanel.initialize();
        }

        // Link the components together
        linkComponents();
    }

    /**
     * Links the components together so they can communicate.
     */
    private void linkComponents() {
        // When a folder is selected in the tree, load messages in the table
        if (folderTreePanel != null && messageTablePanel != null) {
            folderTreePanel.folderSelectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    messageTablePanel.loadFolder(newVal);
                }
            });
        }

        // When a message is selected in the table, display it in the viewer
        if (messageTablePanel != null && messageViewerPanel != null) {
            messageTablePanel.messageSelectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    messageViewerPanel.displayMessage(newVal);
                }
            });
        }
    }

    /**
     * Handles the Settings menu item action.
     */
    @FXML
    public void handleSettings() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/fxml/settings.fxml"));
            Dialog<?> settingsDialog = fxmlLoader.load();
            settingsDialog.showAndWait();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load settings dialog", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open Settings");
            alert.setContentText("Could not load the settings dialog: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Handles the Connect menu item action.
     * Connects to the mail server and syncs all accounts.
     */
    @FXML
    public void handleConnect() {
        new Thread(() -> {
            try {
                var accounts = accountService.getAllAccounts();
                if (accounts.isEmpty()) {
                    javafx.application.Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("No Accounts");
                        alert.setHeaderText("No email accounts configured");
                        alert.setContentText("Please add an email account in Settings before connecting.");
                        alert.showAndWait();
                    });
                    return;
                }

                for (Account account : accounts) {
                    syncService.syncAccount(account);
                }

                javafx.application.Platform.runLater(() -> {
                    refresh();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Connect");
                    alert.setHeaderText("Sync completed");
                    alert.setContentText("Successfully synced " + accounts.size() + " account(s).");
                    alert.showAndWait();
                });
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Failed to sync accounts", e);
                javafx.application.Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Sync failed");
                    alert.setContentText("Could not connect to mail server: " + e.getMessage());
                    alert.showAndWait();
                });
            }
        }).start();
    }

    /**
     * Handles the Refresh Messages menu item action.
     * Refreshes the folder tree and reloads messages for the selected folder.
     */
    @FXML
    public void handleRefresh() {
        refresh();
        // Reload messages for the currently selected folder
        EmailFolder selectedFolder = getSelectedFolder();
        if (selectedFolder != null && messageTablePanel != null) {
            messageTablePanel.loadFolder(selectedFolder);
        }
    }

    /**
     * Gets the currently selected folder.
     */
    private EmailFolder getSelectedFolder() {
        if (folderTreePanel != null) {
            return folderTreePanel.getSelectedFolder();
        }
        return null;
    }

    /**
     * Gets the folder tree panel.
     */
    public FolderTreePanel getFolderTreePanel() {
        return folderTreePanel;
    }

    /**
     * Gets the message table panel.
     */
    public MessageTablePanel getMessageTablePanel() {
        return messageTablePanel;
    }

    /**
     * Gets the message viewer panel.
     */
    public MessageViewerPanel getMessageViewerPanel() {
        return messageViewerPanel;
    }

    /**
     * Refreshes all panels.
     */
    public void refresh() {
        if (folderTreePanel != null) {
            folderTreePanel.refresh();
        }
    }
}
