package com.terramail.repository;

import com.terramail.config.DatabaseConfig;
import com.terramail.model.Recipient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Repository for Recipient entity operations.
 */
public class RecipientRepository {
    private static final Logger logger = Logger.getLogger(RecipientRepository.class.getName());
    private final DatabaseConfig dbConfig;

    public RecipientRepository() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    public Recipient create(Recipient recipient) throws SQLException {
        String sql = "INSERT INTO RECIPIENT (message_id, email, name, type) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, recipient.getMessageId());
            stmt.setString(2, recipient.getEmail());
            stmt.setString(3, recipient.getName());
            stmt.setString(4, recipient.getType());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    recipient.setId(generatedKeys.getLong("id"));
                }
            }
        }

        return recipient;
    }

    public List<Recipient> createBatch(List<Recipient> recipients) throws SQLException {
        String sql = "INSERT INTO RECIPIENT (message_id, email, name, type) VALUES (?, ?, ?, ?)";
        List<Recipient> created = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.addBatch();

            for (Recipient recipient : recipients) {
                stmt.setLong(1, recipient.getMessageId());
                stmt.setString(2, recipient.getEmail());
                stmt.setString(3, recipient.getName());
                stmt.setString(4, recipient.getType());
                stmt.addBatch();
            }

            stmt.executeBatch();

            // Now fetch the created recipients
            for (Recipient recipient : recipients) {
                List<Recipient> found = findByMessageIdAndEmail(
                        recipient.getMessageId(), recipient.getEmail());
                if (!found.isEmpty()) {
                    created.addAll(found);
                }
            }
        }

        return created;
    }

    public List<Recipient> findByMessageId(long messageId) throws SQLException {
        String sql = "SELECT * FROM RECIPIENT WHERE message_id = ? ORDER BY type, email";
        List<Recipient> recipients = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, messageId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recipients.add(mapRow(rs));
                }
            }
        }

        return recipients;
    }

    public List<Recipient> findByMessageIdAndEmail(long messageId, String email) throws SQLException {
        String sql = "SELECT * FROM RECIPIENT WHERE message_id = ? AND email = ?";
        List<Recipient> recipients = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, messageId);
            stmt.setString(2, email);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recipients.add(mapRow(rs));
                }
            }
        }

        return recipients;
    }

    public void deleteByMessageId(long messageId) throws SQLException {
        String sql = "DELETE FROM RECIPIENT WHERE message_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, messageId);
            stmt.executeUpdate();
        }
    }

    private Recipient mapRow(ResultSet rs) throws SQLException {
        Recipient recipient = new Recipient();
        recipient.setId(rs.getLong("id"));
        recipient.setMessageId(rs.getLong("message_id"));
        recipient.setEmail(rs.getString("email"));
        recipient.setName(rs.getString("name"));
        recipient.setType(rs.getString("type"));
        return recipient;
    }
}
