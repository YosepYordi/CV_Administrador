package com.cvmanager.services;

import com.cvmanager.dao.impl.AnalyticsDAOImpl;
import com.cvmanager.dao.interfaces.AnalyticsDAO;
import com.cvmanager.models.DashboardStats;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class DashboardStatsService {
    private final AnalyticsDAO analyticsDAO;

    public DashboardStatsService() {
        this(new AnalyticsDAOImpl());
    }

    public DashboardStatsService(AnalyticsDAO analyticsDAO) {
        this.analyticsDAO = analyticsDAO;
    }

    public DashboardStats buildStats() throws SQLException {
        DashboardStats stats = analyticsDAO.loadDashboardStats();
        if (stats.getRoleCounts() == null) stats.setRoleCounts(new LinkedHashMap<>());
        if (stats.getStatusCounts() == null) stats.setStatusCounts(new LinkedHashMap<>());
        if (stats.getGraduatesByCareer() == null) stats.setGraduatesByCareer(new LinkedHashMap<>());
        if (stats.getContactRequestsByStatus() == null) stats.setContactRequestsByStatus(new LinkedHashMap<>());
        return stats;
    }
}
