package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.EgresadosDAO;
import com.cvmanager.models.Egresados;
import com.cvmanager.utils.DBConnection;

import java.sql.*;
import java.util.*;

public class EgresadoDAOImpl extends JdbcSupport implements EgresadosDAO {
    private static final String SELECT = "SELECT e.*, c.name AS career_name FROM egresados e LEFT JOIN careers c ON c.career_id = e.career_id";

    @Override
    public Optional<Egresados> findById(Long id) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT + " WHERE e.graduate_id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public Optional<Egresados> findByUserId(Long userId) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT + " WHERE e.user_id = ?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Egresados> findAll() throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT + " ORDER BY e.last_name, e.first_name");
             ResultSet rs = ps.executeQuery()) {
            List<Egresados> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        }
    }

    @Override
    public Long create(Egresados e) throws SQLException {
        String sql = "INSERT INTO egresados (user_id, first_name, last_name, document_type, document_number, phone, address, city, country, birth_date, photo_url, linkedin_url, portfolio_url, career_id, graduation_year, is_public, expected_salary, availability) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, e, true);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean update(Egresados e) throws SQLException {
        String sql = "UPDATE egresados SET first_name=?, last_name=?, document_type=?, document_number=?, phone=?, address=?, city=?, country=?, birth_date=?, photo_url=?, linkedin_url=?, portfolio_url=?, career_id=?, graduation_year=?, is_public=?, expected_salary=?, availability=? WHERE graduate_id=?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            bind(ps, e, false);
            ps.setLong(18, e.getGraduateId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public long countAll() throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT COUNT(*) FROM egresados");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        }
    }

    @Override
    public Map<String, Long> countByCareer() throws SQLException {
        String sql = "SELECT COALESCE(c.name, 'Sin carrera') AS career, COUNT(*) AS total FROM egresados e LEFT JOIN careers c ON c.career_id=e.career_id GROUP BY COALESCE(c.name, 'Sin carrera') ORDER BY total DESC";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            Map<String, Long> map = new LinkedHashMap<>();
            while (rs.next()) map.put(rs.getString("career"), rs.getLong("total"));
            return map;
        }
    }

    private void bind(PreparedStatement ps, Egresados e, boolean includeUser) throws SQLException {
        int i = 1;
        if (includeUser) ps.setLong(i++, e.getUserId());
        ps.setString(i++, e.getFirstName());
        ps.setString(i++, e.getLastName());
        ps.setString(i++, e.getDocumentType());
        ps.setString(i++, e.getDocumentNumber());
        ps.setString(i++, e.getPhone());
        ps.setString(i++, e.getAddress());
        ps.setString(i++, e.getCity());
        ps.setString(i++, e.getCountry());
        ps.setDate(i++, sqlDate(e.getBirthDate()));
        ps.setString(i++, e.getPhotoUrl());
        ps.setString(i++, e.getLinkedinUrl());
        ps.setString(i++, e.getPortfolioUrl());
        if (e.getCareerId() == null) ps.setNull(i++, Types.BIGINT); else ps.setLong(i++, e.getCareerId());
        if (e.getGraduationYear() == null) ps.setNull(i++, Types.INTEGER); else ps.setInt(i++, e.getGraduationYear());
        ps.setBoolean(i++, e.isPublic());
        ps.setBigDecimal(i++, e.getExpectedSalary());
        ps.setString(i, e.getAvailability());
    }

    private Egresados map(ResultSet rs) throws SQLException {
        Egresados e = new Egresados();
        e.setGraduateId(rs.getLong("graduate_id"));
        e.setUserId(rs.getLong("user_id"));
        e.setFirstName(rs.getString("first_name"));
        e.setLastName(rs.getString("last_name"));
        e.setDocumentType(rs.getString("document_type"));
        e.setDocumentNumber(rs.getString("document_number"));
        e.setPhone(rs.getString("phone"));
        e.setAddress(rs.getString("address"));
        e.setCity(rs.getString("city"));
        e.setCountry(rs.getString("country"));
        e.setBirthDate(date(rs, "birth_date"));
        e.setPhotoUrl(rs.getString("photo_url"));
        e.setLinkedinUrl(rs.getString("linkedin_url"));
        e.setPortfolioUrl(rs.getString("portfolio_url"));
        long careerId = rs.getLong("career_id");
        e.setCareerId(rs.wasNull() ? null : careerId);
        e.setCareerName(rs.getString("career_name"));
        int year = rs.getInt("graduation_year");
        e.setGraduationYear(rs.wasNull() ? null : year);
        e.setPublic(rs.getBoolean("is_public"));
        e.setExpectedSalary(rs.getBigDecimal("expected_salary"));
        e.setAvailability(rs.getString("availability"));
        return e;
    }
}
