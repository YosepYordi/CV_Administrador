package com.cvmanager.auth;

import com.cvmanager.dao.interfaces.CompanyDAO;
import com.cvmanager.dao.interfaces.EgresadosDAO;
import com.cvmanager.dao.interfaces.PasswordResetTokenDAO;
import com.cvmanager.dao.interfaces.UsuarioDAO;
import com.cvmanager.models.Company;
import com.cvmanager.models.Egresados;
import com.cvmanager.models.PasswordResetToken;
import com.cvmanager.models.User;
import com.cvmanager.services.EmailServicio;
import com.cvmanager.services.Servicio_Autenticacion;
import com.cvmanager.utils.PasswordUtil;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetServiceTest {
    @Test
    void solicitarRecuperacionCreatesExpiringTokenAndSendsResetUrl() throws Exception {
        FakeUsuarioDAO usuarioDAO = new FakeUsuarioDAO();
        FakePasswordResetTokenDAO tokenDAO = new FakePasswordResetTokenDAO();
        CapturingEmailServicio emailServicio = new CapturingEmailServicio();
        Servicio_Autenticacion auth = new Servicio_Autenticacion(
                usuarioDAO,
                new UnusedEgresadosDAO(),
                new UnusedCompanyDAO(),
                tokenDAO,
                emailServicio
        );

        auth.solicitarRecuperacion("persona@example.com", "http://localhost:8080/cvmanager");

        assertEquals(10L, tokenDAO.createdUserId);
        assertNotNull(tokenDAO.createdTokenHash);
        assertTrue(tokenDAO.createdExpiresAt.isAfter(LocalDateTime.now()));
        assertEquals("persona@example.com", emailServicio.resetEmail);
        assertTrue(emailServicio.resetUrl.startsWith("http://localhost:8080/cvmanager/auth/reset-password?token="));
    }

    @Test
    void restablecerPasswordUpdatesHashAndConsumesToken() throws Exception {
        FakeUsuarioDAO usuarioDAO = new FakeUsuarioDAO();
        FakePasswordResetTokenDAO tokenDAO = new FakePasswordResetTokenDAO();
        Servicio_Autenticacion auth = new Servicio_Autenticacion(
                usuarioDAO,
                new UnusedEgresadosDAO(),
                new UnusedCompanyDAO(),
                tokenDAO,
                new CapturingEmailServicio()
        );
        String rawToken = auth.solicitarRecuperacion("persona@example.com", "http://localhost:8080/cvmanager");

        auth.restablecerPassword(rawToken, "Nueva123", "Nueva123");

        assertTrue(PasswordUtil.verifyPassword("Nueva123", usuarioDAO.user.getPasswordHash()));
        assertEquals(99L, tokenDAO.usedTokenId);
        assertFalse(tokenDAO.findValidByTokenHash(tokenDAO.createdTokenHash, LocalDateTime.now()).isPresent());
    }

    @Test
    void restablecerPasswordRejectsExpiredOrUsedToken() throws Exception {
        FakeUsuarioDAO usuarioDAO = new FakeUsuarioDAO();
        FakePasswordResetTokenDAO tokenDAO = new FakePasswordResetTokenDAO();
        Servicio_Autenticacion auth = new Servicio_Autenticacion(
                usuarioDAO,
                new UnusedEgresadosDAO(),
                new UnusedCompanyDAO(),
                tokenDAO,
                new CapturingEmailServicio()
        );
        String rawToken = auth.solicitarRecuperacion("persona@example.com", "http://localhost:8080/cvmanager");
        tokenDAO.token.setUsedAt(LocalDateTime.now());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> auth.restablecerPassword(rawToken, "Nueva123", "Nueva123"));

        assertEquals("El enlace de recuperacion no es valido o ya expiro.", error.getMessage());
        assertTrue(PasswordUtil.verifyPassword("Actual123", usuarioDAO.user.getPasswordHash()));
    }

    private static class FakeUsuarioDAO implements UsuarioDAO {
        private final User user = new User();

        private FakeUsuarioDAO() {
            user.setUserId(10L);
            user.setEmail("persona@example.com");
            user.setPasswordHash(PasswordUtil.hashPassword("Actual123"));
            user.setRole(User.Role.GRADUATE);
            user.setStatus(User.Status.ACTIVE);
        }

        @Override public Optional<User> findById(Long id) { return id.equals(user.getUserId()) ? Optional.of(user) : Optional.empty(); }
        @Override public Optional<User> findByEmail(String email) { return user.getEmail().equals(email) ? Optional.of(user) : Optional.empty(); }
        @Override public List<User> findAll() { return List.of(user); }
        @Override public List<User> findByRoleAndStatus(String role, String status) { return List.of(user); }
        @Override public Long create(User user) { return null; }
        @Override public boolean update(User updated) {
            user.setPasswordHash(updated.getPasswordHash());
            return true;
        }
        @Override public boolean updateStatus(Long userId, User.Status status) { return false; }
        @Override public boolean updateLastLogin(Long userId, LocalDateTime loginAt) { return false; }
        @Override public long countByRole(User.Role role) { return 0; }
    }

    private static class FakePasswordResetTokenDAO implements PasswordResetTokenDAO {
        private Long createdUserId;
        private String createdTokenHash;
        private LocalDateTime createdExpiresAt;
        private Long usedTokenId;
        private PasswordResetToken token;

        @Override public Long create(Long userId, String tokenHash, LocalDateTime expiresAt) {
            createdUserId = userId;
            createdTokenHash = tokenHash;
            createdExpiresAt = expiresAt;
            token = new PasswordResetToken();
            token.setTokenId(99L);
            token.setUserId(userId);
            token.setTokenHash(tokenHash);
            token.setExpiresAt(expiresAt);
            return 99L;
        }

        @Override public Optional<PasswordResetToken> findValidByTokenHash(String tokenHash, LocalDateTime now) {
            if (token == null || !token.getTokenHash().equals(tokenHash)) return Optional.empty();
            if (token.getUsedAt() != null || !token.getExpiresAt().isAfter(now)) return Optional.empty();
            return Optional.of(token);
        }

        @Override public boolean markUsed(Long tokenId, LocalDateTime usedAt) {
            usedTokenId = tokenId;
            token.setUsedAt(usedAt);
            return true;
        }
    }

    private static class CapturingEmailServicio extends EmailServicio {
        private String resetEmail;
        private String resetUrl;

        @Override public void sendPasswordResetEmail(String email, String resetUrl) {
            this.resetEmail = email;
            this.resetUrl = resetUrl;
        }
    }

    private static class UnusedCompanyDAO implements CompanyDAO {
        @Override public Optional<Company> findByUserId(Long userId) { return Optional.empty(); }
        @Override public Optional<Company> findById(Long companyId) { return Optional.empty(); }
        @Override public Long create(Company company) { return null; }
        @Override public boolean addFavorite(Long companyId, Long cvId) { return false; }
        @Override public boolean removeFavorite(Long companyId, Long cvId) { return false; }
        @Override public boolean isFavorite(Long companyId, Long cvId) { return false; }
        @Override public List<Long> findFavoriteCvIds(Long companyId) { return List.of(); }
    }

    private static class UnusedEgresadosDAO implements EgresadosDAO {
        @Override public Optional<Egresados> findById(Long id) throws SQLException { return Optional.empty(); }
        @Override public Optional<Egresados> findByUserId(Long userId) throws SQLException { return Optional.empty(); }
        @Override public List<Egresados> findAll() throws SQLException { return List.of(); }
        @Override public Long create(Egresados egresado) throws SQLException { return null; }
        @Override public boolean update(Egresados egresado) throws SQLException { return false; }
        @Override public long countAll() throws SQLException { return 0; }
        @Override public Map<String, Long> countByCareer() throws SQLException { return Map.of(); }
    }
}
