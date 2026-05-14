package com.cvmanager.dao.interfaces;

import com.cvmanager.models.ContactRequest;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ContactRequestDAO {
    Optional<ContactRequest> findById(Long requestId) throws SQLException;
    List<ContactRequest> findByGraduateId(Long graduateId) throws SQLException;
    List<ContactRequest> findByCompanyId(Long companyId) throws SQLException;
    Long createForCv(Long companyId, Long cvId, String message) throws SQLException;
    Long createForCompany(Long graduateId, Long companyId, String message) throws SQLException;
    boolean updateStatusForGraduate(Long requestId, Long graduateId, ContactRequest.Status status) throws SQLException;
    boolean updateStatusForCompany(Long requestId, Long companyId, ContactRequest.Status status) throws SQLException;
}
