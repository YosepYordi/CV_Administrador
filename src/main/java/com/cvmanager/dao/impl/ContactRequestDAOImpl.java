package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.ContactRequestDAO;
import com.cvmanager.models.ContactRequest;
import com.cvmanager.utils.DBConnection;
import com.cvmanager.utils.ValidacionUtil;

import java.sql.*;
import java.util.Optional;

public class ContactRequestDAOImpl extends JdbcSupport implements ContactRequestDAO {
    @Override
    public Optional<ContactRequest> findById(Long requestId) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM contact_requests WHERE request_id = ?")) {
            ps.setLong(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public Long createForCv(Long companyId, Long cvId, String message) throws SQLException {
        String sql = "INSERT INTO contact_requests (company_id, graduate_id, message, status, created_at) " +
                "SELECT ?, cv.graduate_id, ?, 'pending', NOW() " +
                "FROM cvs cv JOIN egresados e ON e.graduate_id = cv.graduate_id " +
                "WHERE cv.cv_id = ? AND cv.is_published = TRUE AND e.is_public = TRUE";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, companyId);
            ps.setString(2, ValidacionUtil.isBlank(message) ? null : ValidacionUtil.sanitize(message));
            ps.setLong(3, cvId);
            if (ps.executeUpdate() == 0) return null;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return null;
    }

    private ContactRequest map(ResultSet rs) throws SQLException {
        ContactRequest request = new ContactRequest();
        request.setRequestId(rs.getLong("request_id"));
        request.setCompanyId(rs.getLong("company_id"));
        request.setGraduateId(rs.getLong("graduate_id"));
        request.setMessage(rs.getString("message"));
        request.setStatus(ContactRequest.Status.from(rs.getString("status")));
        request.setCreatedAt(dateTime(rs, "created_at"));
        return request;
    }
}
