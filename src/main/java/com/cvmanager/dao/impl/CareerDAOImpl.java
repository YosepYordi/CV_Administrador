package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.CareerDAO;
import com.cvmanager.models.Career;
import com.cvmanager.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CareerDAOImpl implements CareerDAO {
    @Override
    public Optional<Career> findById(Long id) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM careers WHERE career_id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Career> findAll() throws SQLException {
        return query("SELECT * FROM careers ORDER BY name");
    }

    @Override
    public List<Career> findActive() throws SQLException {
        return query("SELECT * FROM careers WHERE is_active = TRUE ORDER BY name");
    }

    @Override
    public Long create(Career career) throws SQLException {
        String sql = "INSERT INTO careers (name, code, description, duration_years, is_active) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, career.getName());
            ps.setString(2, career.getCode());
            ps.setString(3, career.getDescription());
            if (career.getDurationYears() == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, career.getDurationYears());
            ps.setBoolean(5, career.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean update(Career career) throws SQLException {
        String sql = "UPDATE careers SET name = ?, code = ?, description = ?, duration_years = ?, is_active = ? WHERE career_id = ?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, career.getName());
            ps.setString(2, career.getCode());
            ps.setString(3, career.getDescription());
            if (career.getDurationYears() == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, career.getDurationYears());
            ps.setBoolean(5, career.isActive());
            ps.setLong(6, career.getCareerId());
            return ps.executeUpdate() > 0;
        }
    }

    private List<Career> query(String sql) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Career> careers = new ArrayList<>();
            while (rs.next()) careers.add(map(rs));
            return careers;
        }
    }

    private Career map(ResultSet rs) throws SQLException {
        Career career = new Career();
        career.setCareerId(rs.getLong("career_id"));
        career.setName(rs.getString("name"));
        career.setCode(rs.getString("code"));
        career.setDescription(rs.getString("description"));
        career.setDurationYears((Integer) rs.getObject("duration_years"));
        career.setActive(rs.getBoolean("is_active"));
        return career;
    }
}
