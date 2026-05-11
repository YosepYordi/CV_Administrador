package com.cvmanager.dao.interfaces;

import com.cvmanager.models.CV;
import com.cvmanager.models.SearchCriteria;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CVDAO {
    Optional<CV> findById(Long id) throws SQLException;
    Optional<CV> findByGraduateId(Long graduateId) throws SQLException;
    List<CV> findPublished(int limit) throws SQLException;
    List<CV> search(SearchCriteria criteria) throws SQLException;
    Long create(CV cv) throws SQLException;
    boolean update(CV cv) throws SQLException;
    boolean incrementViews(Long cvId) throws SQLException;
    long countPublished() throws SQLException;
}
