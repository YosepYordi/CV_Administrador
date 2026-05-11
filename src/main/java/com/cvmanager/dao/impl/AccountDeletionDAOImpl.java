package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.AccountDeletionDAO;
import com.cvmanager.models.DeletionSummary;
import com.cvmanager.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDeletionDAOImpl implements AccountDeletionDAO {
    @Override
    public DeletionSummary deleteUserCompletely(Long userId) throws SQLException {
        DeletionSummary summary = new DeletionSummary();
        if (userId == null) return summary;
        try (Connection cn = DBConnection.getConnection()) {
            boolean previousAutoCommit = cn.getAutoCommit();
            cn.setAutoCommit(false);
            try {
                List<Long> graduateIds = ids(cn, "SELECT graduate_id FROM egresados WHERE user_id = ?", userId);
                List<Long> cvIds = graduateIds.isEmpty() ? new ArrayList<>() :
                        idsIn(cn, "SELECT cv_id FROM cvs WHERE graduate_id", graduateIds);
                List<Long> companyIds = ids(cn, "SELECT company_id FROM companies WHERE user_id = ?", userId);

                deleteCvDependents(cn, summary, graduateIds, cvIds);
                deleteCompanyDependents(cn, summary, companyIds);

                summary.setDeletedPasswordResetTokens(count(cn, "SELECT COUNT(*) FROM password_reset_tokens WHERE user_id = ?", userId));
                update(cn, "DELETE FROM password_reset_tokens WHERE user_id = ?", userId);

                summary.setAnonymizedAuditLogs(count(cn, "SELECT COUNT(*) FROM audit_logs WHERE user_id = ?", userId));
                update(cn, "UPDATE audit_logs SET user_id = NULL WHERE user_id = ?", userId);

                summary.setDeletedCompanies(update(cn, "DELETE FROM companies WHERE user_id = ?", userId));
                summary.setDeletedGraduates(update(cn, "DELETE FROM egresados WHERE user_id = ?", userId));
                summary.setDeletedUsers(update(cn, "DELETE FROM usuarios WHERE user_id = ?", userId));

                cn.commit();
            } catch (SQLException | RuntimeException ex) {
                cn.rollback();
                throw ex;
            } finally {
                cn.setAutoCommit(previousAutoCommit);
            }
        }
        return summary;
    }

    @Override
    public DeletionSummary deleteCvByGraduateId(Long graduateId) throws SQLException {
        DeletionSummary summary = new DeletionSummary();
        if (graduateId == null) return summary;
        try (Connection cn = DBConnection.getConnection()) {
            boolean previousAutoCommit = cn.getAutoCommit();
            cn.setAutoCommit(false);
            try {
                List<Long> graduateIds = new ArrayList<>();
                graduateIds.add(graduateId);
                List<Long> cvIds = ids(cn, "SELECT cv_id FROM cvs WHERE graduate_id = ?", graduateId);
                deleteCvDependents(cn, summary, graduateIds, cvIds);
                cn.commit();
            } catch (SQLException | RuntimeException ex) {
                cn.rollback();
                throw ex;
            } finally {
                cn.setAutoCommit(previousAutoCommit);
            }
        }
        return summary;
    }

    private void deleteCvDependents(Connection cn, DeletionSummary summary, List<Long> graduateIds, List<Long> cvIds) throws SQLException {
        if (!cvIds.isEmpty()) {
            summary.setDeletedCvSections(countCvSections(cn, cvIds));
            summary.setDeletedFavorites(summary.getDeletedFavorites()
                    + countIn(cn, "SELECT COUNT(*) FROM company_favorites WHERE cv_id", cvIds));
            updateIn(cn, "DELETE FROM company_favorites WHERE cv_id", cvIds);
        }
        if (!graduateIds.isEmpty()) {
            summary.setDeletedContactRequests(summary.getDeletedContactRequests()
                    + countIn(cn, "SELECT COUNT(*) FROM contact_requests WHERE graduate_id", graduateIds));
            updateIn(cn, "DELETE FROM contact_requests WHERE graduate_id", graduateIds);
            summary.setDeletedCvs(summary.getDeletedCvs()
                    + updateIn(cn, "DELETE FROM cvs WHERE graduate_id", graduateIds));
        }
    }

    private void deleteCompanyDependents(Connection cn, DeletionSummary summary, List<Long> companyIds) throws SQLException {
        if (companyIds.isEmpty()) return;
        summary.setDeletedFavorites(summary.getDeletedFavorites()
                + countIn(cn, "SELECT COUNT(*) FROM company_favorites WHERE company_id", companyIds));
        summary.setDeletedContactRequests(summary.getDeletedContactRequests()
                + countIn(cn, "SELECT COUNT(*) FROM contact_requests WHERE company_id", companyIds));
        updateIn(cn, "DELETE FROM company_favorites WHERE company_id", companyIds);
        updateIn(cn, "DELETE FROM contact_requests WHERE company_id", companyIds);
    }

    private long countCvSections(Connection cn, List<Long> cvIds) throws SQLException {
        String[] tables = {"educacion", "experiencia", "habilidades", "idiomas", "certificaciones"};
        long total = 0;
        for (String table : tables) {
            total += countIn(cn, "SELECT COUNT(*) FROM " + table + " WHERE cv_id", cvIds);
        }
        return total;
    }

    private List<Long> ids(Connection cn, String sql, Long value) throws SQLException {
        List<Long> ids = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getLong(1));
            }
        }
        return ids;
    }

    private List<Long> idsIn(Connection cn, String sqlPrefix, List<Long> values) throws SQLException {
        List<Long> ids = new ArrayList<>();
        if (values.isEmpty()) return ids;
        try (PreparedStatement ps = cn.prepareStatement(sqlPrefix + inClause(values.size()))) {
            bind(ps, values);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getLong(1));
            }
        }
        return ids;
    }

    private long count(Connection cn, String sql, Long value) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        }
    }

    private long countIn(Connection cn, String sqlPrefix, List<Long> values) throws SQLException {
        if (values.isEmpty()) return 0;
        try (PreparedStatement ps = cn.prepareStatement(sqlPrefix + inClause(values.size()))) {
            bind(ps, values);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        }
    }

    private long update(Connection cn, String sql, Long value) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, value);
            return ps.executeUpdate();
        }
    }

    private long updateIn(Connection cn, String sqlPrefix, List<Long> values) throws SQLException {
        if (values.isEmpty()) return 0;
        try (PreparedStatement ps = cn.prepareStatement(sqlPrefix + inClause(values.size()))) {
            bind(ps, values);
            return ps.executeUpdate();
        }
    }

    private String inClause(int count) {
        StringBuilder clause = new StringBuilder(" IN (");
        for (int i = 0; i < count; i++) {
            if (i > 0) clause.append(',');
            clause.append('?');
        }
        return clause.append(')').toString();
    }

    private void bind(PreparedStatement ps, List<Long> values) throws SQLException {
        for (int i = 0; i < values.size(); i++) {
            ps.setLong(i + 1, values.get(i));
        }
    }
}
