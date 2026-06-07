package com.terramail.repository;

import com.terramail.model.Folder;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FolderRepositoryImpl implements FolderRepository {

    private static final Logger LOGGER = Logger.getLogger(FolderRepositoryImpl.class.getName());

    private final HikariDataSource dataSource;

    public FolderRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @Override
    public List<Folder> findByAccountId(long accountId) {
        String sql = "SELECT * FROM folders WHERE account_id = ? ORDER BY type ASC, name ASC";
        LOGGER.info(String.format("[FOLDER LOADING] Querying folders for account_id=%d", accountId));
        long startTime = System.currentTimeMillis();
        List<Folder> folders = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            LOGGER.fine(String.format("[FOLDER LOADING] Executing SQL: %s with accountId=%d", sql, accountId));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Folder folder = mapRow(rs);
                folders.add(folder);
                LOGGER.fine(String.format("[FOLDER LOADING] Loaded folder: id=%d, name=%s, type=%s", folder.getId(), folder.getName(), folder.getType()));
            }
        } catch (SQLException e) {
            LOGGER.severe(String.format("[FOLDER LOADING] Failed to fetch folders for account_id=%d: %s", accountId, e.getMessage()));
            throw new RuntimeException("Failed to fetch folders for account " + accountId, e);
        }
        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info(String.format("[FOLDER LOADING] Completed loading %d folders for account_id=%d in %d ms", folders.size(), accountId, duration));
        return folders;
    }

    @Override
    public Folder findById(long id) {
        String sql = "SELECT * FROM folders WHERE id = ?";
        LOGGER.info(String.format("[FOLDER LOADING] Querying folder by id=%d", id));
        long startTime = System.currentTimeMillis();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            LOGGER.fine(String.format("[FOLDER LOADING] Executing SQL: %s with id=%d", sql, id));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Folder folder = mapRow(rs);
                LOGGER.fine(String.format("[FOLDER LOADING] Found folder: id=%d, name=%s, type=%s", folder.getId(), folder.getName(), folder.getType()));
                return folder;
            }
        } catch (SQLException e) {
            LOGGER.severe(String.format("[FOLDER LOADING] Failed to fetch folder id=%d: %s", id, e.getMessage()));
            throw new RuntimeException("Failed to fetch folder " + id, e);
        }
        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info(String.format("[FOLDER LOADING] Completed folder lookup for id=%d, not found in %d ms", id, duration));
        return null;
    }

    @Override
    public Folder save(Folder folder) {
        if (folder.getId() > 0) {
            String sql = "UPDATE folders SET name = ?, type = ?, parent_folder_id = ?, imap_path = ? WHERE id = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, folder.getName());
                stmt.setString(2, folder.getType().name());
                stmt.setLong(3, folder.getParentFolderId());
                stmt.setString(4, folder.getImapPath());
                stmt.setLong(5, folder.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to update folder " + folder.getId(), e);
            }
        } else {
            String sql = "INSERT INTO folders (account_id, name, type, parent_folder_id, imap_path) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, folder.getAccountId());
                stmt.setString(2, folder.getName());
                stmt.setString(3, folder.getType().name());
                stmt.setLong(4, folder.getParentFolderId());
                stmt.setString(5, folder.getImapPath());
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        folder.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to save folder", e);
            }
        }
        return folder;
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM folders WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete folder " + id, e);
        }
    }

    @Override
    public Folder findByName(long accountId, String name) {
        String sql = "SELECT * FROM folders WHERE account_id = ? AND name = ?";
        LOGGER.info(String.format("[FOLDER LOADING] Querying folder by account_id=%d, name=%s", accountId, name));
        long startTime = System.currentTimeMillis();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            stmt.setString(2, name);
            LOGGER.fine(String.format("[FOLDER LOADING] Executing SQL: %s with accountId=%d, name=%s", sql, accountId, name));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Folder folder = mapRow(rs);
                LOGGER.fine(String.format("[FOLDER LOADING] Found folder: id=%d, name=%s, type=%s", folder.getId(), folder.getName(), folder.getType()));
                return folder;
            }
        } catch (SQLException e) {
            LOGGER.severe(String.format("[FOLDER LOADING] Failed to find folder by name account_id=%d, name=%s: %s", accountId, name, e.getMessage()));
            throw new RuntimeException("Failed to find folder by name " + name, e);
        }
        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info(String.format("[FOLDER LOADING] Completed folder lookup for account_id=%d, name=%s, not found in %d ms", accountId, name, duration));
        return null;
    }

    @Override
    public long countByAccountId(long accountId) {
        String sql = "SELECT COUNT(*) FROM folders WHERE account_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count folders for account " + accountId, e);
        }
        return 0;
    }

    @Override
    public List<Folder> findByAccountIdAndParentFolderId(long accountId, long parentFolderId) {
        String sql = "SELECT * FROM folders WHERE account_id = ? AND parent_folder_id = ? ORDER BY type ASC, name ASC";
        LOGGER.info(String.format("[FOLDER LOADING] Querying subfolders for account_id=%d, parent_folder_id=%d", accountId, parentFolderId));
        long startTime = System.currentTimeMillis();
        List<Folder> folders = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            stmt.setLong(2, parentFolderId);
            LOGGER.fine(String.format("[FOLDER LOADING] Executing SQL: %s with accountId=%d, parentFolderId=%d", sql, accountId, parentFolderId));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Folder folder = mapRow(rs);
                folders.add(folder);
                LOGGER.fine(String.format("[FOLDER LOADING] Loaded subfolder: id=%d, name=%s, type=%s", folder.getId(), folder.getName(), folder.getType()));
            }
        } catch (SQLException e) {
            LOGGER.severe(String.format("[FOLDER LOADING] Failed to fetch subfolders for account_id=%d, parent_folder_id=%d: %s", accountId, parentFolderId, e.getMessage()));
            throw new RuntimeException("Failed to fetch subfolders for parent " + parentFolderId, e);
        }
        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info(String.format("[FOLDER LOADING] Completed loading %d subfolders for account_id=%d, parent_folder_id=%d in %d ms", folders.size(), accountId, parentFolderId, duration));
        return folders;
    }

    private Folder mapRow(ResultSet rs) throws SQLException {
        Folder folder = new Folder();
        folder.setId(rs.getLong("id"));
        folder.setAccountId(rs.getLong("account_id"));
        folder.setName(rs.getString("name"));
        folder.setType(Folder.Type.valueOf(rs.getString("type")));
        folder.setParentFolderId(rs.getLong("parent_folder_id"));
        folder.setImapPath(rs.getString("imap_path"));
        return folder;
    }
}
