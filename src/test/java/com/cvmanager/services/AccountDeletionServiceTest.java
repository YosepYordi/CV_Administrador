package com.cvmanager.services;

import com.cvmanager.dao.interfaces.AccountDeletionDAO;
import com.cvmanager.models.DeletionSummary;
import com.cvmanager.models.User;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AccountDeletionServiceTest {
    @Test
    void deleteOwnAccountRequiresEmailConfirmation() {
        RecordingDeletionDAO dao = new RecordingDeletionDAO();
        AccountDeletionService service = new AccountDeletionService(dao);
        User user = user(7L, "egresado@instituto.edu.pe", User.Role.GRADUATE);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.deleteOwnAccount(user, "DELETE"));

        assertTrue(ex.getMessage().contains("correo"));
        assertEquals(0, dao.deletedUsers);
    }

    @Test
    void deleteOwnAccountDeletesMatchingUser() throws SQLException {
        RecordingDeletionDAO dao = new RecordingDeletionDAO();
        AccountDeletionService service = new AccountDeletionService(dao);
        User user = user(7L, "egresado@instituto.edu.pe", User.Role.GRADUATE);

        DeletionSummary summary = service.deleteOwnAccount(user, "egresado@instituto.edu.pe");

        assertEquals(7L, dao.lastDeletedUserId);
        assertEquals(1, summary.getDeletedUsers());
    }

    @Test
    void adminCannotDeleteOwnAccountFromUserManagement() {
        RecordingDeletionDAO dao = new RecordingDeletionDAO();
        AccountDeletionService service = new AccountDeletionService(dao);
        User admin = user(1L, "admin@instituto.edu.pe", User.Role.ADMIN);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.deleteUserAsAdmin(admin, 1L, "DELETE USER"));

        assertTrue(ex.getMessage().contains("propia cuenta"));
        assertEquals(0, dao.deletedUsers);
    }

    @Test
    void deleteCvRequiresDeleteCvPhrase() {
        RecordingDeletionDAO dao = new RecordingDeletionDAO();
        AccountDeletionService service = new AccountDeletionService(dao);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.deleteCvForGraduate(12L, "DELETE"));

        assertTrue(ex.getMessage().contains("DELETE CV"));
        assertEquals(0, dao.deletedCvs);
    }

    @Test
    void deleteCvForGraduateCallsDao() throws SQLException {
        RecordingDeletionDAO dao = new RecordingDeletionDAO();
        AccountDeletionService service = new AccountDeletionService(dao);

        DeletionSummary summary = service.deleteCvForGraduate(12L, "DELETE CV");

        assertEquals(12L, dao.lastGraduateId);
        assertEquals(1, summary.getDeletedCvs());
    }

    private static User user(Long id, String email, User.Role role) {
        User user = new User();
        user.setUserId(id);
        user.setEmail(email);
        user.setRole(role);
        user.setStatus(User.Status.ACTIVE);
        return user;
    }

    private static class RecordingDeletionDAO implements AccountDeletionDAO {
        private int deletedUsers;
        private int deletedCvs;
        private Long lastDeletedUserId;
        private Long lastGraduateId;

        @Override
        public DeletionSummary deleteUserCompletely(Long userId) {
            deletedUsers++;
            lastDeletedUserId = userId;
            DeletionSummary summary = new DeletionSummary();
            summary.setDeletedUsers(1);
            return summary;
        }

        @Override
        public DeletionSummary deleteCvByGraduateId(Long graduateId) {
            deletedCvs++;
            lastGraduateId = graduateId;
            DeletionSummary summary = new DeletionSummary();
            summary.setDeletedCvs(1);
            return summary;
        }
    }
}
