package com.terramail.ui.panels;

import com.terramail.model.AccountSettings;
import com.terramail.service.DatabaseService;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private final JTextField accountNameField;
    private final JTextField imapHostField;
    private final JSpinner imapPortSpinner;
    private final JTextField imapUserField;
    private final JPasswordField imapPasswordField;
    private final JCheckBox imapSslCheck;
    private final JTextField smtpHostField;
    private final JSpinner smtpPortSpinner;
    private final JTextField smtpUserField;
    private final JPasswordField smtpPasswordField;
    private final JCheckBox smtpSslCheck;
    private final JTextField dbUrlField;
    private final JTextField dbUserField;
    private final JPasswordField dbPasswordField;

    private AccountSettings currentSettings;
    private DatabaseService databaseService;
    private java.util.function.Consumer<AccountSettings> saveListener;

    public SettingsPanel(AccountSettings settings, DatabaseService databaseService) {
        this.currentSettings = settings;
        this.databaseService = databaseService;
        this.accountNameField = new JTextField(settings.getAccountName(), 20);
        this.imapHostField = new JTextField(settings.getImapHost(), 20);
        this.imapPortSpinner = new JSpinner(new SpinnerNumberModel(settings.getImapPort(), 1, 65535, 1));
        this.imapUserField = new JTextField(settings.getImapUser(), 20);
        this.imapPasswordField = new JPasswordField(settings.getImapPassword(), 20);
        this.imapSslCheck = new JCheckBox("SSL", settings.isImapSsl());
        this.smtpHostField = new JTextField(settings.getSmtpHost(), 20);
        this.smtpPortSpinner = new JSpinner(new SpinnerNumberModel(settings.getSmtpPort(), 1, 65535, 1));
        this.smtpUserField = new JTextField(settings.getSmtpUser(), 20);
        this.smtpPasswordField = new JPasswordField(settings.getSmtpPassword(), 20);
        this.smtpSslCheck = new JCheckBox("SSL", settings.isSmtpSsl());
        this.dbUrlField = new JTextField(settings.getDbUrl(), 20);
        this.dbUserField = new JTextField(settings.getDbUser(), 20);
        this.dbPasswordField = new JPasswordField(settings.getDbPassword(), 20);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("IMAP", createImapPanel());
        tabs.add("SMTP", createSmtpPanel());
        tabs.add("Database", createDbPanel());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        saveButton.addActionListener(e -> saveSettings());
        cancelButton.addActionListener(e -> {
            if (saveListener != null) saveListener.accept(currentSettings);
        });
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(tabs, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createImapPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        panel.add(new JLabel("Account Name:"), gbc);
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        panel.add(accountNameField, gbc);

        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Host:"), gbc);
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        panel.add(imapHostField, gbc);

        gbc.gridy = 4;
        gbc.weightx = 0;
        panel.add(new JLabel("Port:"), gbc);
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        panel.add(imapPortSpinner, gbc);

        gbc.gridy = 6;
        gbc.weightx = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        panel.add(imapUserField, gbc);

        gbc.gridy = 8;
        gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridy = 9;
        gbc.weightx = 1.0;
        panel.add(imapPasswordField, gbc);

        gbc.gridy = 10;
        gbc.weightx = 0;
        panel.add(imapSslCheck, gbc);

        return panel;
    }

    private JPanel createSmtpPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        panel.add(new JLabel("Host:"), gbc);
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        panel.add(smtpHostField, gbc);

        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Port:"), gbc);
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        panel.add(smtpPortSpinner, gbc);

        gbc.gridy = 4;
        gbc.weightx = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        panel.add(smtpUserField, gbc);

        gbc.gridy = 6;
        gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        panel.add(smtpPasswordField, gbc);

        gbc.gridy = 8;
        gbc.weightx = 0;
        panel.add(smtpSslCheck, gbc);

        return panel;
    }

    private JPanel createDbPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        panel.add(new JLabel("URL:"), gbc);
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        panel.add(dbUrlField, gbc);

        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        panel.add(dbUserField, gbc);

        gbc.gridy = 4;
        gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        panel.add(dbPasswordField, gbc);

        return panel;
    }

    private void saveSettings() {
        if (saveListener != null) {
            AccountSettings newSettings = new AccountSettings();
            newSettings.setId(currentSettings.getId());
            newSettings.setAccountName(accountNameField.getText());
            newSettings.setImapHost(imapHostField.getText());
            newSettings.setImapPort((int) imapPortSpinner.getValue());
            newSettings.setImapUser(imapUserField.getText());
            newSettings.setImapPassword(new String(imapPasswordField.getPassword()));
            newSettings.setImapSsl(imapSslCheck.isSelected());
            newSettings.setSmtpHost(smtpHostField.getText());
            newSettings.setSmtpPort((int) smtpPortSpinner.getValue());
            newSettings.setSmtpUser(smtpUserField.getText());
            newSettings.setSmtpPassword(new String(smtpPasswordField.getPassword()));
            newSettings.setSmtpSsl(smtpSslCheck.isSelected());
            newSettings.setDbUrl(dbUrlField.getText());
            newSettings.setDbUser(dbUserField.getText());
            newSettings.setDbPassword(new String(dbPasswordField.getPassword()));

            boolean dbOk = databaseService.initialize(newSettings);
            if (!dbOk) {
                JOptionPane.showMessageDialog(this, "Database connection failed. Please check settings.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            saveListener.accept(newSettings);
        }
    }

    public void setSaveListener(java.util.function.Consumer<AccountSettings> listener) {
        this.saveListener = listener;
    }
}
