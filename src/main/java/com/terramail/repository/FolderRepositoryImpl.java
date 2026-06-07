package com.terramail.repository;

import com.terramail.model.Folder;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FolderRepositoryImpl implements FolderRepository {

    private final HikariDataSource dataSource;

    public FolderRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @Override
    public List<Folder> findByAccountId(long accountId) {
        String sql = "SELECT * FROM folders WHERE account_id = ? ORDER BY type ASC, name ASC";
        List<Folder> folders = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                folders.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch folders for account " + accountId, e);
        }
        return folders;
    }

    @Override
    public Folder findById(long id) {
        String sql = "SELECT * FROM folders WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch folder " + id, e);
        }
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
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find folder by name " + name, e);
        }
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
        List<Folder> folders = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            stmt.setLong(2, parentFolderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                folders.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch subfolders for parent " + parentFolderId, e);
        }
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
