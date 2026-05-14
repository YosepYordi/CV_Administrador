package com.cvmanager.controllers;

import com.cvmanager.dao.impl.CareerDAOImpl;
import com.cvmanager.dao.impl.CompanyDAOImpl;
import com.cvmanager.dao.impl.ContactRequestDAOImpl;
import com.cvmanager.dao.impl.EgresadoDAOImpl;
import com.cvmanager.dao.impl.UsuarioDAOImpl;
import com.cvmanager.dao.interfaces.CompanyDAO;
import com.cvmanager.dao.interfaces.ContactRequestDAO;
import com.cvmanager.models.CV;
import com.cvmanager.models.ContactRequest;
import com.cvmanager.models.Egresados;
import com.cvmanager.models.User;
import com.cvmanager.services.AccountDeletionService;
import com.cvmanager.services.Archivo_Servicio;
import com.cvmanager.services.CVService;
import com.cvmanager.utils.Constantes;
import com.cvmanager.utils.PasswordUtil;
import com.cvmanager.utils.ValidacionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@WebServlet(urlPatterns = {"/graduate/dashboard", "/graduate/profile", "/graduate/profile/edit", "/graduate/requests", "/graduate/companies"})
@MultipartConfig(maxFileSize = 5 * 1024 * 1024)
public class ProfileServlet extends BaseServlet {
    private final EgresadoDAOImpl egresadoDAO = new EgresadoDAOImpl();
    private final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
    private final CareerDAOImpl careerDAO = new CareerDAOImpl();
    private final CompanyDAO companyDAO = new CompanyDAOImpl();
    private final ContactRequestDAO contactRequestDAO = new ContactRequestDAOImpl();
    private final CVService cvService = new CVService();
    private final Archivo_Servicio archivoServicio = new Archivo_Servicio();
    private final AccountDeletionService accountDeletionService = new AccountDeletionService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Egresados graduate = loadGraduate(request);
            request.setAttribute("graduate", graduate);
            request.setAttribute("careers", careerDAO.findActive());
            if ("/graduate/dashboard".equals(request.getServletPath())) {
                request.setAttribute("cv", cvService.getOrCreateByGraduateId(graduate.getGraduateId()));
                forward(request, response, "/WEB-INF/views/graduate/Panel.jsp", "Panel del egresado");
            } else if ("/graduate/requests".equals(request.getServletPath())) {
                request.setAttribute("requests", contactRequestDAO.findByGraduateId(graduate.getGraduateId()));
                forward(request, response, "/WEB-INF/views/graduate/Solicitudes.jsp", "Solicitudes de contacto");
            } else if ("/graduate/companies".equals(request.getServletPath())) {
                request.setAttribute("companies", companyDAO.findAllActive());
                forward(request, response, "/WEB-INF/views/graduate/Empresas.jsp", "Empresas registradas");
            } else if ("/graduate/profile/edit".equals(request.getServletPath())) {
                forward(request, response, "/WEB-INF/views/graduate/editarPerfil.jsp", "Editar perfil");
            } else {
                forward(request, response, "/WEB-INF/views/graduate/Perfil.jsp", "Mi perfil");
            }
        } catch (Exception ex) {
            request.setAttribute("formError", ex.getMessage());
            forward(request, response, "/WEB-INF/views/graduate/Perfil.jsp", "Mi perfil");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            if ("changePassword".equals(action)) {
                changePassword(request, response);
            } else if ("deleteAccount".equals(action)) {
                deleteAccount(request, response);
            } else if ("acceptRequest".equals(action) || "rejectRequest".equals(action)) {
                updateContactRequest(request, response, action);
            } else if ("requestCompanyContact".equals(action)) {
                requestCompanyContact(request, response);
            } else {
                updateProfile(request, response);
            }
        } catch (Exception ex) {
            request.setAttribute("formError", ex.getMessage());
            doGet(request, response);
        }
    }

    private void updateContactRequest(HttpServletRequest request, HttpServletResponse response, String action) throws Exception {
        Egresados graduate = loadGraduate(request);
        Long requestId = ValidacionUtil.parseLong(request.getParameter("requestId"))
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no valida."));
        ContactRequest.Status status = "acceptRequest".equals(action)
                ? ContactRequest.Status.ACCEPTED
                : ContactRequest.Status.REJECTED;
        if (!contactRequestDAO.updateStatusForGraduate(requestId, graduate.getGraduateId(), status)) {
            throw new IllegalArgumentException("No se pudo actualizar la solicitud.");
        }
        setSuccess(request, status == ContactRequest.Status.ACCEPTED ? "Solicitud aceptada." : "Solicitud rechazada.");
        redirect(request, response, "/graduate/requests");
    }

    private void requestCompanyContact(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Egresados graduate = loadGraduate(request);
        Long companyId = ValidacionUtil.parseLong(request.getParameter("companyId"))
                .orElseThrow(() -> new IllegalArgumentException("Empresa no valida."));
        Long requestId = contactRequestDAO.createForCompany(graduate.getGraduateId(), companyId, ValidacionUtil.sanitize(request.getParameter("message")));
        if (requestId == null) throw new IllegalArgumentException("No se pudo enviar la solicitud.");
        setSuccess(request, "Solicitud enviada a la empresa.");
        redirect(request, response, "/graduate/companies");
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Egresados e = loadGraduate(request);
        e.setFirstName(ValidacionUtil.sanitize(request.getParameter("firstName")));
        e.setLastName(ValidacionUtil.sanitize(request.getParameter("lastName")));
        e.setDocumentType(ValidacionUtil.sanitize(request.getParameter("documentType")));
        e.setDocumentNumber(ValidacionUtil.sanitize(request.getParameter("documentNumber")));
        e.setPhone(ValidacionUtil.sanitize(request.getParameter("phone")));
        e.setAddress(ValidacionUtil.sanitize(request.getParameter("address")));
        e.setCity(ValidacionUtil.sanitize(request.getParameter("city")));
        e.setCountry(ValidacionUtil.sanitize(request.getParameter("country")));
        e.setBirthDate(parseDate(request.getParameter("birthDate")));
        e.setLinkedinUrl(ValidacionUtil.sanitize(request.getParameter("linkedinUrl")));
        e.setPortfolioUrl(ValidacionUtil.sanitize(request.getParameter("portfolioUrl")));
        e.setCareerId(ValidacionUtil.parseLong(request.getParameter("careerId")).orElse(null));
        e.setGraduationYear(ValidacionUtil.parseInteger(request.getParameter("graduationYear")).orElse(null));
        e.setExpectedSalary(parseMoney(request.getParameter("expectedSalary")));
        e.setAvailability(ValidacionUtil.sanitize(request.getParameter("availability")));
        e.setPublic("on".equals(request.getParameter("isPublic")));
        Part photo = request.getPart("photo");
        String savedPhoto = archivoServicio.savePhoto(photo, getServletContext());
        if (savedPhoto != null) e.setPhotoUrl(request.getContextPath() + savedPhoto);
        egresadoDAO.update(e);
        request.getSession().setAttribute(Constantes.SESSION_GRADUATE, e);
        setSuccess(request, "Perfil actualizado correctamente.");
        redirect(request, response, "/graduate/profile");
    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
        loadGraduate(request);
        User user = currentUser(request);
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        String error = validatePasswordChange(currentPassword, newPassword, confirmPassword, user.getPasswordHash());
        if (error != null) throw new IllegalArgumentException(error);

        String newHash = PasswordUtil.hashPassword(newPassword);
        usuarioDAO.updatePasswordHash(user.getUserId(), newHash);
        user.setPasswordHash(newHash);
        request.getSession().setAttribute(Constantes.SESSION_USER, user);
        setSuccess(request, "Contrasena actualizada correctamente.");
        redirect(request, response, "/graduate/profile");
    }

    private void deleteAccount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Egresados graduate = loadGraduate(request);
        User user = currentUser(request);
        String photoUrl = graduate.getPhotoUrl();
        CV cv = cvService.findByGraduateId(graduate.getGraduateId()).orElse(null);
        accountDeletionService.deleteOwnAccount(user, request.getParameter("confirmEmail"));
        safeDeleteStoredFile(photoUrl, request);
        if (cv != null) safeDeleteStoredFile(cv.getCvPdfUrl(), request);
        request.getSession().invalidate();
        request.getSession(true).setAttribute(Constantes.SESSION_FLASH_SUCCESS, "Cuenta eliminada completamente.");
        redirect(request, response, "/auth/login");
    }

    private void safeDeleteStoredFile(String url, HttpServletRequest request) {
        try {
            archivoServicio.deleteStoredFile(url, getServletContext(), request.getContextPath());
        } catch (IOException ignored) {
        }
    }

    public static String validatePasswordChange(String currentPassword, String newPassword, String confirmPassword, String passwordHash) {
        if (!PasswordUtil.verifyPassword(currentPassword, passwordHash)) {
            return "La contrasena actual no coincide.";
        }
        if (!ValidacionUtil.isStrongPassword(newPassword)) {
            return "La nueva contrasena debe tener minimo 8 caracteres, letras y numeros.";
        }
        if (!newPassword.equals(confirmPassword)) {
            return "La confirmacion de contrasena no coincide.";
        }
        return null;
    }

    private Egresados loadGraduate(HttpServletRequest request) throws Exception {
        User user = currentUser(request);
        if (user == null) throw new IllegalStateException("Sesion expirada.");
        Egresados graduate = egresadoDAO.findByUserId(user.getUserId()).orElseThrow(() -> new IllegalStateException("Perfil de egresado no encontrado."));
        request.getSession().setAttribute(Constantes.SESSION_GRADUATE, graduate);
        return graduate;
    }

    private LocalDate parseDate(String value) {
        try { return ValidacionUtil.isBlank(value) ? null : LocalDate.parse(value); } catch (Exception ex) { return null; }
    }

    private BigDecimal parseMoney(String value) {
        try { return ValidacionUtil.isBlank(value) ? null : new BigDecimal(value); } catch (Exception ex) { return null; }
    }
}
