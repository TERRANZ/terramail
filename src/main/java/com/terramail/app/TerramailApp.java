package com.terramail.app;

import com.terramail.model.AccountSettings;
import com.terramail.repository.AccountSettingsRepository;
import com.terramail.repository.AccountSettingsRepositoryImpl;
import com.terramail.service.DatabaseService;
import com.terramail.ui.main.MainFrame;

import javax.swing.*;

public class TerramailApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Use default look and feel
            }

            DatabaseService databaseService = new DatabaseService();

            AccountSettings defaultSettings = new AccountSettings();
            defaultSettings.setAccountName("default");
            defaultSettings.setImapHost("localhost");
            defaultSettings.setImapPort(143);
            defaultSettings.setSmtpHost("localhost");
            defaultSettings.setSmtpPort(25);
            defaultSettings.setDbUrl("jdbc:mysql://192.168.1.3:3306/terramail");
            defaultSettings.setDbUser("terramail");
            defaultSettings.setDbPassword("123");

            if (!databaseService.initialize(defaultSettings)) {
                System.err.println("Failed to initialize database. Starting with default settings.");
            }

            AccountSettingsRepository settingsRepo = new AccountSettingsRepositoryImpl(databaseService.getDataSource());
            AccountSettings settings = settingsRepo.findByAccountName("default");
            if (settings == null) {
                settings = settingsRepo.save(defaultSettings);
            }

            MainFrame mainFrame = new MainFrame(databaseService, settings);
            mainFrame.setVisible(true);
        });
    }
}
