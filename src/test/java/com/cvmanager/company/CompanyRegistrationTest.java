package com.cvmanager.company;

import com.cvmanager.dao.interfaces.CompanyDAO;
import com.cvmanager.dao.interfaces.EgresadosDAO;
import com.cvmanager.dao.interfaces.UsuarioDAO;
import com.cvmanager.models.Company;
import com.cvmanager.models.Egresados;
import com.cvmanager.models.User;
import com.cvmanager.services.EmailServicio;
import com.cvmanager.services.Servicio_Autenticacion;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CompanyRegistrationTest {
    @Test
    void registrarEmpresaCreatesCompanyLinkedToUser() throws Exception {
        FakeUsuarioDAO usuarioDAO = new FakeUsuarioDAO();
        FakeCompanyDAO companyDAO = new FakeCompanyDAO();
        Servicio_Autenticacion auth = new Servicio_Autenticacion(
                usuarioDAO,
                new UnusedEgresadosDAO(),
                companyDAO,
                new EmailServicio()
        );

        User user = auth.registrarEmpresa("empresa@example.com", "Clave123");

        assertNotNull(user.getUserId());
        assertEquals(User.Role.COMPANY, user.getRole());
        assertEquals(user.getUserId(), companyDAO.created.getUserId());
        assertEquals("empresa", companyDAO.created.getCompanyName());
    }

    private static class FakeUsuarioDAO implements UsuarioDAO {
        @Override public Optional<User> findById(Long id) { return Optional.empty(); }
        @Override public Optional<User> findByEmail(String email) { return Optional.empty(); }
        @Override public List<User> findAll() { return List.of(); }
        @Override public List<User> findByRoleAndStatus(String role, String status) { return List.of(); }
        @Override public Long create(User user) {
            user.setUserId(42L);
            return 42L;
        }
        @Override public boolean update(User user) { return false; }
        @Override public boolean updateStatus(Long userId, User.Status status) { return false; }
        @Override public boolean updateLastLogin(Long userId, LocalDateTime loginAt) { return false; }
        @Override public long countByRole(User.Role role) { return 0; }
    }

    private static class FakeCompanyDAO implements CompanyDAO {
        private Company created;

        @Override public Optional<Company> findByUserId(Long userId) { return Optional.empty(); }
        @Override public Optional<Company> findById(Long companyId) { return Optional.empty(); }
        @Override public Long create(Company company) {
            this.created = company;
            company.setCompanyId(7L);
            return 7L;
        }
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
