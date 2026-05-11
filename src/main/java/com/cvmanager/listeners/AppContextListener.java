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

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger logger = LogManager.getLogger(AppContextListener.class);

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
        String password = DBConnection.property("admin.password", "admin123");
        try (Connection cn = DBConnection.getConnection()) {
            try (PreparedStatement ps = cn.prepareStatement("SELECT COUNT(*) FROM usuarios WHERE email=?")) {
                ps.setString(1, email);
                try (var rs = ps.executeQuery()) {
                    if (rs.next() && rs.getLong(1) > 0) return;
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
}
