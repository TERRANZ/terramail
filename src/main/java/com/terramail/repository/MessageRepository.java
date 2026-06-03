package com.terramail.repository;

import com.terramail.config.DatabaseConfig;
import com.terramail.model.EmailMessage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository for EmailMessage entity operations.
 */
public class MessageRepository {
    private static final Logger logger = Logger.getLogger(MessageRepository.class.getName());
    private final DatabaseConfig dbConfig;

    public MessageRepository() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    public EmailMessage create(EmailMessage message) throws SQLException {
        String sql = """
                INSERT INTO MESSAGE (folder_id, message_id, in_reply_to, subject, body_plain, body_html,
                                    received_date, sent_date, is_read, is_flagged, is_deleted,
                                    has_attachments, attachment_count, size_bytes)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, message.getFolderId());
            stmt.setString(2, message.getMessageId());
            stmt.setString(3, message.getInReplyTo());
            stmt.setString(4, message.getSubject());
            stmt.setString(5, message.getBodyPlain());
            stmt.setString(6, message.getBodyHtml());
            stmt.setTimestamp(7, message.getReceivedDate() != null ? Timestamp.valueOf(message.getReceivedDate()) : null);
            stmt.setTimestamp(8, message.getSentDate() != null ? Timestamp.valueOf(message.getSentDate()) : null);
            stmt.setBoolean(9, message.isRead());
            stmt.setBoolean(10, message.isFlagged());
            stmt.setBoolean(11, message.isDeleted());
            stmt.setBoolean(12, message.hasAttachments());
            stmt.setInt(13, message.getAttachmentCount());
            stmt.setObject(14, message.getSizeBytes());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    message.setId(generatedKeys.getLong("id"));
                }
            }
        }

        logger.log(Level.FINE, "Created message: {0}", message.getMessageId());
        return message;
    }

    public void update(EmailMessage message) throws SQLException {
        String sql = """
                UPDATE MESSAGE SET is_read = ?, is_flagged = ?, is_deleted = ?,
                                   has_attachments = ?, attachment_count = ?, subject = ?,
                                   body_plain = ?, body_html = ?
                WHERE id = ?
                """;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, message.isRead());
            stmt.setBoolean(2, message.isFlagged());
            stmt.setBoolean(3, message.isDeleted());
            stmt.setBoolean(4, message.hasAttachments());
            stmt.setInt(5, message.getAttachmentCount());
            stmt.setString(6, message.getSubject());
            stmt.setString(7, message.getBodyPlain());
            stmt.setString(8, message.getBodyHtml());
            stmt.setLong(9, message.getId());

            stmt.executeUpdate();
        }
    }

    public void updateReadState(long messageId, boolean isRead) throws SQLException {
        String sql = "UPDATE MESSAGE SET is_read = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, isRead);
            stmt.setLong(2, messageId);

            stmt.executeUpdate();
        }
    }

    public void updateFlaggedState(long messageId, boolean isFlagged) throws SQLException {
        String sql = "UPDATE MESSAGE SET is_flagged = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, isFlagged);
            stmt.setLong(2, messageId);

            stmt.executeUpdate();
        }
    }

    public void delete(long messageId) throws SQLException {
        String sql = "DELETE FROM MESSAGE WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, messageId);
            stmt.executeUpdate();
        }
    }

    public EmailMessage findById(long messageId) throws SQLException {
        String sql = "SELECT * FROM MESSAGE WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, messageId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public EmailMessage findByMessageId(String messageId) throws SQLException {
        String sql = "SELECT * FROM MESSAGE WHERE message_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, messageId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<EmailMessage> findByFolderId(long folderId, int offset, int limit) throws SQLException {
        String sql = "SELECT * FROM MESSAGE WHERE folder_id = ? AND is_deleted = FALSE ORDER BY received_date DESC LIMIT ? OFFSET ?";
        List<EmailMessage> messages = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, folderId);
            stmt.setInt(2, limit);
            stmt.setLong(3, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRow(rs));
                }
            }
        }

        return messages;
    }

    public List<EmailMessage> findByFolderIdUnread(long folderId) throws SQLException {
        String sql = "SELECT * FROM MESSAGE WHERE folder_id = ? AND is_read = FALSE AND is_deleted = FALSE ORDER BY received_date DESC";
        List<EmailMessage> messages = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, folderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRow(rs));
                }
            }
        }

        return messages;
    }

    public List<EmailMessage> searchBySubject(long folderId, String query) throws SQLException {
        String sql = "SELECT * FROM MESSAGE WHERE folder_id = ? AND is_deleted = FALSE AND subject LIKE ? ORDER BY received_date DESC LIMIT 100";
        List<EmailMessage> messages = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, folderId);
            stmt.setString(2, "%" + query + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRow(rs));
                }
            }
        }

        return messages;
    }

    public int countByFolderId(long folderId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM MESSAGE WHERE folder_id = ? AND is_deleted = FALSE";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, folderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    public int countUnreadByFolderId(long folderId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM MESSAGE WHERE folder_id = ? AND is_read = FALSE AND is_deleted = FALSE";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, folderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    private EmailMessage mapRow(ResultSet rs) throws SQLException {
        EmailMessage message = new EmailMessage();
        message.setId(rs.getLong("id"));
        message.setFolderId(rs.getLong("folder_id"));
        message.setMessageId(rs.getString("message_id"));
        message.setInReplyTo(rs.getString("in_reply_to"));
        message.setSubject(rs.getString("subject"));
        message.setBodyPlain(rs.getString("body_plain"));
        message.setBodyHtml(rs.getString("body_html"));

        Timestamp receivedDate = rs.getTimestamp("received_date");
        if (receivedDate != null) {
            message.setReceivedDate(receivedDate.toLocalDateTime());
        }

        Timestamp sentDate = rs.getTimestamp("sent_date");
        if (sentDate != null) {
            message.setSentDate(sentDate.toLocalDateTime());
        }

        message.setRead(rs.getBoolean("is_read"));
        message.setFlagged(rs.getBoolean("is_flagged"));
        message.setDeleted(rs.getBoolean("is_deleted"));
        message.setHasAttachments(rs.getBoolean("has_attachments"));
        message.setAttachmentCount(rs.getInt("attachment_count"));

        Long sizeBytes = rs.getLong("size_bytes");
        if (!rs.wasNull()) {
            message.setSizeBytes(sizeBytes);
        }

        Timestamp localReceivedAt = rs.getTimestamp("local_received_at");
        if (localReceivedAt != null) {
            message.setLocalReceivedAt(localReceivedAt.toLocalDateTime());
        }

        return message;
    }
}
