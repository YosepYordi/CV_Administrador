package com.cvmanager.dao.interfaces;

import com.cvmanager.models.AuditLog;

import java.sql.SQLException;
import java.util.List;

public interface AuditLogDAO {
    Long create(AuditLog auditLog) throws SQLException;
    List<AuditLog> findRecent(int limit) throws SQLException;
}
