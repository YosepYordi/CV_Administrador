package com.cvmanager.services;

import com.cvmanager.dao.impl.AccountDeletionDAOImpl;
import com.cvmanager.dao.interfaces.AccountDeletionDAO;
import com.cvmanager.models.DeletionSummary;
import com.cvmanager.models.User;
import com.cvmanager.utils.ValidacionUtil;

import java.sql.SQLException;

public class AccountDeletionService {
    private static final String DELETE_CV_CONFIRMATION = "ELIMINAR CV";
    private static final String DELETE_USER_CONFIRMATION = "ELIMINAR USUARIO";
    private final AccountDeletionDAO deletionDAO;

    public AccountDeletionService() {
        this(new AccountDeletionDAOImpl());
    }

    public AccountDeletionService(AccountDeletionDAO deletionDAO) {
        this.deletionDAO = deletionDAO;
    }

    public DeletionSummary deleteOwnAccount(User user, String confirmation) throws SQLException {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("Sesion expirada.");
        }
        if (!matchesEmail(user.getEmail(), confirmation)) {
            throw new IllegalArgumentException("Escribe tu correo actual para confirmar la eliminacion total de la cuenta.");
        }
        return deletionDAO.deleteUserCompletely(user.getUserId());
    }

    public DeletionSummary deleteUserAsAdmin(User actor, Long targetUserId, String confirmation) throws SQLException {
        if (actor == null || !actor.isAdmin()) {
            throw new IllegalArgumentException("Solo administracion puede eliminar usuarios.");
        }
        if (actor.getUserId() != null && actor.getUserId().equals(targetUserId)) {
            throw new IllegalArgumentException("No puedes eliminar tu propia cuenta desde gestion de usuarios.");
        }
        if (!DELETE_USER_CONFIRMATION.equals(confirmation)) {
            throw new IllegalArgumentException("Escribe ELIMINAR USUARIO para confirmar la eliminacion total.");
        }
        return deletionDAO.deleteUserCompletely(targetUserId);
    }

    public DeletionSummary deleteCvForGraduate(Long graduateId, String confirmation) throws SQLException {
        if (graduateId == null) {
            throw new IllegalArgumentException("Perfil de egresado no valido.");
        }
        if (!DELETE_CV_CONFIRMATION.equals(confirmation)) {
            throw new IllegalArgumentException("Escribe ELIMINAR CV para confirmar la eliminacion total del CV.");
        }
        return deletionDAO.deleteCvByGraduateId(graduateId);
    }

    private boolean matchesEmail(String email, String confirmation) {
        return !ValidacionUtil.isBlank(email)
                && !ValidacionUtil.isBlank(confirmation)
                && email.trim().equalsIgnoreCase(confirmation.trim());
    }
}
