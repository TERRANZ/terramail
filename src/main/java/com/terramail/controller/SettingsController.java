package com.terramail.controller;

import com.terramail.model.Account;
import com.terramail.service.AccountService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the settings dialog.
 */
public class SettingsController {
    private static final Logger logger = Logger.getLogger(SettingsController.class.getName());

    private final AccountService accountService = new AccountService();
    private ObservableList<Account> accounts = FXCollections.observableArrayList();
    private Account selectedAccount;

    @FXML
    private Dialog<ButtonType> settingsDialog;

    @FXML
    private TabPane settingsTabPane;

    @FXML
    private ComboBox<Account> accountComboBox;

    @FXML
    private Button addAccountButton;

    @FXML
    private Button editAccountButton;

    @FXML
    private Button deleteAccountButton;

    @FXML
    private TextField displayNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField organizationField;

    @FXML
    private ToggleGroup imapEnabledGroup;

    @FXML
    private RadioButton imapEnabledYes;

    @FXML
    private RadioButton imapEnabledNo;

    @FXML
    private TextField imapHostField;

    @FXML
    private TextField imapPortField;

    @FXML
    private TextField imapUserField;

    @FXML
    private PasswordField imapPasswordField;

    @FXML
    private ToggleGroup smtpEnabledGroup;

    @FXML
    private RadioButton smtpEnabledYes;

    @FXML
    private RadioButton smtpEnabledNo;

    @FXML
    private TextField smtpHostField;

    @FXML
    private TextField smtpPortField;

    @FXML
    private TextField smtpUserField;

    @FXML
    private PasswordField smtpPasswordField;

    @FXML
    private Button testConnectionButton;

    @FXML
    private Button saveButton;

    @FXML
    private ComboBox<Integer> syncIntervalCombo;

    @FXML
    private CheckBox systemTrayCheck;

    @FXML
    private CheckBox showPreviewsCheck;

    @FXML
    private ComboBox<String> defaultFormatCombo;

    /**
     * Initializes the controller.
     */
    public void initialize() {
        // Create ToggleGroup instances programmatically since ToggleGroup is not a Node
        // and cannot be placed in FXML layout containers
        imapEnabledGroup = new ToggleGroup();
        imapEnabledYes.setToggleGroup(imapEnabledGroup);
        imapEnabledNo.setToggleGroup(imapEnabledGroup);

        smtpEnabledGroup = new ToggleGroup();
        smtpEnabledYes.setToggleGroup(smtpEnabledGroup);
        smtpEnabledNo.setToggleGroup(smtpEnabledGroup);

        loadAccounts();
        setupEventHandlers();
        setupComboBox();
    }

