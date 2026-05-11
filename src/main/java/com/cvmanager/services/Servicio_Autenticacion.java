package com.cvmanager.services;

import com.cvmanager.config.AppConfig;
import com.cvmanager.dao.impl.CompanyDAOImpl;
import com.cvmanager.dao.impl.EgresadoDAOImpl;
import com.cvmanager.dao.impl.PasswordResetTokenDAOImpl;
import com.cvmanager.dao.impl.UsuarioDAOImpl;
import com.cvmanager.dao.interfaces.CompanyDAO;
import com.cvmanager.dao.interfaces.EgresadosDAO;
import com.cvmanager.dao.interfaces.PasswordResetTokenDAO;
import com.cvmanager.dao.interfaces.UsuarioDAO;
import com.cvmanager.models.Company;
import com.cvmanager.models.Egresados;
import com.cvmanager.models.PasswordResetToken;
import com.cvmanager.models.User;
import com.cvmanager.utils.PasswordUtil;
import com.cvmanager.utils.TokenUtil;
import com.cvmanager.utils.ValidacionUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public class Servicio_Autenticacion {
    private final UsuarioDAO usuarioDAO;
    private final EgresadosDAO egresadosDAO;
    private final CompanyDAO companyDAO;
    private final PasswordResetTokenDAO passwordResetTokenDAO;
    private final EmailServicio emailServicio;

    public Servicio_Autenticacion() {
        this(new UsuarioDAOImpl(), new EgresadoDAOImpl(), new CompanyDAOImpl(), new PasswordResetTokenDAOImpl(), new EmailServicio());
    }

    public Servicio_Autenticacion(UsuarioDAO usuarioDAO, EgresadosDAO egresadosDAO, EmailServicio emailServicio) {
        this(usuarioDAO, egresadosDAO, new CompanyDAOImpl(), emailServicio);
    }

    public Servicio_Autenticacion(UsuarioDAO usuarioDAO, EgresadosDAO egresadosDAO, CompanyDAO companyDAO, EmailServicio emailServicio) {
        this(usuarioDAO, egresadosDAO, companyDAO, new PasswordResetTokenDAOImpl(), emailServicio);
    }

    public Servicio_Autenticacion(UsuarioDAO usuarioDAO, EgresadosDAO egresadosDAO, CompanyDAO companyDAO,
                                  PasswordResetTokenDAO passwordResetTokenDAO, EmailServicio emailServicio) {
        this.usuarioDAO = usuarioDAO;
        this.egresadosDAO = egresadosDAO;
        this.companyDAO = companyDAO;
        this.passwordResetTokenDAO = passwordResetTokenDAO;
        this.emailServicio = emailServicio;
    }

    public User autenticar(String email, String password) throws SQLException {
        if (!ValidacionUtil.isValidEmail(email) || ValidacionUtil.isBlank(password)) {
            throw new IllegalArgumentException("Credenciales incompletas.");
        }
        User user = usuarioDAO.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuario no registrado."));
        if (user.getStatus() != User.Status.ACTIVE) {
            throw new IllegalArgumentException("La cuenta no esta activa. Contacta con el administrador.");
        }
        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("La contrasena es incorrecta.");
        }
        usuarioDAO.updateLastLogin(user.getUserId(), LocalDateTime.now());
        return user;
    }

    public User registrarEgresado(String email, String password, String firstName, String lastName, Long careerId, Integer graduationYear) throws SQLException {
        validateNewUser(email, password);
        if (!ValidacionUtil.isInstitutionalEmail(email, AppConfig.institutionalDomain())) {
            throw new IllegalArgumentException("El egresado debe usar correo institucional: " + AppConfig.institutionalDomain());
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(PasswordUtil.hashPassword(password));
        user.setRole(User.Role.GRADUATE);
        user.setStatus(User.Status.ACTIVE);
        Long userId = usuarioDAO.create(user);
        user.setUserId(userId);

        Egresados egresado = new Egresados();
        egresado.setUserId(userId);
        egresado.setFirstName(ValidacionUtil.sanitize(firstName));
        egresado.setLastName(ValidacionUtil.sanitize(lastName));
        egresado.setCareerId(careerId);
        egresado.setGraduationYear(graduationYear);
        egresado.setCountry("Peru");
        egresado.setPublic(true);
        egresadosDAO.create(egresado);
        emailServicio.sendVerificationEmail(email);
        return user;
    }

    public User registrarEmpresa(String email, String password) throws SQLException {
        validateNewUser(email, password);
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(PasswordUtil.hashPassword(password));
        user.setRole(User.Role.COMPANY);
        user.setStatus(User.Status.ACTIVE);
        Long userId = usuarioDAO.create(user);
        user.setUserId(userId);
        Company company = new Company();
        company.setUserId(userId);
        company.setCompanyName(defaultCompanyName(email));
        companyDAO.create(company);
        emailServicio.sendVerificationEmail(email);
        return user;
    }

    public void solicitarRecuperacion(String email) throws SQLException {
        solicitarRecuperacion(email, null);
    }

    public String solicitarRecuperacion(String email, String baseUrl) throws SQLException {
        User user = usuarioDAO.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("No existe una cuenta con ese correo."));
        String rawToken = TokenUtil.generateSecureToken();
        String tokenHash = TokenUtil.sha256(rawToken);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);
        passwordResetTokenDAO.create(user.getUserId(), tokenHash, expiresAt);
        emailServicio.sendPasswordResetEmail(email, buildResetUrl(baseUrl, rawToken));
        return rawToken;
    }

    public void restablecerPassword(String rawToken, String newPassword, String confirmPassword) throws SQLException {
        if (ValidacionUtil.isBlank(rawToken)) {
            throw new IllegalArgumentException("El enlace de recuperacion no es valido o ya expiro.");
        }
        if (!ValidacionUtil.isStrongPassword(newPassword)) {
            throw new IllegalArgumentException("La contrasena debe tener minimo 8 caracteres, letras y numeros.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("La confirmacion de contrasena no coincide.");
        }
        String tokenHash = TokenUtil.sha256(rawToken);
        PasswordResetToken token = passwordResetTokenDAO
                .findValidByTokenHash(tokenHash, LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("El enlace de recuperacion no es valido o ya expiro."));
        User user = usuarioDAO.findById(token.getUserId()).orElseThrow(() ->
                new IllegalArgumentException("El enlace de recuperacion no es valido o ya expiro."));
        user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
        if (!usuarioDAO.update(user)) {
            throw new IllegalStateException("No se pudo actualizar la contrasena.");
        }
        passwordResetTokenDAO.markUsed(token.getTokenId(), LocalDateTime.now());
    }

    public Optional<Egresados> getGraduateProfile(Long userId) throws SQLException {
        return egresadosDAO.findByUserId(userId);
    }

    private void validateNewUser(String email, String password) throws SQLException {
        if (!ValidacionUtil.isValidEmail(email)) throw new IllegalArgumentException("Correo no valido.");
        if (!ValidacionUtil.isStrongPassword(password)) throw new IllegalArgumentException("La contrasena debe tener minimo 8 caracteres, letras y numeros.");
        if (usuarioDAO.findByEmail(email).isPresent()) throw new IllegalArgumentException("El correo ya esta registrado.");
    }

    private String buildResetUrl(String baseUrl, String rawToken) {
        if (ValidacionUtil.isBlank(baseUrl)) {
            return "/auth/reset-password?token=" + rawToken;
        }
        return baseUrl.replaceAll("/+$", "") + "/auth/reset-password?token=" + rawToken;
    }

    private String defaultCompanyName(String email) {
        String localPart = email == null ? "" : email.split("@", 2)[0];
        return ValidacionUtil.sanitize(localPart);
    }
}
