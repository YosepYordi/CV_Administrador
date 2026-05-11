package com.cvmanager.services;

import com.cvmanager.dao.impl.AuditLogDAOImpl;
import com.cvmanager.dao.interfaces.AuditLogDAO;
import com.cvmanager.models.AuditLog;
import com.cvmanager.models.User;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class AuditLogService {
    private final AuditLogDAO auditLogDAO;

    public AuditLogService() {
        this(new AuditLogDAOImpl());
    }

    public AuditLogService(AuditLogDAO auditLogDAO) {
        this.auditLogDAO = auditLogDAO;
    }

    public void record(User actor, String action, String entityType, Long entityId, String details, HttpServletRequest request) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setUserId(actor == null ? null : actor.getUserId());
            auditLog.setAction(action);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setDetails(details);
            auditLog.setIpAddress(resolveIp(request));
            auditLogDAO.create(auditLog);
        } catch (SQLException ex) {
            // Audit failures must not block administrative actions.
        }
    }

    public List<AuditLog> recent(int limit) {
        try {
            return auditLogDAO.findRecent(limit);
        } catch (SQLException ex) {
            return Collections.emptyList();
        }
    }

    private String resolveIp(HttpServletRequest request) {
        if (request == null) return null;
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
