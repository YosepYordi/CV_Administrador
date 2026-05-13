package com.cvmanager.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {
    private static final Properties PROPS = load();

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        loadDriver();
        return DriverManager.getConnection(
                env("DB_URL", PROPS.getProperty("db.url")),
                env("DB_USER", PROPS.getProperty("db.username")),
                env("DB_PASSWORD", PROPS.getProperty("db.password"))
        );
    }

    public static Connection getServerConnection() throws SQLException {
        loadDriver();
        String host = env("DB_HOST", "localhost");
        String port = env("DB_PORT", "3306");
        String serverUrl = env("DB_SERVER_URL", "jdbc:mysql://" + host + ":" + port + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima&characterEncoding=UTF-8");
        return DriverManager.getConnection(
                serverUrl,
                env("DB_USER", PROPS.getProperty("db.username")),
                env("DB_PASSWORD", PROPS.getProperty("db.password"))
        );
    }

    public static String property(String key, String defaultValue) {
        if ("db.name".equals(key)) return env("DB_NAME", PROPS.getProperty(key, defaultValue));
        if ("admin.email".equals(key)) return env("ADMIN_EMAIL", PROPS.getProperty(key, defaultValue));
        if ("admin.password".equals(key)) return env("ADMIN_PASSWORD", PROPS.getProperty(key, defaultValue));
        return PROPS.getProperty(key, defaultValue);
    }

    private static void loadDriver() throws SQLException {
        try {
            Class.forName(PROPS.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        } catch (ClassNotFoundException ex) {
            throw new SQLException("No se encontro el driver MySQL.", ex);
        }
    }

    private static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) props.load(input);
        } catch (IOException ignored) {
        }
        return props;
    }
}
