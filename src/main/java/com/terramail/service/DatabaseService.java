package com.terramail.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.terramail.model.AccountSettings;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseService {

    private HikariDataSource dataSource;

    public boolean initialize(AccountSettings settings) {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(settings.getDbUrl());
            config.setUsername(settings.getDbUser());
            config.setPassword(settings.getDbPassword());
            config.setMaximumPoolSize(5);
            config.setMinimumIdle(1);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);

            try (Connection conn = dataSource.getConnection()) {
                createTables(conn);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public boolean isConnected() {
        try {
            if (dataSource != null && !dataSource.isClosed()) {
                try (Connection conn = dataSource.getConnection()) {
                    return conn.isValid(2);
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    private void createTables(Connection conn) throws SQLException {
        String[] schemas = {
            "CREATE TABLE IF NOT EXISTS folders (" +
            "  `id` BIGINT AUTO_INCREMENT PRIMARY KEY," +
            "  `account_id` BIGINT NOT NULL," +
            "  `name` VARCHAR(255) NOT NULL," +
            "  `type` VARCHAR(50) NOT NULL" +
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
            "  id BIGINT AUTO_INCREMENT PRIMARY KEY," +
            "  account_name VARCHAR(255) NOT NULL," +
            "  imap_host VARCHAR(255)," +
            "  imap_port INT," +
            "  imap_user VARCHAR(255)," +
            "  imap_password VARCHAR(255)," +
            "  imap_ssl BOOLEAN DEFAULT FALSE," +
            "  smtp_host VARCHAR(255)," +
            "  smtp_port INT," +
            "  smtp_user VARCHAR(255)," +
            "  smtp_password VARCHAR(255)," +
            "  smtp_ssl BOOLEAN DEFAULT FALSE," +
            "  db_url VARCHAR(500)," +
            "  db_user VARCHAR(255)," +
            "  db_password VARCHAR(255)" +
            ")"
        };
        for (String sql : schemas) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sql);
            }
        }
    }
}
