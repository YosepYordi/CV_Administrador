package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.CompanyDAO;
import com.cvmanager.models.Company;
import com.cvmanager.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompanyDAOImpl extends JdbcSupport implements CompanyDAO {
    @Override
    public Optional<Company> findByUserId(Long userId) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM companies WHERE user_id = ?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public Optional<Company> findById(Long companyId) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM companies WHERE company_id = ?")) {
            ps.setLong(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public Long create(Company company) throws SQLException {
        String sql = "INSERT INTO companies (user_id, company_name, ruc, industry, company_size, phone, website, description, logo_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, company.getUserId());
            ps.setString(2, company.getCompanyName());
            ps.setString(3, company.getRuc());
            ps.setString(4, company.getIndustry());
            ps.setString(5, company.getCompanySize());
            ps.setString(6, company.getPhone());
            ps.setString(7, company.getWebsite());
            ps.setString(8, company.getDescription());
            ps.setString(9, company.getLogoUrl());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean addFavorite(Long companyId, Long cvId) throws SQLException {
        if (isFavorite(companyId, cvId)) return true;
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO company_favorites (company_id, cv_id) VALUES (?, ?)")) {
            ps.setLong(1, companyId);
            ps.setLong(2, cvId);
            ps.executeUpdate();
            return true;
        }
    }

    @Override
    public boolean removeFavorite(Long companyId, Long cvId) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM company_favorites WHERE company_id = ? AND cv_id = ?")) {
            ps.setLong(1, companyId);
            ps.setLong(2, cvId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean isFavorite(Long companyId, Long cvId) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT 1 FROM company_favorites WHERE company_id = ? AND cv_id = ?")) {
            ps.setLong(1, companyId);
            ps.setLong(2, cvId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public List<Long> findFavoriteCvIds(Long companyId) throws SQLException {
        List<Long> ids = new ArrayList<>();
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT cv_id FROM company_favorites WHERE company_id = ? ORDER BY created_at DESC")) {
            ps.setLong(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getLong("cv_id"));
            }
        }
        return ids;
    }

    private Company map(ResultSet rs) throws SQLException {
        Company company = new Company();
        company.setCompanyId(rs.getLong("company_id"));
        company.setUserId(rs.getLong("user_id"));
        company.setCompanyName(rs.getString("company_name"));
        company.setRuc(rs.getString("ruc"));
        company.setIndustry(rs.getString("industry"));
        company.setCompanySize(rs.getString("company_size"));
        company.setPhone(rs.getString("phone"));
        company.setWebsite(rs.getString("website"));
        company.setDescription(rs.getString("description"));
        company.setLogoUrl(rs.getString("logo_url"));
        return company;
    }
}
