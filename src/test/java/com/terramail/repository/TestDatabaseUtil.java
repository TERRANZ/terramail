package com.terramail.repository;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;

public class TestDatabaseUtil {

    private static HikariDataSource dataSource;

    public static HikariDataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new HikariDataSource();
            dataSource.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
            dataSource.setUsername("sa");
            dataSource.setPassword("");
            dataSource.setMaximumPoolSize(5);
            initSchema();
        }
        return dataSource;
    }

    private static void initSchema() {
        String[] schema = {
            "CREATE TABLE IF NOT EXISTS folders (" +
            "  `id` BIGINT AUTO_INCREMENT PRIMARY KEY," +
            "  `account_id` BIGINT NOT NULL," +
            "  `name` VARCHAR(255) NOT NULL," +
            "  `type` VARCHAR(50) NOT NULL," +
            "  `parent_folder_id` BIGINT NOT NULL DEFAULT 0," +
            "  `imap_path` VARCHAR(1000) NOT NULL DEFAULT ''" +
            ")",
            "CREATE TABLE IF NOT EXISTS messages (" +
            "  `id` BIGINT AUTO_INCREMENT PRIMARY KEY," +
            "  `folder_id` BIGINT NOT NULL," +
            "  `from` VARCHAR(255)," +
            "  `to` VARCHAR(255)," +
            "  `cc` VARCHAR(255)," +
            "  `subject` VARCHAR(500)," +
            "  `date` TIMESTAMP," +
            "  `body` TEXT," +
            "  `seen` BOOLEAN DEFAULT FALSE," +
            "  `flagged` BOOLEAN DEFAULT FALSE," +
            "  `attachments` TEXT" +
            ")",
            "CREATE TABLE IF NOT EXISTS account_settings (" +
            "  `id` BIGINT AUTO_INCREMENT PRIMARY KEY," +
            "  `account_name` VARCHAR(255) NOT NULL," +
            "  `imap_host` VARCHAR(255)," +
            "  `imap_port` INT," +
            "  `imap_user` VARCHAR(255)," +
            "  `imap_password` VARCHAR(255)," +
            "  `imap_ssl` BOOLEAN DEFAULT FALSE," +
            "  `smtp_host` VARCHAR(255)," +
            "  `smtp_port` INT," +
            "  `smtp_user` VARCHAR(255)," +
            "  `smtp_password` VARCHAR(255)," +
            "  `smtp_ssl` BOOLEAN DEFAULT FALSE," +
            "  `db_url` VARCHAR(500)," +
            "  `db_user` VARCHAR(255)," +
            "  `db_password` VARCHAR(255)" +
            ")"
        };
        try (var conn = dataSource.getConnection()) {
            for (String sql : schema) {
                try (var stmt = conn.prepareStatement(sql)) {
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize test schema", e);
        }
    }

    public static void resetDatabase() {
        try (var conn = dataSource.getConnection()) {
            conn.createStatement().executeUpdate("DELETE FROM messages");
            conn.createStatement().executeUpdate("DELETE FROM folders");
            conn.createStatement().executeUpdate("DELETE FROM account_settings");
            conn.createStatement().executeUpdate("ALTER TABLE messages ALTER COLUMN id RESTART WITH 1");
            conn.createStatement().executeUpdate("ALTER TABLE folders ALTER COLUMN id RESTART WITH 1");
            conn.createStatement().executeUpdate("ALTER TABLE account_settings ALTER COLUMN id RESTART WITH 1");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reset test database", e);
        }
    }
}