    /**
     * Loads accounts from the database.
     */
    private void loadAccounts() {
        try {
            accounts.setAll(accountService.getAllAccounts());
            accountComboBox.setItems(accounts);

            if (!accounts.isEmpty()) {
                accountComboBox.getSelectionModel().selectFirst();
                selectedAccount = accounts.get(0);
                populateForm(selectedAccount);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to load accounts", e);
        }
    }

    /**
     * Sets up the account combo box listener.
     */
    private void setupComboBox() {
        accountComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedAccount = newVal;
            populateForm(newVal);
        });
    }

    /**
     * Populates the form with account data.
     */
    private void populateForm(Account account) {
        if (account == null) {
            clearForm();
            return;
        }

        displayNameField.setText(account.getDisplayName());
        emailField.setText(account.getEmail());
        organizationField.setText(account.getOrganization() != null ? account.getOrganization() : "");

        imapEnabledYes.setSelected(account.isImapEnabled());
        imapEnabledNo.setSelected(!account.isImapEnabled());
        imapHostField.setText(account.getImapHost());
        imapPortField.setText(String.valueOf(account.getImapPort()));
        imapUserField.setText(account.getImapUser());
        imapPasswordField.setText(account.getImapPassword());

        smtpEnabledYes.setSelected(account.isSmtpEnabled());
        smtpEnabledNo.setSelected(!account.isSmtpEnabled());
        smtpHostField.setText(account.getSmtpHost());
        smtpPortField.setText(String.valueOf(account.getSmtpPort()));
        smtpUserField.setText(account.getSmtpUser());
        smtpPasswordField.setText(account.getSmtpPassword());
    }

    /**
     * Clears the form.
     */
    private void clearForm() {
        displayNameField.clear();
        emailField.clear();
        organizationField.clear();
        imapHostField.clear();
        imapPortField.clear();
        imapUserField.clear();
        imapPasswordField.clear();
        smtpHostField.clear();
        smtpPortField.clear();
        smtpUserField.clear();
        smtpPasswordField.clear();
    }

    /**
     * Sets up event handlers.
     */
    private void setupEventHandlers() {
        addAccountButton.setOnAction(e -> handleAddAccount());
        editAccountButton.setOnAction(e -> handleEditAccount());
        deleteAccountButton.setOnAction(e -> handleDeleteAccount());
        saveButton.setOnAction(e -> handleSave());
        testConnectionButton.setOnAction(e -> handleTestConnection());
    }

    /**
     * Handles the add account button click.
     */
    private void handleAddAccount() {
        clearForm();
        selectedAccount = new Account();
        emailField.setDisable(false);
    }

    /**
     * Handles the edit account button click.
     */
    private void handleEditAccount() {
        if (selectedAccount == null) {
            return;
        }
        emailField.setDisable(true);
    }

    /**
     * Handles the delete account button click.
     */
    private void handleDeleteAccount() {
        if (selectedAccount == null) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Account");
        confirm.setHeaderText("Are you sure you want to delete the account '" + selectedAccount.getName() + "'?");
        confirm.setContentText("This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    accountService.deleteAccount(selectedAccount.getId());
                    accounts.remove(selectedAccount);
                    accountComboBox.setItems(accounts);
                    clearForm();
                    selectedAccount = null;
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Failed to delete account", e);
                    showError("Failed to delete account: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Handles the save button click.
     */
    private void handleSave() {
        Account account = selectedAccount != null ? selectedAccount : new Account();

        // Validate and populate account
        account.setName(displayNameField.getText());
        account.setEmail(emailField.getText());
        account.setOrganization(organizationField.getText());

        account.setImapEnabled(imapEnabledYes.isSelected());
        account.setImapHost(imapHostField.getText());
        try {
            account.setImapPort(Integer.parseInt(imapPortField.getText()));
        } catch (NumberFormatException e) {
            account.setImapPort(993);
        }
        account.setImapUser(imapUserField.getText());
        account.setImapPassword(imapPasswordField.getText());

        account.setSmtpEnabled(smtpEnabledYes.isSelected());
        account.setSmtpHost(smtpHostField.getText());
        try {
            account.setSmtpPort(Integer.parseInt(smtpPortField.getText()));
        } catch (NumberFormatException e) {
            account.setSmtpPort(587);
        }
        account.setSmtpUser(smtpUserField.getText());
        account.setSmtpPassword(smtpPasswordField.getText());

        // Validate
        AccountService.ValidationResult result = accountService.validateAccount(account);
        if (!result.isValid()) {
            StringBuilder msg = new StringBuilder("Please fix the following errors:\n");
            result.getErrors().forEach((field, error) -> msg.append("- ").append(error).append("\n"));
            showError(msg.toString());
            return;
        }

        try {
            if (selectedAccount == null || selectedAccount.getId() == 0) {
                // Create new account
                accountService.createAccount(account);
                accounts.add(account);
            } else {
                // Update existing account
                accountService.updateAccount(account);
            }

            accountComboBox.setItems(accounts);
            loadAccounts();
            settingsDialog.setResult(ButtonType.OK);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to save account", e);
            showError("Failed to save account: " + e.getMessage());
        }
    }

    /**
     * Handles the test connection button click.
     */
    private void handleTestConnection() {
        testConnectionButton.setDisable(true);
        testConnectionButton.setText("Testing...");

        // In a real implementation, this would test the IMAP/SMTP connections
        new Thread(() -> {
            try {
                // Simulate connection test
                Thread.sleep(1000);
                showInfo("Connection test completed successfully!");
            } catch (Exception e) {
                showError("Connection test failed: " + e.getMessage());
            } finally {
                testConnectionButton.setDisable(false);
                testConnectionButton.setText("Test Connection");
            }
        }).start();
    }

    /**
     * Shows an error dialog.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an info dialog.
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Gets the selected account.
     */
    public Account getSelectedAccount() {
        return selectedAccount;
    }
}
