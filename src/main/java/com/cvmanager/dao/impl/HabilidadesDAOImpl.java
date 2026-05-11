package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.HabilidadesDAO;
import com.cvmanager.models.Habilidades;
import com.cvmanager.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabilidadesDAOImpl implements HabilidadesDAO {
    @Override
    public List<Habilidades> findByCvId(Long cvId) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM habilidades WHERE cv_id=? ORDER BY habilidad_name")) {
            ps.setLong(1, cvId);
            List<Habilidades> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Habilidades h = new Habilidades();
                    h.setHabilidadId(rs.getLong("habilidad_id"));
                    h.setCvId(rs.getLong("cv_id"));
                    h.setHabilidadName(rs.getString("habilidad_name"));
                    h.setHabilidadCategory(Habilidades.Category.from(rs.getString("habilidad_category")));
                    h.setPreferenciaLevel(rs.getInt("preferencia_level"));
                    list.add(h);
                }
            }
            return list;
        }
    }

    @Override
    public void replaceAll(Long cvId, List<Habilidades> items) throws SQLException {
        try (Connection cn = DBConnection.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement del = cn.prepareStatement("DELETE FROM habilidades WHERE cv_id=?")) {
                del.setLong(1, cvId);
                del.executeUpdate();
            }
            try (PreparedStatement ps = cn.prepareStatement("INSERT INTO habilidades (cv_id,habilidad_name,habilidad_category,preferencia_level) VALUES (?,?,?,?)")) {
                for (Habilidades h : items) {
                    ps.setLong(1, cvId);
                    ps.setString(2, h.getHabilidadName());
                    ps.setString(3, h.getHabilidadCategory().getValue());
                    ps.setInt(4, h.getPreferenciaLevel());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            cn.commit();
        }
    }
}
