package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.AuditLogDAO;
import com.cvmanager.models.AuditLog;
import com.cvmanager.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAOImpl extends JdbcSupport implements AuditLogDAO {
    @Override
    public Long create(AuditLog auditLog) throws SQLException {
        String sql = "INSERT INTO audit_logs (user_id, action, entity_type, entity_id, details, ip_address) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (auditLog.getUserId() == null) ps.setNull(1, java.sql.Types.BIGINT); else ps.setLong(1, auditLog.getUserId());
            ps.setString(2, auditLog.getAction());
            ps.setString(3, auditLog.getEntityType());
            if (auditLog.getEntityId() == null) ps.setNull(4, java.sql.Types.BIGINT); else ps.setLong(4, auditLog.getEntityId());
            ps.setString(5, auditLog.getDetails());
            ps.setString(6, auditLog.getIpAddress());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return null;
    }

    @Override
    public List<AuditLog> findRecent(int limit) throws SQLException {
        String sql = "SELECT * FROM audit_logs ORDER BY created_at DESC LIMIT ?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, Math.max(1, limit));
            List<AuditLog> logs = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) logs.add(map(rs));
            }
            return logs;
        }
    }

    private AuditLog map(ResultSet rs) throws SQLException {
        AuditLog auditLog = new AuditLog();
        auditLog.setLogId(rs.getLong("log_id"));
        long userId = rs.getLong("user_id");
        auditLog.setUserId(rs.wasNull() ? null : userId);
        auditLog.setAction(rs.getString("action"));
        auditLog.setEntityType(rs.getString("entity_type"));
        long entityId = rs.getLong("entity_id");
        auditLog.setEntityId(rs.wasNull() ? null : entityId);
        auditLog.setDetails(rs.getString("details"));
        auditLog.setIpAddress(rs.getString("ip_address"));
        auditLog.setCreatedAt(dateTime(rs, "created_at"));
        return auditLog;
    }
}
