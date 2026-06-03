package com.terramail.repository;

import com.terramail.config.DatabaseConfig;
import com.terramail.model.EmailFolder;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository for EmailFolder entity operations.
 */
public class FolderRepository {
    private static final Logger logger = Logger.getLogger(FolderRepository.class.getName());
    private final DatabaseConfig dbConfig;

    public FolderRepository() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    public EmailFolder create(EmailFolder folder) throws SQLException {
        String sql = """
                INSERT INTO FOLDER (account_id, name, path, unique_id_prefix, message_count,
                                   is_subscribed, is_synched, last_synched)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, folder.getAccountId());
            stmt.setString(2, folder.getName());
            stmt.setString(3, folder.getPath());
            stmt.setString(4, folder.getUniqueIdPrefix());
            stmt.setInt(5, folder.getMessageCount());
            stmt.setBoolean(6, folder.isSubscribed());
            stmt.setBoolean(7, folder.isSynched());
            stmt.setTimestamp(8, folder.getLastSynched() != null ? Timestamp.valueOf(folder.getLastSynched()) : null);

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    folder.setId(generatedKeys.getLong("id"));
                    folder.setCreatedAt(LocalDateTime.now());
                }
            }
        }

        logger.log(Level.FINE, "Created folder: {0} for account {1}",
                new Object[]{folder.getName(), folder.getAccountId()});
        return folder;
    }

    public void update(EmailFolder folder) throws SQLException {
        String sql = """
                UPDATE FOLDER SET name = ?, path = ?, unique_id_prefix = ?, message_count = ?,
                                  is_subscribed = ?, is_synched = ?, last_synched = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, folder.getName());
            stmt.setString(2, folder.getPath());
            stmt.setString(3, folder.getUniqueIdPrefix());
            stmt.setInt(4, folder.getMessageCount());
            stmt.setBoolean(5, folder.isSubscribed());
            stmt.setBoolean(6, folder.isSynched());
            stmt.setLong(7, folder.getId());

            stmt.executeUpdate();
        }
    }

    public void updateSynchedState(long folderId, boolean isSynched, LocalDateTime lastSynched) throws SQLException {
        String sql = """
                UPDATE FOLDER SET is_synched = ?, last_synched = ?, updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, isSynched);
            stmt.setTimestamp(2, lastSynched != null ? Timestamp.valueOf(lastSynched) : null);
            stmt.setLong(3, folderId);

            stmt.executeUpdate();
        }
    }

    public void delete(long folderId) throws SQLException {
        String sql = "DELETE FROM FOLDER WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, folderId);
            stmt.executeUpdate();
        }
    }

    public EmailFolder findById(long folderId) throws SQLException {
        String sql = "SELECT * FROM FOLDER WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, folderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<EmailFolder> findByAccountId(long accountId) throws SQLException {
        String sql = "SELECT * FROM FOLDER WHERE account_id = ? ORDER BY name";
        List<EmailFolder> folders = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, accountId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    folders.add(mapRow(rs));
                }
            }
        }

        return folders;
    }

    public List<EmailFolder> findUnsynched(long accountId) throws SQLException {
        String sql = "SELECT * FROM FOLDER WHERE account_id = ? AND is_synched = FALSE ORDER BY name";
        List<EmailFolder> folders = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, accountId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    folders.add(mapRow(rs));
                }
            }
        }

        return folders;
    }

    public void updateMessageCount(long folderId, int messageCount) throws SQLException {
        String sql = "UPDATE FOLDER SET message_count = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, messageCount);
            stmt.setLong(2, folderId);

            stmt.executeUpdate();
        }
    }

    private EmailFolder mapRow(ResultSet rs) throws SQLException {
        EmailFolder folder = new EmailFolder();
        folder.setId(rs.getLong("id"));
        folder.setAccountId(rs.getLong("account_id"));
        folder.setName(rs.getString("name"));
        folder.setPath(rs.getString("path"));
        folder.setUniqueIdPrefix(rs.getString("unique_id_prefix"));
        folder.setMessageCount(rs.getInt("message_count"));
        folder.setSubscribed(rs.getBoolean("is_subscribed"));
        folder.setSynched(rs.getBoolean("is_synched"));

        Timestamp lastSynched = rs.getTimestamp("last_synched");
        if (lastSynched != null) {
            folder.setLastSynched(lastSynched.toLocalDateTime());
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            folder.setCreatedAt(createdAt.toLocalDateTime());
        }

        return folder;
    }
}
