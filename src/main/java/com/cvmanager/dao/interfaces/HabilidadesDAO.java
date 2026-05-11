package com.cvmanager.dao.interfaces;

import com.cvmanager.models.Habilidades;

import java.sql.SQLException;
import java.util.List;

public interface HabilidadesDAO {
    List<Habilidades> findByCvId(Long cvId) throws SQLException;
    void replaceAll(Long cvId, List<Habilidades> items) throws SQLException;
}
