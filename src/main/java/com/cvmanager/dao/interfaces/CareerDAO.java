package com.cvmanager.dao.interfaces;

import com.cvmanager.models.Career;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CareerDAO {
    Optional<Career> findById(Long id) throws SQLException;
    List<Career> findAll() throws SQLException;
    List<Career> findActive() throws SQLException;
    Long create(Career career) throws SQLException;
    boolean update(Career career) throws SQLException;
}
