package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.CertificacionDAO;
import com.cvmanager.models.Certificacion;
import com.cvmanager.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CertificacionDAOImpl extends JdbcSupport implements CertificacionDAO {
    @Override
    public List<Certificacion> findByCvId(Long cvId) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM certificaciones WHERE cv_id=? ORDER BY issue_date DESC")) {
            ps.setLong(1, cvId);
            List<Certificacion> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Certificacion c = new Certificacion();
                    c.setCertificationId(rs.getLong("certification_id"));
                    c.setCvId(rs.getLong("cv_id"));
                    c.setName(rs.getString("name"));
                    c.setIssuingOrganization(rs.getString("issuing_organization"));
                    c.setIssueDate(date(rs, "issue_date"));
                    c.setExpirationDate(date(rs, "expiration_date"));
                    c.setCredentialId(rs.getString("credential_id"));
                    c.setCredentialUrl(rs.getString("credential_url"));
                    list.add(c);
                }
            }
            return list;
        }
    }

    @Override
    public void replaceAll(Long cvId, List<Certificacion> items) throws SQLException {
        try (Connection cn = DBConnection.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement del = cn.prepareStatement("DELETE FROM certificaciones WHERE cv_id=?")) {
                del.setLong(1, cvId);
                del.executeUpdate();
            }
            try (PreparedStatement ps = cn.prepareStatement("INSERT INTO certificaciones (cv_id,name,issuing_organization,issue_date,expiration_date,credential_id,credential_url) VALUES (?,?,?,?,?,?,?)")) {
                for (Certificacion c : items) {
                    ps.setLong(1, cvId);
                    ps.setString(2, c.getName());
                    ps.setString(3, c.getIssuingOrganization());
                    ps.setDate(4, sqlDate(c.getIssueDate()));
                    ps.setDate(5, sqlDate(c.getExpirationDate()));
                    ps.setString(6, c.getCredentialId());
                    ps.setString(7, c.getCredentialUrl());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            cn.commit();
        }
    }
}
