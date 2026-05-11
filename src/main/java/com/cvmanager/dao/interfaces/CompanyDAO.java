package com.cvmanager.dao.interfaces;

import com.cvmanager.models.Company;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CompanyDAO {
    Optional<Company> findByUserId(Long userId) throws SQLException;
    Optional<Company> findById(Long companyId) throws SQLException;
    Long create(Company company) throws SQLException;
    boolean addFavorite(Long companyId, Long cvId) throws SQLException;
    boolean removeFavorite(Long companyId, Long cvId) throws SQLException;
    boolean isFavorite(Long companyId, Long cvId) throws SQLException;
    List<Long> findFavoriteCvIds(Long companyId) throws SQLException;
}
