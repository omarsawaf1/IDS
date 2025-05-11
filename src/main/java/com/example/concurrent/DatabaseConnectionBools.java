package com.example.concurrent;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import javax.sql.DataSource;

public class DatabaseConnectionBools {
    private static final Dotenv dotenv = Dotenv.load();
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();

        String dbHost = dotenv.get("DB_HOST");
        String dbPort = dotenv.get("DB_PORT");
        String dbName = dotenv.get("DB_NAME");
        String dbUser = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");

        if (dbHost == null || dbPort == null || dbName == null || dbUser == null || dbPassword == null) {
            throw new IllegalStateException("Database configuration environment variables are not set.");
        }

        config.setJdbcUrl("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(10);

        dataSource = new HikariDataSource(config);

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
                System.out.println("HikariCP shutdown completed.");
            }
        }));
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
