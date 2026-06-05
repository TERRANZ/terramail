package com.terramail;

import com.terramail.config.AppConfig;
import com.terramail.config.DatabaseConfig;
import com.terramail.service.SyncService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX Application entry point for Terramail.
 */
public class TerramailApp extends Application {
    private static final Logger logger = Logger.getLogger(TerramailApp.class.getName());
    private static SyncService syncService;

    private static final String PRIMARY_STAGE_TITLE = AppConfig.getAppName() + " v" + AppConfig.getAppVersion();

    @Override
    public void init() {
        // Ensure data directories exist
        AppConfig.ensureDataDirectories();

        // Initialize database
        try {
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            dbConfig.initialize();
            logger.info("Database initialized successfully");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to initialize database", e);
        }

        // Create and start sync service
        syncService = new SyncService();
        if (AppConfig.isSyncOnStartup()) {
            syncService.startAutoSync();
        }

        logger.info("Terramail application initializing...");
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/base_view.fxml"));
            BorderPane root = fxmlLoader.load();

            var scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

            primaryStage.setTitle(PRIMARY_STAGE_TITLE);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error starting application", e);
            System.exit(1);
        }
    }

    @Override
    public void stop() {
        if (syncService != null) {
            syncService.shutdown();
        }

        DatabaseConfig.getInstance().closeAllConnections();
        logger.info("Terramail application stopped");
    }

    /**
     * Gets the sync service instance.
     */
    public static SyncService getSyncService() {
        return syncService;
    }

    /**
     * Main entry point (for testing without JavaFX launcher).
     */
    public static void main(String[] args) {
        launch(args);
    }
}
