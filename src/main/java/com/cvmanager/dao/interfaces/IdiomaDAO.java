package com.cvmanager.dao.interfaces;

import com.cvmanager.models.Idioma;

import java.sql.SQLException;
import java.util.List;

public interface IdiomaDAO {
    List<Idioma> findByCvId(Long cvId) throws SQLException;
    void replaceAll(Long cvId, List<Idioma> items) throws SQLException;
}
