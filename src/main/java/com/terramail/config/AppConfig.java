package com.terramail.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Application configuration loader.
 */
public class AppConfig {
    private static final Properties properties = new Properties();
    private static final String PROPERTIES_FILE = "application.properties";

    static {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Error loading application.properties: " + e.getMessage());
        }
    }

    private static String resolvePlaceholders(String value) {
        if (value == null) return null;
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\$\\{([^}]+)}");
        java.util.regex.Matcher matcher = pattern.matcher(value);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = System.getProperty(key, properties.getProperty(key, matcher.group(0)));
            matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        return resolvePlaceholders(value);
    }

    public static String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key);
        return resolvePlaceholders(value != null ? value : defaultValue);
    }

    public static int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(resolvePlaceholders(value));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(resolvePlaceholders(value));
    }

    // Database properties
    public static String getDbUrl() {
        return getProperty("db.url", "jdbc:mysql://localhost:3306/terramail?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
    }

    public static String getDbUsername() {
        return getProperty("db.username", "root");
    }

    public static String getDbPassword() {
        return getProperty("db.password", "");
    }

    public static int getDbPoolSize() {
        return getIntProperty("db.pool.size", 10);
    }

    public static int getDbMinimumIdle() {
        return getIntProperty("db.minimumIdle", 2);
    }

    // IMAP properties
    public static int getImapDefaultPort() {
        return getIntProperty("imap.default.port", 993);
    }

    public static boolean isImapSslEnabled() {
        return getBooleanProperty("imap.ssl.enabled", true);
    }

    public static int getImapTimeout() {
        return getIntProperty("imap.timeout", 30000);
    }

    // SMTP properties
    public static int getSmtpDefaultPort() {
        return getIntProperty("smtp.default.port", 587);
    }

    public static boolean isSmtpStarttlsEnabled() {
        return getBooleanProperty("smtp.starttls.enabled", true);
    }

    public static int getSmtpTimeout() {
        return getIntProperty("smtp.timeout", 30000);
    }

    // Sync properties
    public static int getSyncIntervalSeconds() {
        return getIntProperty("sync.interval.seconds", 300);
    }

    public static boolean isSyncOnStartup() {
        return getBooleanProperty("sync.on.startup", true);
    }

    // Application properties
    public static String getAppName() {
        return getProperty("app.name", "Terramail");
    }

    public static String getAppVersion() {
        return getProperty("app.version", "1.0.0");
    }

    public static Path getAppDataDir() {
        String dir = getProperty("app.data.dir", Paths.get(System.getProperty("user.home"), ".terramail").toString());
        return Paths.get(dir);
    }

    public static Path getAttachmentsDir() {
        String dir = getProperty("app.attachments.dir", Paths.get(getAppDataDir().toString(), "attachments").toString());
        return Paths.get(dir);
    }

    public static void ensureDataDirectories() {
        try {
            getAppDataDir().toFile().mkdirs();
            getAttachmentsDir().toFile().mkdirs();
        } catch (SecurityException e) {
            System.err.println("Warning: Could not create data directories: " + e.getMessage());
        }
    }
}
