package com.terramail.repository;

import com.terramail.config.DatabaseConfig;
import com.terramail.model.Attachment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Repository for Attachment entity operations.
 */
public class AttachmentRepository {
    private static final Logger logger = Logger.getLogger(AttachmentRepository.class.getName());
    private final DatabaseConfig dbConfig;

    public AttachmentRepository() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    public Attachment create(Attachment attachment) throws SQLException {
        String sql = "INSERT INTO ATTACHMENT (message_id, filename, content_type, size_bytes, storage_path, charset) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, attachment.getMessageId());
            stmt.setString(2, attachment.getFilename());
            stmt.setString(3, attachment.getContentType());
            stmt.setLong(4, attachment.getSizeBytes() != null ? attachment.getSizeBytes() : 0);
            stmt.setString(5, attachment.getStoragePath());
            stmt.setString(6, attachment.getCharset());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    attachment.setId(generatedKeys.getLong("id"));
                }
            }
        }

        return attachment;
    }

    public List<Attachment> createBatch(List<Attachment> attachments) throws SQLException {
        String sql = "INSERT INTO ATTACHMENT (message_id, filename, content_type, size_bytes, storage_path, charset) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        List<Attachment> created = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Attachment attachment : attachments) {
                stmt.setLong(1, attachment.getMessageId());
                stmt.setString(2, attachment.getFilename());
                stmt.setString(3, attachment.getContentType());
                stmt.setLong(4, attachment.getSizeBytes() != null ? attachment.getSizeBytes() : 0);
                stmt.setString(5, attachment.getStoragePath());
                stmt.setString(6, attachment.getCharset());
                stmt.addBatch();
            }

            stmt.executeBatch();

            // Fetch created attachments
            for (Attachment attachment : attachments) {
                List<Attachment> found = findByMessageId(attachment.getMessageId());
                for (Attachment a : found) {
                    if (a.getFilename().equals(attachment.getFilename())) {
                        a.setId(attachment.getId());
                        created.add(a);
                        break;
                    }
                }
            }
        }

        return created;
    }

    public List<Attachment> findByMessageId(long messageId) throws SQLException {
        String sql = "SELECT * FROM ATTACHMENT WHERE message_id = ? ORDER BY filename";
        List<Attachment> attachments = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, messageId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attachments.add(mapRow(rs));
                }
            }
        }

        return attachments;
    }

    public void deleteByMessageId(long messageId) throws SQLException {
        String sql = "DELETE FROM ATTACHMENT WHERE message_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, messageId);
            stmt.executeUpdate();
        }
    }

    public void delete(long attachmentId) throws SQLException {
        String sql = "DELETE FROM ATTACHMENT WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, attachmentId);
            stmt.executeUpdate();
        }
    }

    private Attachment mapRow(ResultSet rs) throws SQLException {
        Attachment attachment = new Attachment();
        attachment.setId(rs.getLong("id"));
        attachment.setMessageId(rs.getLong("message_id"));
        attachment.setFilename(rs.getString("filename"));
        attachment.setContentType(rs.getString("content_type"));

        Long sizeBytes = rs.getLong("size_bytes");
        if (!rs.wasNull()) {
            attachment.setSizeBytes(sizeBytes);
        }

        attachment.setStoragePath(rs.getString("storage_path"));
        attachment.setCharset(rs.getString("charset"));

        return attachment;
    }
}
