package com.terramail.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database connection configuration and connection pool.
 */
public class DatabaseConfig {
    private static final Logger logger = Logger.getLogger(DatabaseConfig.class.getName());
    private static volatile DatabaseConfig instance;

    private final String url;
    private final String username;
    private final String password;
    private final int poolSize;
    private final List<Connection> connectionPool;
    private boolean initialized = false;

    private DatabaseConfig() {
        this.url = AppConfig.getDbUrl();
        this.username = AppConfig.getDbUsername();
        this.password = AppConfig.getDbPassword();
        this.poolSize = AppConfig.getDbPoolSize();
        this.connectionPool = new CopyOnWriteArrayList<>();
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    public void initialize() throws SQLException {
        if (initialized) {
            return;
        }

        // Load MySQL driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found", e);
        }

        // Pre-populate connection pool
        for (int i = 0; i < poolSize; i++) {
            try {
                Connection conn = DriverManager.getConnection(url, username, password);
                connectionPool.add(conn);
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Failed to create connection " + i, e);
            }
        }

        initialized = true;
        logger.info("Database configuration initialized with pool size: " + connectionPool.size());
    }

    public Connection getConnection() throws SQLException {
        if (!initialized) {
            initialize();
        }

        synchronized (connectionPool) {
            for (int i = connectionPool.size() - 1; i >= 0; i--) {
                Connection conn = connectionPool.get(i);
                if (conn != null && !conn.isClosed() && conn.isValid(5)) {
                    connectionPool.remove(i);
                    connectionPool.add(conn); // Move to end for round-robin
                    return conn;
                }
            }

            // If no valid connections, create a new one
            if (connectionPool.size() < poolSize) {
                Connection conn = DriverManager.getConnection(url, username, password);
                connectionPool.add(conn);
                return conn;
            }

            throw new SQLException("Unable to obtain connection from pool");
        }
    }

    public void returnConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true);
                }
                connectionPool.add(conn);
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error returning connection to pool", e);
            }
        }
    }

    public void closeAllConnections() {
        synchronized (connectionPool) {
            for (Connection conn : connectionPool) {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Error closing connection", e);
                }
            }
            connectionPool.clear();
        }
        initialized = false;
        logger.info("All database connections closed");
    }

    public boolean isInitialized() {
        return initialized;
    }

    public int getActiveConnectionCount() {
        return connectionPool.size();
    }
}
