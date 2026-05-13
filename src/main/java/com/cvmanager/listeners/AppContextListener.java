package com.cvmanager.listeners;

import com.cvmanager.models.User;
import com.cvmanager.utils.DBConnection;
import com.cvmanager.utils.PasswordUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger logger = LogManager.getLogger(AppContextListener.class);
    private static final List<String> UNSAFE_ADMIN_PASSWORDS = List.of(
            "admin123",
            "change_me",
            "changeme",
            "password",
            "password123",
            "admin",
            "12345678"
    );

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            initializeSchema();
            seedAdmin();
        } catch (Exception ex) {
            logger.warn("No se pudo inicializar la base de datos automaticamente: {}", ex.getMessage());
            sce.getServletContext().setAttribute("databaseWarning", "MySQL no esta disponible o no acepto la configuracion actual.");
        }
    }

    private void initializeSchema() throws Exception {
        String dbName = DBConnection.property("db.name", "CV_Administrador");
        try (Connection server = DBConnection.getServerConnection(); Statement st = server.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("schema.sql");
             Connection cn = DBConnection.getConnection();
             Statement st = cn.createStatement()) {
            if (input == null) return;
            String sql = new String(input.readAllBytes(), StandardCharsets.UTF_8).replace("CV_Administrador", dbName);
            for (String statement : sql.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) executeSchemaStatement(st, trimmed);
            }
        }
    }

    private void executeSchemaStatement(Statement st, String sql) throws SQLException {
        try {
            st.execute(sql);
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1061) {
                logger.debug("Indice ya existente al inicializar schema: {}", ex.getMessage());
                return;
            }
            throw ex;
        }
    }

    private void seedAdmin() throws Exception {
        String email = DBConnection.property("admin.email", "admin@instituto.edu.pe");
        String password = requireConfiguredAdminPassword(DBConnection.property("admin.password", null));
        try (Connection cn = DBConnection.getConnection()) {
            try (PreparedStatement ps = cn.prepareStatement("SELECT user_id, password_hash FROM usuarios WHERE email=?")) {
                ps.setString(1, email);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        rotateUnsafeExistingAdminPassword(cn, rs.getLong("user_id"), rs.getString("password_hash"), password);
                        return;
                    }
                }
            }
            try (PreparedStatement ps = cn.prepareStatement("INSERT INTO usuarios (email,password_hash,role,status,created_at,updated_at) VALUES (?,?,?,?,NOW(),NOW())")) {
                ps.setString(1, email);
                ps.setString(2, PasswordUtil.hashPassword(password));
                ps.setString(3, User.Role.ADMIN.getValue());
                ps.setString(4, User.Status.ACTIVE.getValue());
                ps.executeUpdate();
            }
        }
    }

    private void rotateUnsafeExistingAdminPassword(Connection cn, Long userId, String passwordHash, String configuredPassword) throws SQLException {
        if (!usesUnsafeAdminPasswordHash(passwordHash)) return;
        try (PreparedStatement ps = cn.prepareStatement("UPDATE usuarios SET password_hash=?, updated_at=NOW() WHERE user_id=?")) {
            ps.setString(1, PasswordUtil.hashPassword(configuredPassword));
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
        logger.warn("Se roto el password del administrador bootstrap porque usaba un valor por defecto conocido.");
    }

    static String requireConfiguredAdminPassword(String password) {
        String value = password == null ? "" : password.trim();
        if (value.isEmpty() || isUnsafeAdminPassword(value)) {
            throw new IllegalStateException("Debe configurar admin.password con un valor fuerte y no predeterminado.");
        }
        return value;
    }

    private static boolean isUnsafeAdminPassword(String password) {
        String value = password.toLowerCase(Locale.ROOT);
        return UNSAFE_ADMIN_PASSWORDS.contains(value);
    }

    static boolean usesUnsafeAdminPasswordHash(String passwordHash) {
        for (String password : UNSAFE_ADMIN_PASSWORDS) {
            if (PasswordUtil.verifyPassword(password, passwordHash)) return true;
        }
        return false;
    }
}
