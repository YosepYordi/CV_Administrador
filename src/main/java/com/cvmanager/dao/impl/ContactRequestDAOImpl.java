package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.ContactRequestDAO;
import com.cvmanager.models.ContactRequest;
import com.cvmanager.utils.DBConnection;
import com.cvmanager.utils.ValidacionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
    public List<ContactRequest> findByGraduateId(Long graduateId) throws SQLException {
        String sql = "SELECT cr.*, COALESCE(c.company_name, u.email) AS company_name, u.email AS company_email " +
                "FROM contact_requests cr " +
                "JOIN companies c ON c.company_id = cr.company_id " +
                "JOIN usuarios u ON u.user_id = c.user_id " +
                "WHERE cr.graduate_id = ? " +
                "ORDER BY cr.created_at DESC";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, graduateId);
            List<ContactRequest> requests = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) requests.add(map(rs));
            }
            return requests;
        }
    }

    @Override
    public List<ContactRequest> findByCompanyId(Long companyId) throws SQLException {
        String sql = "SELECT cr.*, CONCAT(e.first_name, ' ', e.last_name) AS graduate_name, u.email AS graduate_email, e.phone AS graduate_phone " +
                "FROM contact_requests cr " +
                "JOIN egresados e ON e.graduate_id = cr.graduate_id " +
                "JOIN usuarios u ON u.user_id = e.user_id " +
                "WHERE cr.company_id = ? " +
                "ORDER BY cr.created_at DESC";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, companyId);
            List<ContactRequest> requests = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) requests.add(map(rs));
            }
            return requests;
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

    @Override
    public Long createForCompany(Long graduateId, Long companyId, String message) throws SQLException {
        String sql = "INSERT INTO contact_requests (company_id, graduate_id, message, status, created_at) " +
                "SELECT c.company_id, e.graduate_id, ?, 'pending', NOW() " +
                "FROM companies c JOIN egresados e ON e.graduate_id = ? " +
                "WHERE c.company_id = ? AND e.is_public = TRUE";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ValidacionUtil.isBlank(message) ? null : ValidacionUtil.sanitize(message));
            ps.setLong(2, graduateId);
            ps.setLong(3, companyId);
            if (ps.executeUpdate() == 0) return null;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean updateStatusForGraduate(Long requestId, Long graduateId, ContactRequest.Status status) throws SQLException {
        if (status == null || status == ContactRequest.Status.PENDING) return false;
        String sql = "UPDATE contact_requests SET status = ? WHERE request_id = ? AND graduate_id = ?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, status.getValue());
            ps.setLong(2, requestId);
            ps.setLong(3, graduateId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatusForCompany(Long requestId, Long companyId, ContactRequest.Status status) throws SQLException {
        if (status == null || status == ContactRequest.Status.PENDING) return false;
        String sql = "UPDATE contact_requests SET status = ? WHERE request_id = ? AND company_id = ?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, status.getValue());
            ps.setLong(2, requestId);
            ps.setLong(3, companyId);
            return ps.executeUpdate() > 0;
        }
    }

    private ContactRequest map(ResultSet rs) throws SQLException {
        ContactRequest request = new ContactRequest();
        request.setRequestId(rs.getLong("request_id"));
        request.setCompanyId(rs.getLong("company_id"));
        request.setGraduateId(rs.getLong("graduate_id"));
        request.setCompanyName(readOptional(rs, "company_name"));
        request.setCompanyEmail(readOptional(rs, "company_email"));
        request.setGraduateName(readOptional(rs, "graduate_name"));
        request.setGraduateEmail(readOptional(rs, "graduate_email"));
        request.setGraduatePhone(readOptional(rs, "graduate_phone"));
        request.setMessage(rs.getString("message"));
        request.setStatus(ContactRequest.Status.from(rs.getString("status")));
        request.setCreatedAt(dateTime(rs, "created_at"));
        return request;
    }

    private String readOptional(ResultSet rs, String column) {
        try {
            return rs.getString(column);
        } catch (SQLException ex) {
            return null;
        }
    }
}
