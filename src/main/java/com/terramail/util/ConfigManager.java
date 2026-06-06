package com.terramail.util;

import com.terramail.model.AccountSettings;
import com.terramail.repository.AccountSettingsRepository;

import java.nio.file.*;
import java.util.Optional;

public class ConfigManager {

    private static final Path CONFIG_FILE = Path.of("terrmail_config.json");
    private final AccountSettingsRepository repository;

    public ConfigManager(AccountSettingsRepository repository) {
        this.repository = repository;
    }

    public Optional<AccountSettings> loadSettings() {
        try {
            AccountSettings settings = repository.findByAccountName("default");
            return Optional.ofNullable(settings);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean saveSettings(AccountSettings settings) {
        try {
            repository.save(settings);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
