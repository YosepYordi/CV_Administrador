package com.cvmanager.dao.interfaces;

import com.cvmanager.models.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UsuarioDAO {
    Optional<User> findById(Long id) throws SQLException;
    Optional<User> findByEmail(String email) throws SQLException;
    List<User> findAll() throws SQLException;
    List<User> findByRoleAndStatus(String role, String status) throws SQLException;
    Long create(User user) throws SQLException;
    boolean update(User user) throws SQLException;
    boolean updateStatus(Long userId, User.Status status) throws SQLException;
    boolean updateLastLogin(Long userId, LocalDateTime loginAt) throws SQLException;
    long countByRole(User.Role role) throws SQLException;
}
