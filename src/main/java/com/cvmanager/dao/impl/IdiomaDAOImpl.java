package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.IdiomaDAO;
import com.cvmanager.models.Idioma;
import com.cvmanager.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IdiomaDAOImpl implements IdiomaDAO {
    @Override
    public List<Idioma> findByCvId(Long cvId) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM idiomas WHERE cv_id=? ORDER BY language_name")) {
            ps.setLong(1, cvId);
            List<Idioma> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Idioma i = new Idioma();
                    i.setLanguageId(rs.getLong("language_id"));
                    i.setCvId(rs.getLong("cv_id"));
                    i.setLanguageName(rs.getString("language_name"));
                    i.setProficiencyLevel(Idioma.Proficiency.from(rs.getString("proficiency_level")));
                    i.setCertifications(rs.getString("certifications"));
                    list.add(i);
                }
            }
            return list;
        }
    }

    @Override
    public void replaceAll(Long cvId, List<Idioma> items) throws SQLException {
        try (Connection cn = DBConnection.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement del = cn.prepareStatement("DELETE FROM idiomas WHERE cv_id=?")) {
                del.setLong(1, cvId);
                del.executeUpdate();
            }
            try (PreparedStatement ps = cn.prepareStatement("INSERT INTO idiomas (cv_id,language_name,proficiency_level,certifications) VALUES (?,?,?,?)")) {
                for (Idioma i : items) {
                    ps.setLong(1, cvId);
                    ps.setString(2, i.getLanguageName());
                    ps.setString(3, i.getProficiencyLevel().getValue());
                    ps.setString(4, i.getCertifications());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            cn.commit();
        }
    }
}
