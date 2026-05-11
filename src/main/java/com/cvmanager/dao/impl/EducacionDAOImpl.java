package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.EducacionDAO;
import com.cvmanager.models.Educacion;
import com.cvmanager.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EducacionDAOImpl extends JdbcSupport implements EducacionDAO {
    @Override
    public List<Educacion> findByCvId(Long cvId) throws SQLException {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM educacion WHERE cv_id=? ORDER BY start_date DESC")) {
            ps.setLong(1, cvId);
            List<Educacion> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Educacion e = new Educacion();
                    e.setEducationId(rs.getLong("education_id"));
                    e.setCvId(rs.getLong("cv_id"));
                    e.setInstitution(rs.getString("institution"));
                    e.setDegree(rs.getString("degree"));
                    e.setFieldOfStudy(rs.getString("field_of_study"));
                    e.setStartDate(date(rs, "start_date"));
                    e.setEndDate(date(rs, "end_date"));
                    e.setCurrent(rs.getBoolean("is_current"));
                    e.setDescription(rs.getString("description"));
                    Number gpa = (Number) rs.getObject("gpa");
                    e.setGpa(gpa == null ? null : gpa.doubleValue());
                    list.add(e);
                }
            }
            return list;
        }
    }

    @Override
    public void replaceAll(Long cvId, List<Educacion> items) throws SQLException {
        try (Connection cn = DBConnection.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement del = cn.prepareStatement("DELETE FROM educacion WHERE cv_id=?")) {
                del.setLong(1, cvId);
                del.executeUpdate();
            }
            try (PreparedStatement ps = cn.prepareStatement("INSERT INTO educacion (cv_id,institution,degree,field_of_study,start_date,end_date,is_current,description,gpa) VALUES (?,?,?,?,?,?,?,?,?)")) {
                for (Educacion e : items) {
                    ps.setLong(1, cvId);
                    ps.setString(2, e.getInstitution());
                    ps.setString(3, e.getDegree());
                    ps.setString(4, e.getFieldOfStudy());
                    ps.setDate(5, sqlDate(e.getStartDate()));
                    ps.setDate(6, sqlDate(e.getEndDate()));
                    ps.setBoolean(7, e.isCurrent());
                    ps.setString(8, e.getDescription());
                    if (e.getGpa() == null) ps.setNull(9, Types.DOUBLE); else ps.setDouble(9, e.getGpa());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            cn.commit();
        }
    }
}
