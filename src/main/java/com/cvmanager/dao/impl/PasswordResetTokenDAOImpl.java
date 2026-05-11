package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.PasswordResetTokenDAO;
import com.cvmanager.models.PasswordResetToken;
import com.cvmanager.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class PasswordResetTokenDAOImpl extends JdbcSupport implements PasswordResetTokenDAO {
    @Override
    public Long create(Long userId, String tokenHash, LocalDateTime expiresAt) throws SQLException {
        String sql = "INSERT INTO password_reset_tokens (user_id, token_hash, expires_at, created_at) VALUES (?, ?, ?, NOW())";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setString(2, tokenHash);
            ps.setTimestamp(3, Timestamp.valueOf(expiresAt));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return null;
    }

    @Override
    public Optional<PasswordResetToken> findValidByTokenHash(String tokenHash, LocalDateTime now) throws SQLException {
        String sql = "SELECT * FROM password_reset_tokens WHERE token_hash = ? AND used_at IS NULL AND expires_at > ?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tokenHash);
            ps.setTimestamp(2, Timestamp.valueOf(now));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public boolean markUsed(Long tokenId, LocalDateTime usedAt) throws SQLException {
        String sql = "UPDATE password_reset_tokens SET used_at = ? WHERE token_id = ? AND used_at IS NULL";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(usedAt));
            ps.setLong(2, tokenId);
            return ps.executeUpdate() > 0;
        }
    }

    private PasswordResetToken map(ResultSet rs) throws SQLException {
        PasswordResetToken token = new PasswordResetToken();
        token.setTokenId(rs.getLong("token_id"));
        token.setUserId(rs.getLong("user_id"));
        token.setTokenHash(rs.getString("token_hash"));
        token.setExpiresAt(dateTime(rs, "expires_at"));
        token.setUsedAt(dateTime(rs, "used_at"));
        token.setCreatedAt(dateTime(rs, "created_at"));
        return token;
    }
}
