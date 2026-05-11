package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.AnalyticsDAO;
import com.cvmanager.models.DashboardStats;
import com.cvmanager.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnalyticsDAOImpl implements AnalyticsDAO {
    @Override
    public DashboardStats loadDashboardStats() throws SQLException {
        try (Connection cn = DBConnection.getConnection()) {
            DashboardStats stats = new DashboardStats();
            stats.setTotalUsers(count(cn, "SELECT COUNT(*) FROM usuarios"));
            stats.setTotalGraduates(count(cn, "SELECT COUNT(*) FROM egresados"));
            stats.setTotalCompanies(count(cn, "SELECT COUNT(*) FROM companies"));
            stats.setTotalCvs(count(cn, "SELECT COUNT(*) FROM cvs"));
            stats.setTotalPublishedCvs(count(cn, "SELECT COUNT(*) FROM cvs WHERE is_published = TRUE"));
            stats.setDraftCvs(count(cn, "SELECT COUNT(*) FROM cvs WHERE is_published = FALSE"));
            stats.setActiveCareers(count(cn, "SELECT COUNT(*) FROM careers WHERE is_active = TRUE"));
            stats.setTotalViews(count(cn, "SELECT COALESCE(SUM(views_count), 0) FROM cvs"));
            stats.setTotalFavorites(count(cn, "SELECT COUNT(*) FROM company_favorites"));
            stats.setTotalContactRequests(count(cn, "SELECT COUNT(*) FROM contact_requests"));
            stats.setTotalSearches(count(cn, "SELECT COUNT(*) FROM audit_logs WHERE action = 'company.search'"));
            stats.setRoleCounts(group(cn, "SELECT role AS label, COUNT(*) AS total FROM usuarios GROUP BY role ORDER BY role"));
            stats.setStatusCounts(group(cn, "SELECT status AS label, COUNT(*) AS total FROM usuarios GROUP BY status ORDER BY status"));
            stats.setGraduatesByCareer(group(cn,
                    "SELECT COALESCE(c.name, 'Sin carrera') AS label, COUNT(*) AS total " +
                    "FROM egresados e LEFT JOIN careers c ON c.career_id = e.career_id " +
                    "GROUP BY COALESCE(c.name, 'Sin carrera') ORDER BY total DESC, label"));
            Map<String, Long> requestStatus = group(cn,
                    "SELECT status AS label, COUNT(*) AS total FROM contact_requests GROUP BY status ORDER BY status");
            stats.setContactRequestsByStatus(requestStatus);
            stats.setPendingContactRequests(requestStatus.getOrDefault("pending", 0L));
            stats.setAcceptedContactRequests(requestStatus.getOrDefault("accepted", 0L));
            stats.setRejectedContactRequests(requestStatus.getOrDefault("rejected", 0L));
            return stats;
        }
    }

    private long count(Connection cn, String sql) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        }
    }

    private Map<String, Long> group(Connection cn, String sql) throws SQLException {
        Map<String, Long> values = new LinkedHashMap<>();
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                values.put(rs.getString("label"), rs.getLong("total"));
            }
        }
        return values;
    }
}
