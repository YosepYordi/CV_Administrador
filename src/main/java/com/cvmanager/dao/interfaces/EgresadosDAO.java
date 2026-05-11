package com.cvmanager.dao.interfaces;

import com.cvmanager.models.Egresados;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EgresadosDAO {
    Optional<Egresados> findById(Long id) throws SQLException;
    Optional<Egresados> findByUserId(Long userId) throws SQLException;
    List<Egresados> findAll() throws SQLException;
    Long create(Egresados egresado) throws SQLException;
    boolean update(Egresados egresado) throws SQLException;
    long countAll() throws SQLException;
    Map<String, Long> countByCareer() throws SQLException;
}
