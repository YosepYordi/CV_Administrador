package com.cvmanager.dao.interfaces;

import com.cvmanager.models.Experiencia;

import java.sql.SQLException;
import java.util.List;

public interface ExperienciaDAO {
    List<Experiencia> findByCvId(Long cvId) throws SQLException;
    void replaceAll(Long cvId, List<Experiencia> items) throws SQLException;
}
