package com.cvmanager.dao.interfaces;

import com.cvmanager.models.DashboardStats;

import java.sql.SQLException;

public interface AnalyticsDAO {
    DashboardStats loadDashboardStats() throws SQLException;
}
