package com.cvmanager.dao.interfaces;

import com.cvmanager.models.ContactRequest;

import java.sql.SQLException;
import java.util.Optional;

public interface ContactRequestDAO {
    Optional<ContactRequest> findById(Long requestId) throws SQLException;
    Long createForCv(Long companyId, Long cvId, String message) throws SQLException;
}
