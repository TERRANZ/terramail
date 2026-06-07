package com.terramail.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Logger;

public class AppConfig {

    private static final Logger LOGGER = Logger.getLogger(AppConfig.class.getName());
    private static final Path CONFIG_FILE = Path.of("terrmail.properties");
    private static final String DEFAULT_THREAD_POOL_SIZE = "10";
    private static final String THREAD_POOL_SIZE_KEY = "message.loading.threads";

    private final Properties properties;

    public AppConfig() {
        this.properties = new Properties();
        loadConfig();
    }

    private void loadConfig() {
        try {
            if (Files.exists(CONFIG_FILE)) {
                try (var input = Files.newInputStream(CONFIG_FILE)) {
                    properties.load(input);
                    LOGGER.info("Loaded configuration from " + CONFIG_FILE);
                }
            } else {
                LOGGER.info("Configuration file not found, using defaults");
            }
        } catch (IOException e) {
            LOGGER.warning("Failed to load configuration: " + e.getMessage());
        }
    }

    public int getMessageLoadingThreads() {
        String value = properties.getProperty(THREAD_POOL_SIZE_KEY, DEFAULT_THREAD_POOL_SIZE);
        try {
            int threads = Integer.parseInt(value);
            if (threads < 1 || threads > 50) {
                LOGGER.warning("Invalid thread pool size: " + threads + ", using default: " + DEFAULT_THREAD_POOL_SIZE);
                return Integer.parseInt(DEFAULT_THREAD_POOL_SIZE);
            }
            return threads;
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid thread pool size format: " + value + ", using default: " + DEFAULT_THREAD_POOL_SIZE);
            return Integer.parseInt(DEFAULT_THREAD_POOL_SIZE);
        }
    }

    public void setMessageLoadingThreads(int threads) {
        if (threads < 1 || threads > 50) {
            throw new IllegalArgumentException("Thread pool size must be between 1 and 50");
        }
        properties.setProperty(THREAD_POOL_SIZE_KEY, String.valueOf(threads));
        saveConfig();
    }

    private void saveConfig() {
        try {
            try (var output = Files.newOutputStream(CONFIG_FILE)) {
                properties.store(output, "Terramail Application Configuration");
                LOGGER.info("Saved configuration to " + CONFIG_FILE);
            }
        } catch (IOException e) {
            LOGGER.warning("Failed to save configuration: " + e.getMessage());
        }
    }
}
