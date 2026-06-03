package com.terramail.controller;

import com.terramail.model.Account;
import com.terramail.model.EmailFolder;
import com.terramail.service.AccountService;
import com.terramail.service.FolderService;
import com.terramail.service.SyncService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the folder tree panel.
 */
public class FolderTreeController {
    private static final Logger logger = Logger.getLogger(FolderTreeController.class.getName());

    private final SyncService syncService = new SyncService();
    private final AccountService accountService = new AccountService();
    private final FolderService folderService = new FolderService();

    private ObservableList<Account> accounts = FXCollections.observableArrayList();
    private Account selectedAccount;
    private final SimpleObjectProperty<EmailFolder> folderSelected = new SimpleObjectProperty<>();

    @FXML
    private MenuButton accountMenu;

    @FXML
    private Button refreshButton;

    @FXML
    private TreeView<AccountFolderWrapper> folderTreeView;

    /**
     * Initializes the controller.
     */
    public void initialize() {
        setupTreeView();
        loadAccounts();
        setupEventHandlers();
    }

    /**
     * Sets up the tree view with a custom cell factory.
     */
    private void setupTreeView() {
        folderTreeView.setCellFactory(treeView -> new TreeCell<>() {
            @Override
            protected void updateItem(AccountFolderWrapper item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(item.getDisplayName());
                    if (item.isFolder()) {
                        EmailFolder folder = item.getFolder();
                        int unreadCount = 0;
                        try {
                            unreadCount = folderService.getUnreadMessageCount(folder.getId());
                        } catch (SQLException e) {
                            logger.log(Level.WARNING, "Failed to get unread count", e);
                        }
                        if (unreadCount > 0) {
                            setText(item.getDisplayName() + " (" + unreadCount + ")");
                            setStyle("-fx-font-weight: bold;");
                        }
                    }
                }
            }
        });

        folderTreeView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TreeItem<AccountFolderWrapper> selected = folderTreeView.getSelectionModel().getSelectedItem();
                if (selected != null && selected.getValue() != null) {
                    AccountFolderWrapper item = selected.getValue();
                    if (item.isFolder()) {
                        folderSelected.set(item.getFolder());
                    } else {
                        // Expand/collapse account
                        TreeItem<AccountFolderWrapper> root = selected.getParent();
                        if (root != null) {
                            root.setExpanded(!root.isExpanded());
                        }
                    }
                }
            }
        });
    }

    /**
     * Loads accounts from the database.
     */
    private void loadAccounts() {
        try {
            accounts.setAll(accountService.getAllAccounts());
            updateAccountMenu();

            if (!accounts.isEmpty()) {
                selectedAccount = accounts.get(0);
                loadFoldersForAccount(selectedAccount);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to load accounts", e);
        }
    }

    /**
     * Updates the account menu with current accounts.
     */
    private void updateAccountMenu() {
        accountMenu.getItems().clear();
        for (Account account : accounts) {
            MenuItem item = new MenuItem(account.getName() + " <" + account.getEmail() + ">");
            item.setUserData(account);
            item.setOnAction(e -> switchAccount(account));
            accountMenu.getItems().add(item);
        }

        if (!accounts.isEmpty()) {
            accountMenu.setText(accounts.get(0).getName());
        }
    }

    /**
     * Switches to a different account.
     */
    private void switchAccount(Account account) {
        selectedAccount = account;
        accountMenu.setText(account.getName());
        loadFoldersForAccount(account);
    }

    /**
     * Loads folders for the given account.
     */
    private void loadFoldersForAccount(Account account) {
        TreeItem<AccountFolderWrapper> root = new TreeItem<>(
                new AccountFolderWrapper(account, true));
        root.setExpanded(true);

        try {
            List<EmailFolder> folders = folderService.getFoldersByAccountId(account.getId());
            ObservableList<TreeItem<AccountFolderWrapper>> children = FXCollections.observableArrayList();

            for (EmailFolder folder : folders) {
                TreeItem<AccountFolderWrapper> folderItem = new TreeItem<>(
                        new AccountFolderWrapper(folder, false));
                children.add(folderItem);
            }

            root.getChildren().setAll(children);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to load folders for account: " + account.getEmail(), e);
        }

        folderTreeView.setRoot(root);
        folderTreeView.getStylesheets().clear();
    }

    /**
     * Sets up event handlers.
     */
    private void setupEventHandlers() {
        refreshButton.setOnAction(e -> refresh());
    }

    /**
     * Refreshes the folder tree.
     */
    public void refresh() {
        if (selectedAccount != null) {
            loadFoldersForAccount(selectedAccount);
        }
    }

    /**
     * Gets the currently selected folder.
     */
    public EmailFolder getSelectedFolder() {
        return folderSelected.get();
    }

    /**
     * Gets the folder selected property.
     */
    public SimpleObjectProperty<EmailFolder> folderSelectedProperty() {
        return folderSelected;
    }

    /**
     * Wrapper class for tree view items.
     */
    public static class AccountFolderWrapper {
        private final Account account;
        private final EmailFolder folder;
        private final boolean isAccount;

        public AccountFolderWrapper(Account account, boolean isAccount) {
            this.account = account;
            this.folder = null;
            this.isAccount = isAccount;
        }

        public AccountFolderWrapper(EmailFolder folder, boolean isAccount) {
            this.account = null;
            this.folder = folder;
            this.isAccount = isAccount;
        }

        public String getDisplayName() {
            if (isAccount && account != null) {
                return account.getName();
            } else if (folder != null) {
                return folder.getName();
            }
            return "";
        }

        public boolean isAccount() {
            return isAccount;
        }

        public boolean isFolder() {
            return !isAccount;
        }

        public Account getAccount() {
            return account;
        }

        public EmailFolder getFolder() {
            return folder;
        }
    }
}
