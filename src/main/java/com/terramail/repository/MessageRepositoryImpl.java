package com.terramail.repository;

import com.terramail.model.AttachmentInfo;
import com.terramail.model.Message;
import com.terramail.model.SortOrder;
import com.zaxxer.hikari.HikariDataSource;

import java.io.Serializable;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MessageRepositoryImpl implements MessageRepository {

    private static final Logger LOGGER = Logger.getLogger(MessageRepositoryImpl.class.getName());

    private final HikariDataSource dataSource;

    public MessageRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @Override
    public List<Message> findByFolderId(long folderId, SortOrder sortOrder) {
        String sql = "SELECT * FROM messages WHERE folder_id = ? ORDER BY " + getSortColumn(sortOrder.getField()) + " " + sortOrder.getDirection() + ", id DESC";
        LOGGER.info(String.format("[MESSAGE LOADING] Querying messages for folder_id=%d, sort_field=%s, direction=%s", folderId, sortOrder.getField(), sortOrder.getDirection()));
        long startTime = System.currentTimeMillis();
        List<Message> messages = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, folderId);
            LOGGER.fine(String.format("[MESSAGE LOADING] Executing SQL: %s with folderId=%d", sql, folderId));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message msg = mapRow(rs);
                messages.add(msg);
                LOGGER.fine(String.format("[MESSAGE LOADING] Loaded message: id=%d, subject=%s, from=%s, date=%s, seen=%b", msg.getId(), msg.getSubject(), msg.getFrom(), msg.getDate(), msg.isSeen()));
            }
        } catch (SQLException e) {
            LOGGER.severe(String.format("[MESSAGE LOADING] Failed to fetch messages for folder_id=%d: %s", folderId, e.getMessage()));
            throw new RuntimeException("Failed to fetch messages for folder " + folderId, e);
        }
        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info(String.format("[MESSAGE LOADING] Completed loading %d messages for folder_id=%d in %d ms", messages.size(), folderId, duration));
        return messages;
    }

    @Override
    public Message findById(long id) {
        String sql = "SELECT * FROM messages WHERE id = ?";
        LOGGER.info(String.format("[MESSAGE LOADING] Querying message by id=%d", id));
        long startTime = System.currentTimeMillis();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            LOGGER.fine(String.format("[MESSAGE LOADING] Executing SQL: %s with id=%d", sql, id));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Message msg = mapRow(rs);
                LOGGER.fine(String.format("[MESSAGE LOADING] Found message: id=%d, subject=%s, from=%s, date=%s", msg.getId(), msg.getSubject(), msg.getFrom(), msg.getDate()));
                return msg;
            }
        } catch (SQLException e) {
            LOGGER.severe(String.format("[MESSAGE LOADING] Failed to fetch message id=%d: %s", id, e.getMessage()));
            throw new RuntimeException("Failed to fetch message " + id, e);
        }
        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info(String.format("[MESSAGE LOADING] Completed message lookup for id=%d, not found in %d ms", id, duration));
        return null;
    }

    @Override
    public List<Message> findByAccountId(long accountId) {
        String sql = "SELECT m.* FROM messages m JOIN folders f ON m.folder_id = f.id WHERE f.account_id = ?";
        LOGGER.info(String.format("[MESSAGE LOADING] Querying all messages for account_id=%d", accountId));
        long startTime = System.currentTimeMillis();
        List<Message> messages = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            LOGGER.fine(String.format("[MESSAGE LOADING] Executing SQL: %s with accountId=%d", sql, accountId));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message msg = mapRow(rs);
                messages.add(msg);
                LOGGER.fine(String.format("[MESSAGE LOADING] Loaded message: id=%d, subject=%s, folder_id=%d", msg.getId(), msg.getSubject(), msg.getFolderId()));
            }
        } catch (SQLException e) {
            LOGGER.severe(String.format("[MESSAGE LOADING] Failed to fetch messages for account_id=%d: %s", accountId, e.getMessage()));
            throw new RuntimeException("Failed to fetch messages for account " + accountId, e);
        }
        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info(String.format("[MESSAGE LOADING] Completed loading %d messages for account_id=%d in %d ms", messages.size(), accountId, duration));
        return messages;
    }

    @Override
    public Message save(Message message) {
        String sql = "INSERT INTO messages (`folder_id`, `from`, `to`, `cc`, `subject`, `date`, `body`, `seen`, `flagged`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, message.getFolderId());
            stmt.setString(2, message.getFrom());
            stmt.setString(3, message.getTo());
            stmt.setString(4, message.getCc());
            stmt.setString(5, message.getSubject());
            stmt.setObject(6, message.getDate());
            stmt.setString(7, message.getBody());
            stmt.setBoolean(8, message.isSeen());
            stmt.setBoolean(9, message.isFlagged());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    message.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save message", e);
        }
        return message;
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM messages WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete message " + id, e);
        }
    }

    @Override
    public void updateSeen(long id, boolean seen) {
        String sql = "UPDATE messages SET seen = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, seen);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update seen status for message " + id, e);
        }
    }

    @Override
    public void updateFlagged(long id, boolean flagged) {
        String sql = "UPDATE messages SET flagged = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, flagged);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update flagged status for message " + id, e);
        }
    }

    @Override
    public void updateAttachments(long messageId, List<AttachmentInfo> attachments) {
        String sql = "UPDATE messages SET attachments = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, attachmentsToJson(attachments));
            stmt.setLong(2, messageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update attachments for message " + messageId, e);
        }
    }

    @Override
    public long countByFolderId(long folderId) {
        String sql = "SELECT COUNT(*) FROM messages WHERE folder_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, folderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count messages for folder " + folderId, e);
        }
        return 0;
    }

    @Override
    public List<Message> searchBySubject(long folderId, String keyword, SortOrder sortOrder) {
        String sql = "SELECT * FROM messages WHERE folder_id = ? AND subject LIKE ? ORDER BY " + getSortColumn(sortOrder.getField()) + " " + sortOrder.getDirection() + ", id DESC";
        LOGGER.info(String.format("[MESSAGE LOADING] Searching messages in folder_id=%d for keyword=%s, sort_field=%s, direction=%s", folderId, keyword, sortOrder.getField(), sortOrder.getDirection()));
        long startTime = System.currentTimeMillis();
        List<Message> messages = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, folderId);
            stmt.setString(2, "%" + keyword + "%");
            LOGGER.fine(String.format("[MESSAGE LOADING] Executing SQL: %s with folderId=%d, keyword=%s", sql, folderId, keyword));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message msg = mapRow(rs);
                messages.add(msg);
                LOGGER.fine(String.format("[MESSAGE LOADING] Matched message: id=%d, subject=%s", msg.getId(), msg.getSubject()));
            }
        } catch (SQLException e) {
            LOGGER.severe(String.format("[MESSAGE LOADING] Failed to search messages in folder_id=%d for keyword=%s: %s", folderId, keyword, e.getMessage()));
            throw new RuntimeException("Failed to search messages in folder " + folderId, e);
        }
        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info(String.format("[MESSAGE LOADING] Completed search for keyword=%s in folder_id=%d, found %d messages in %d ms", keyword, folderId, messages.size(), duration));
        return messages;
    }

    private Message mapRow(ResultSet rs) throws SQLException {
        Message msg = new Message();
        msg.setId(rs.getLong("id"));
        msg.setFolderId(rs.getLong("folder_id"));
        msg.setFrom(rs.getString("from"));
        msg.setTo(rs.getString("to"));
        msg.setCc(rs.getString("cc"));
        msg.setSubject(rs.getString("subject"));
        Timestamp ts = rs.getTimestamp("date");
        msg.setDate(ts != null ? ts.toInstant() : null);
        msg.setBody(rs.getString("body"));
        msg.setSeen(rs.getBoolean("seen"));
        msg.setFlagged(rs.getBoolean("flagged"));

        Object attachmentsObj = rs.getObject("attachments");
        if (attachmentsObj != null) {
            msg.setAttachments(jsonToAttachments(attachmentsObj));
        }
        return msg;
    }

    private String getSortColumn(SortOrder.Field field) {
        switch (field) {
            case SUBJECT: return "subject";
            case FROM: return "`from`";
            case DATE: return "date";
            case TO: return "to";
            case CC: return "cc";
            default: return "date";
        }
    }

    private Serializable attachmentsToJson(List<AttachmentInfo> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return null;
        }
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < attachments.size(); i++) {
            AttachmentInfo info = attachments.get(i);
            if (i > 0) json.append(",");
            json.append("{\"name\":\"").append(escapeJson(info.getName())).append("\"");
            json.append(",\"size\":").append(info.getSize());
            json.append(",\"contentType\":\"").append(escapeJson(info.getContentType())).append("\"}");
        }
        json.append("]");
        return json.toString();
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private List<AttachmentInfo> jsonToAttachments(Object obj) {
        String json;
        if (obj instanceof String s) {
            json = s;
        } else if (obj instanceof byte[] bytes) {
            json = new String(bytes);
        } else {
            return List.of();
        }
        List<AttachmentInfo> attachments = new ArrayList<>();
        if (json.startsWith("[")) {
            int start = json.indexOf("[") + 1;
            int end = json.lastIndexOf("]");
            String content = json.substring(start, end);
            String[] items = content.split("\\},\\{");
            for (String item : items) {
                String cleaned = item.replace("{", "").replace("}", "");
                String name = extractString(cleaned, "name");
                long size = Long.parseLong(extractLong(cleaned, "size"));
                String contentType = extractString(cleaned, "contentType");
                attachments.add(new AttachmentInfo(name, size, contentType));
            }
        }
        return attachments;
    }

    private String extractString(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern) + pattern.length();
        if (start < pattern.length()) return "";
        int end = json.indexOf("\"", start);
        return json.substring(start, end).replace("\\\\", "\\").replace("\\\"", "\"").replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t");
    }

    private String extractLong(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern) + pattern.length();
        if (start < pattern.length()) return "0";
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return json.substring(start, end).trim();
    }
}
