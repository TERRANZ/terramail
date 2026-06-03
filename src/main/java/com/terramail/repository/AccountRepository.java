package com.terramail.repository;

import com.terramail.config.DatabaseConfig;
import com.terramail.model.Account;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository for Account entity operations.
 */
public class AccountRepository {
    private static final Logger logger = Logger.getLogger(AccountRepository.class.getName());
    private final DatabaseConfig dbConfig;

    public AccountRepository() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /**
     * Creates a new account in the database.
     */
    public Account create(Account account) throws SQLException {
        String sql = """
                INSERT INTO ACCOUNT (name, email, imap_host, imap_port, imap_user, imap_password,
                                   smtp_host, smtp_port, smtp_user, smtp_password,
                                   imap_enabled, smtp_enabled, display_name, organization)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, account.getName());
            stmt.setString(2, account.getEmail());
            stmt.setString(3, account.getImapHost());
            stmt.setInt(4, account.getImapPort());
            stmt.setString(5, account.getImapUser());
            stmt.setString(6, account.getImapPassword());
            stmt.setString(7, account.getSmtpHost());
            stmt.setInt(8, account.getSmtpPort());
            stmt.setString(9, account.getSmtpUser());
            stmt.setString(10, account.getSmtpPassword());
            stmt.setBoolean(11, account.isImapEnabled());
            stmt.setBoolean(12, account.isSmtpEnabled());
            stmt.setString(13, account.getDisplayName());
            stmt.setString(14, account.getOrganization());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    account.setId(generatedKeys.getLong("id"));
                    account.setCreatedAt(LocalDateTime.now());
                    account.setUpdatedAt(LocalDateTime.now());
                }
            }
        }

        logger.log(Level.FINE, "Created account: {0}", account.getEmail());
        return account;
    }

    /**
     * Updates an existing account.
     */
    public void update(Account account) throws SQLException {
        String sql = """
                UPDATE ACCOUNT SET name = ?, email = ?, imap_host = ?, imap_port = ?,
                                   imap_user = ?, imap_password = ?, smtp_host = ?, smtp_port = ?,
                                   smtp_user = ?, smtp_password = ?, imap_enabled = ?, smtp_enabled = ?,
                                   display_name = ?, organization = ?, updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account.getName());
            stmt.setString(2, account.getEmail());
            stmt.setString(3, account.getImapHost());
            stmt.setInt(4, account.getImapPort());
            stmt.setString(5, account.getImapUser());
            stmt.setString(6, account.getImapPassword());
            stmt.setString(7, account.getSmtpHost());
            stmt.setInt(8, account.getSmtpPort());
            stmt.setString(9, account.getSmtpUser());
            stmt.setString(10, account.getSmtpPassword());
            stmt.setBoolean(11, account.isImapEnabled());
            stmt.setBoolean(12, account.isSmtpEnabled());
            stmt.setString(13, account.getDisplayName());
            stmt.setString(14, account.getOrganization());
            stmt.setLong(15, account.getId());

            stmt.executeUpdate();
        }

        logger.log(Level.FINE, "Updated account: {0}", account.getEmail());
    }

    /**
     * Deletes an account by ID.
     */
    public void delete(long accountId) throws SQLException {
        String sql = "DELETE FROM ACCOUNT WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, accountId);
            stmt.executeUpdate();
        }

        logger.log(Level.FINE, "Deleted account ID: {0}", accountId);
    }

    /**
     * Finds an account by ID.
     */
    public Account findById(long accountId) throws SQLException {
        String sql = "SELECT * FROM ACCOUNT WHERE id = ?";
        Account account = null;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, accountId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    account = mapRow(rs);
                }
            }
        }

        return account;
    }

    /**
     * Finds an account by email address.
     */
    public Account findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM ACCOUNT WHERE email = ?";
        Account account = null;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    account = mapRow(rs);
                }
            }
        }

        return account;
    }

    /**
     * Finds all accounts.
     */
    public List<Account> findAll() throws SQLException {
        String sql = "SELECT * FROM ACCOUNT ORDER BY name";
        List<Account> accounts = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                accounts.add(mapRow(rs));
            }
        }

        return accounts;
    }

    /**
     * Checks if an account with the given email already exists.
     */
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ACCOUNT WHERE email = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    /**
     * Maps a database row to an Account object.
     */
    private Account mapRow(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getLong("id"));
        account.setName(rs.getString("name"));
        account.setEmail(rs.getString("email"));
        account.setImapHost(rs.getString("imap_host"));
        account.setImapPort(rs.getInt("imap_port"));
        account.setImapUser(rs.getString("imap_user"));
        account.setImapPassword(rs.getString("imap_password"));
        account.setSmtpHost(rs.getString("smtp_host"));
        account.setSmtpPort(rs.getInt("smtp_port"));
        account.setSmtpUser(rs.getString("smtp_user"));
        account.setSmtpPassword(rs.getString("smtp_password"));
        account.setImapEnabled(rs.getBoolean("imap_enabled"));
        account.setSmtpEnabled(rs.getBoolean("smtp_enabled"));
        account.setDisplayName(rs.getString("display_name"));
        account.setOrganization(rs.getString("organization"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            account.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            account.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return account;
    }
}
