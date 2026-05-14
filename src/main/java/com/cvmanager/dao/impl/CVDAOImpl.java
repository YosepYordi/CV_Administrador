package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.CVDAO;
import com.cvmanager.models.CV;
import com.cvmanager.models.SearchCriteria;
import com.cvmanager.utils.DBConnection;
import com.cvmanager.utils.ValidacionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CVDAOImpl extends JdbcSupport implements CVDAO {
    private static final String BASE_SELECT =
            "SELECT cv.*, CONCAT(e.first_name, ' ', e.last_name) AS graduate_name, e.photo_url AS graduate_photo_url, c.name AS career_name, e.city, " +
            "e.is_public AS graduate_public, " +
            "COALESCE((SELECT ROUND(SUM(TIMESTAMPDIFF(MONTH, ex.start_date, COALESCE(ex.end_date, CURDATE()))) / 12, 1) FROM experiencia ex WHERE ex.cv_id = cv.cv_id), 0) AS experience_years " +
            "FROM cvs cv JOIN egresados e ON e.graduate_id = cv.graduate_id LEFT JOIN careers c ON c.career_id = e.career_id";

    @Override
    public Optional<CV> findById(Long id) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(BASE_SELECT + " WHERE cv.cv_id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public Optional<CV> findByGraduateId(Long graduateId) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(BASE_SELECT + " WHERE cv.graduate_id = ?")) {
            ps.setLong(1, graduateId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<CV> findPublished(int limit) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(BASE_SELECT + " WHERE cv.is_published = TRUE AND e.is_public = TRUE ORDER BY cv.updated_at DESC LIMIT ?")) {
            ps.setInt(1, Math.max(1, limit));
            return list(ps);
        }
    }

    @Override
    public List<CV> search(SearchCriteria c) throws SQLException {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE cv.is_published = TRUE AND e.is_public = TRUE");
        List<Object> params = new ArrayList<>();
        if (!ValidacionUtil.isBlank(c.getCareer())) { sql.append(" AND LOWER(c.name) LIKE ?"); params.add("%" + c.getCareer().toLowerCase() + "%"); }
        if (!ValidacionUtil.isBlank(c.getSkill())) { sql.append(" AND EXISTS (SELECT 1 FROM habilidades h WHERE h.cv_id = cv.cv_id AND LOWER(h.habilidad_name) LIKE ?)"); params.add("%" + c.getSkill().toLowerCase() + "%"); }
        if (!ValidacionUtil.isBlank(c.getCity())) { sql.append(" AND LOWER(e.city) LIKE ?"); params.add("%" + c.getCity().toLowerCase() + "%"); }
        if (!ValidacionUtil.isBlank(c.getLanguage())) { sql.append(" AND EXISTS (SELECT 1 FROM idiomas i WHERE i.cv_id = cv.cv_id AND LOWER(i.language_name) LIKE ?)"); params.add("%" + c.getLanguage().toLowerCase() + "%"); }
        if (!ValidacionUtil.isBlank(c.getKeyword())) {
            sql.append(" AND (LOWER(cv.title) LIKE ? OR LOWER(cv.professional_summary) LIKE ? OR LOWER(e.first_name) LIKE ? OR LOWER(e.last_name) LIKE ?)");
            String kw = "%" + c.getKeyword().toLowerCase() + "%";
            params.add(kw); params.add(kw); params.add(kw); params.add(kw);
        }
        if (c.getMinExperience() != null) {
            sql.append(" AND COALESCE((SELECT SUM(TIMESTAMPDIFF(MONTH, ex2.start_date, COALESCE(ex2.end_date, CURDATE()))) / 12 FROM experiencia ex2 WHERE ex2.cv_id = cv.cv_id), 0) >= ?");
            params.add(c.getMinExperience());
        }
        sql.append(" ORDER BY cv.updated_at DESC LIMIT ? OFFSET ?");
        params.add(c.getPageSize());
        params.add((c.getPage() - 1) * c.getPageSize());
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            return list(ps);
        }
    }

    @Override
    public Long create(CV cv) throws SQLException {
        String sql = "INSERT INTO cvs (graduate_id, title, professional_summary, cv_pdf_url, is_published, views_count, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, cv.getGraduateId());
            ps.setString(2, cv.getTitle());
            ps.setString(3, cv.getProfessionalSummary());
            ps.setString(4, cv.getCvPdfUrl());
            ps.setBoolean(5, cv.isPublished());
            ps.setInt(6, cv.getViewsCount());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean update(CV cv) throws SQLException {
        String sql = "UPDATE cvs SET title=?, professional_summary=?, cv_pdf_url=?, is_published=?, views_count=?, updated_at=NOW() WHERE cv_id=?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cv.getTitle());
            ps.setString(2, cv.getProfessionalSummary());
            ps.setString(3, cv.getCvPdfUrl());
            ps.setBoolean(4, cv.isPublished());
            ps.setInt(5, cv.getViewsCount());
            ps.setLong(6, cv.getCvId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean incrementViews(Long cvId) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE cvs SET views_count = views_count + 1 WHERE cv_id = ?")) {
            ps.setLong(1, cvId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public long countPublished() throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT COUNT(*) FROM cvs WHERE is_published = TRUE");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        }
    }

    private List<CV> list(PreparedStatement ps) throws SQLException {
        List<CV> cvs = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cvs.add(map(rs));
        }
        return cvs;
    }

    private CV map(ResultSet rs) throws SQLException {
        CV cv = new CV();
        cv.setCvId(rs.getLong("cv_id"));
        cv.setGraduateId(rs.getLong("graduate_id"));
        cv.setTitle(rs.getString("title"));
        cv.setProfessionalSummary(rs.getString("professional_summary"));
        cv.setCvPdfUrl(rs.getString("cv_pdf_url"));
        cv.setPublished(rs.getBoolean("is_published"));
        cv.setViewsCount(rs.getInt("views_count"));
        cv.setCreatedAt(dateTime(rs, "created_at"));
        cv.setUpdatedAt(dateTime(rs, "updated_at"));
        cv.setGraduateName(rs.getString("graduate_name"));
        cv.setGraduatePhotoUrl(rs.getString("graduate_photo_url"));
        cv.setCareerName(rs.getString("career_name"));
        cv.setCity(rs.getString("city"));
        cv.setGraduatePublic(rs.getBoolean("graduate_public"));
        cv.setYearsOfExperience(rs.getDouble("experience_years"));
        return cv;
    }
}
