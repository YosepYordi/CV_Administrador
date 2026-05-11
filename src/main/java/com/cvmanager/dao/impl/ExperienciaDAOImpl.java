package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.ExperienciaDAO;
import com.cvmanager.models.Experiencia;
import com.cvmanager.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExperienciaDAOImpl extends JdbcSupport implements ExperienciaDAO {
    @Override
    public List<Experiencia> findByCvId(Long cvId) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM experiencia WHERE cv_id=? ORDER BY start_date DESC")) {
            ps.setLong(1, cvId);
            List<Experiencia> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Experiencia e = new Experiencia();
                    e.setExperienciaId(rs.getLong("experiencia_id"));
                    e.setCvId(rs.getLong("cv_id"));
                    e.setEmpresaNombre(rs.getString("empresa_nombre"));
                    e.setPosicion(rs.getString("posicion"));
                    e.setStartDate(date(rs, "start_date"));
                    e.setEndDate(date(rs, "end_date"));
                    e.setCurrent(rs.getBoolean("is_current"));
                    e.setResponsibilities(rs.getString("responsibilities"));
                    e.setAchievements(rs.getString("achievements"));
                    e.setEmploymentType(rs.getString("employment_type"));
                    list.add(e);
                }
            }
            return list;
        }
    }

    @Override
    public void replaceAll(Long cvId, List<Experiencia> items) throws SQLException {
        try (Connection cn = DBConnection.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement del = cn.prepareStatement("DELETE FROM experiencia WHERE cv_id=?")) {
                del.setLong(1, cvId);
                del.executeUpdate();
            }
            try (PreparedStatement ps = cn.prepareStatement("INSERT INTO experiencia (cv_id,empresa_nombre,posicion,start_date,end_date,is_current,responsibilities,achievements,employment_type) VALUES (?,?,?,?,?,?,?,?,?)")) {
                for (Experiencia e : items) {
                    ps.setLong(1, cvId);
                    ps.setString(2, e.getEmpresaNombre());
                    ps.setString(3, e.getPosicion());
                    ps.setDate(4, sqlDate(e.getStartDate()));
                    ps.setDate(5, sqlDate(e.getEndDate()));
                    ps.setBoolean(6, e.isCurrent());
                    ps.setString(7, e.getResponsibilities());
                    ps.setString(8, e.getAchievements());
                    ps.setString(9, e.getEmploymentType());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            cn.commit();
        }
    }
}
