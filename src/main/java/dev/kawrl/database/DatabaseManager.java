package dev.kawrl.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages the HikariCP connection pool.
 * Call {@link #initialize(Dotenv)} once at bot startup,
 * then use {@link #getConnection()} anywhere you need a DB connection.
 * Always close connections in a try-with-resources block.
 */
public class DatabaseManager {
    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    private static HikariDataSource dataSource;

    private DatabaseManager() {}

    public static void initialize(Dotenv dotenv) {
        if (dataSource != null) {
            log.warn("DatabaseManager is already initialized — skipping.");
            return;
        }

        String host     = requireEnv(dotenv, "DB_HOST");
        String port     = requireEnv(dotenv, "DB_PORT");
        String dbName   = requireEnv(dotenv, "DB_NAME");
        String user     = requireEnv(dotenv, "DB_USER");
        String password = requireEnv(dotenv, "DB_PASSWORD");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC", host, port, dbName));
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30_000);
        config.setConnectionTimeout(10_000);
        config.setPoolName("BotPool");

        dataSource = new HikariDataSource(config);
        log.info("HikariCP pool initialized — connected to {}/{}", host, dbName);
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("DatabaseManager has not been initialized. Call initialize() first.");
        }
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("HikariCP pool closed.");
        }
    }

    private static String requireEnv(Dotenv dotenv, String key) {
        String value = dotenv.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + key);
        }
        return value;
    }
}
