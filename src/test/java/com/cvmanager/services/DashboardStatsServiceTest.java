package com.cvmanager.services;

import com.cvmanager.dao.interfaces.AnalyticsDAO;
import com.cvmanager.models.DashboardStats;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DashboardStatsServiceTest {
    @Test
    void loadsCompleteOperationalMetricsFromDao() throws SQLException {
        DashboardStats expected = new DashboardStats();
        expected.setTotalUsers(12);
        expected.setTotalGraduates(7);
        expected.setTotalCompanies(4);
        expected.setTotalCvs(9);
        expected.setTotalPublishedCvs(6);
        expected.setDraftCvs(3);
        expected.setActiveCareers(3);
        expected.setTotalViews(42);
        expected.setTotalFavorites(5);
        expected.setTotalContactRequests(4);
        expected.setPendingContactRequests(2);
        expected.setAcceptedContactRequests(1);
        expected.setRejectedContactRequests(1);
        expected.setTotalSearches(8);
        expected.setRoleCounts(map("graduate", 7, "company", 4, "admin", 1));
        expected.setStatusCounts(map("active", 10, "pending", 1, "inactive", 1));
        expected.setContactRequestsByStatus(map("pending", 2, "accepted", 1, "rejected", 1));

        DashboardStats stats = new DashboardStatsService(() -> expected).buildStats();

        assertEquals(12, stats.getTotalUsers());
        assertEquals(9, stats.getTotalCvs());
        assertEquals(3, stats.getDraftCvs());
        assertEquals(5, stats.getTotalFavorites());
        assertEquals(4, stats.getTotalContactRequests());
        assertEquals(8, stats.getTotalSearches());
        assertEquals(3, stats.getRoleCounts().size());
        assertEquals(3, stats.getContactRequestsByStatus().size());
    }

    @Test
    void replacesNullMapsWithEmptyMaps() throws SQLException {
        DashboardStats stats = new DashboardStatsService(new AnalyticsDAO() {
            @Override
            public DashboardStats loadDashboardStats() {
                return new DashboardStats();
            }
        }).buildStats();

        assertTrue(stats.getRoleCounts().isEmpty());
        assertTrue(stats.getStatusCounts().isEmpty());
        assertTrue(stats.getGraduatesByCareer().isEmpty());
        assertTrue(stats.getContactRequestsByStatus().isEmpty());
    }

    private static Map<String, Long> map(String firstKey, long firstValue, String secondKey, long secondValue, String thirdKey, long thirdValue) {
        Map<String, Long> map = new LinkedHashMap<>();
        map.put(firstKey, firstValue);
        map.put(secondKey, secondValue);
        map.put(thirdKey, thirdValue);
        return map;
    }
}
