package com.terramail.repository;

import com.terramail.model.AccountSettings;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.Objects;

public class AccountSettingsRepositoryImpl implements AccountSettingsRepository {

    private final HikariDataSource dataSource;

    public AccountSettingsRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @Override
    public AccountSettings findById(long id) {
        String sql = "SELECT * FROM account_settings WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch settings " + id, e);
        }
        return null;
    }

    @Override
    public AccountSettings save(AccountSettings settings) {
        if (settings.getId() > 0) {
            String sql = "UPDATE account_settings SET account_name = ?, imap_host = ?, imap_port = ?, imap_user = ?, imap_password = ?, imap_ssl = ?, smtp_host = ?, smtp_port = ?, smtp_user = ?, smtp_password = ?, smtp_ssl = ?, db_url = ?, db_user = ?, db_password = ? WHERE id = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, settings.getAccountName());
                stmt.setString(2, settings.getImapHost());
                stmt.setInt(3, settings.getImapPort());
                stmt.setString(4, settings.getImapUser());
                stmt.setString(5, settings.getImapPassword());
                stmt.setBoolean(6, settings.isImapSsl());
                stmt.setString(7, settings.getSmtpHost());
                stmt.setInt(8, settings.getSmtpPort());
                stmt.setString(9, settings.getSmtpUser());
                stmt.setString(10, settings.getSmtpPassword());
                stmt.setBoolean(11, settings.isSmtpSsl());
                stmt.setString(12, settings.getDbUrl());
                stmt.setString(13, settings.getDbUser());
                stmt.setString(14, settings.getDbPassword());
                stmt.setLong(15, settings.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to update settings", e);
            }
        } else {
            String sql = "INSERT INTO account_settings (account_name, imap_host, imap_port, imap_user, imap_password, imap_ssl, smtp_host, smtp_port, smtp_user, smtp_password, smtp_ssl, db_url, db_user, db_password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, settings.getAccountName());
                stmt.setString(2, settings.getImapHost());
                stmt.setInt(3, settings.getImapPort());
                stmt.setString(4, settings.getImapUser());
                stmt.setString(5, settings.getImapPassword());
                stmt.setBoolean(6, settings.isImapSsl());
                stmt.setString(7, settings.getSmtpHost());
                stmt.setInt(8, settings.getSmtpPort());
                stmt.setString(9, settings.getSmtpUser());
                stmt.setString(10, settings.getSmtpPassword());
                stmt.setBoolean(11, settings.isSmtpSsl());
                stmt.setString(12, settings.getDbUrl());
                stmt.setString(13, settings.getDbUser());
                stmt.setString(14, settings.getDbPassword());
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        settings.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to save settings", e);
            }
        }
        return settings;
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM account_settings WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete settings " + id, e);
        }
    }

    @Override
    public AccountSettings findByAccountName(String accountName) {
        String sql = "SELECT * FROM account_settings WHERE account_name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find settings by name " + accountName, e);
        }
        return null;
    }

    private AccountSettings mapRow(ResultSet rs) throws SQLException {
        AccountSettings settings = new AccountSettings();
        settings.setId(rs.getLong("id"));
        settings.setAccountName(rs.getString("account_name"));
        settings.setImapHost(rs.getString("imap_host"));
        settings.setImapPort(rs.getInt("imap_port"));
        settings.setImapUser(rs.getString("imap_user"));
        settings.setImapPassword(rs.getString("imap_password"));
        settings.setImapSsl(rs.getBoolean("imap_ssl"));
        settings.setSmtpHost(rs.getString("smtp_host"));
        settings.setSmtpPort(rs.getInt("smtp_port"));
        settings.setSmtpUser(rs.getString("smtp_user"));
        settings.setSmtpPassword(rs.getString("smtp_password"));
        settings.setSmtpSsl(rs.getBoolean("smtp_ssl"));
        settings.setDbUrl(rs.getString("db_url"));
        settings.setDbUser(rs.getString("db_user"));
        settings.setDbPassword(rs.getString("db_password"));
        return settings;
    }
}
