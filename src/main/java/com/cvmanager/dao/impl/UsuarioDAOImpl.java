package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.UsuarioDAO;
import com.cvmanager.models.User;
import com.cvmanager.utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAOImpl extends JdbcSupport implements UsuarioDAO {
    @Override
    public Optional<User> findById(Long id) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM usuarios WHERE user_id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM usuarios WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM usuarios ORDER BY created_at DESC")) {
            return list(ps);
        }
    }

    @Override
    public List<User> findByRoleAndStatus(String role, String status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM usuarios WHERE 1=1");
        List<String> params = new ArrayList<>();
        if (role != null && !role.isBlank()) { sql.append(" AND role = ?"); params.add(role); }
        if (status != null && !status.isBlank()) { sql.append(" AND status = ?"); params.add(status); }
        sql.append(" ORDER BY created_at DESC");
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setString(i + 1, params.get(i));
            return list(ps);
        }
    }

    @Override
    public Long create(User user) throws SQLException {
        String sql = "INSERT INTO usuarios (email, password_hash, role, status, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole().getValue());
            ps.setString(4, user.getStatus().getValue());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE usuarios SET email = ?, password_hash = ?, role = ?, status = ?, updated_at = NOW() WHERE user_id = ?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole().getValue());
            ps.setString(4, user.getStatus().getValue());
            ps.setLong(5, user.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePasswordHash(Long userId, String passwordHash) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE usuarios SET password_hash = ?, updated_at = NOW() WHERE user_id = ?")) {
            ps.setString(1, passwordHash);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(Long userId, User.Status status) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE usuarios SET status = ?, updated_at = NOW() WHERE user_id = ?")) {
            ps.setString(1, status.getValue());
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateLastLogin(Long userId, LocalDateTime loginAt) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE usuarios SET last_login = ? WHERE user_id = ?")) {
            ps.setTimestamp(1, Timestamp.valueOf(loginAt));
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public long countByRole(User.Role role) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT COUNT(*) FROM usuarios WHERE role = ?")) {
            ps.setString(1, role.getValue());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        }
    }

    private List<User> list(PreparedStatement ps) throws SQLException {
        List<User> users = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(map(rs));
        }
        return users;
    }

    private User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(User.Role.from(rs.getString("role")));
        user.setStatus(User.Status.from(rs.getString("status")));
        user.setCreatedAt(dateTime(rs, "created_at"));
        user.setUpdatedAt(dateTime(rs, "updated_at"));
        user.setLastLogin(dateTime(rs, "last_login"));
        return user;
    }
}
