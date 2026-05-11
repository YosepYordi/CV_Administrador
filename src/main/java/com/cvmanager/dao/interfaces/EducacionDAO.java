package com.cvmanager.dao.interfaces;

import com.cvmanager.models.Educacion;

import java.sql.SQLException;
import java.util.List;

public interface EducacionDAO {
    List<Educacion> findByCvId(Long cvId) throws SQLException;
    void replaceAll(Long cvId, List<Educacion> items) throws SQLException;
}
