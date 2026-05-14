package com.cvmanager.controllers;

import com.cvmanager.dao.impl.EgresadoDAOImpl;
import com.cvmanager.dao.impl.CompanyDAOImpl;
import com.cvmanager.dao.interfaces.CompanyDAO;
import com.cvmanager.models.*;
import com.cvmanager.services.AccountDeletionService;
import com.cvmanager.services.Archivo_Servicio;
import com.cvmanager.services.CVImportDraft;
import com.cvmanager.services.CVService;
import com.cvmanager.services.OllamaCVImportService;
import com.cvmanager.utils.Constantes;
import com.cvmanager.utils.ValidacionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/graduate/cv", "/graduate/cv/edit", "/graduate/cv/pdf", "/cv/view", "/cv/pdf"})
@MultipartConfig(maxFileSize = 5 * 1024 * 1024)
public class CVServlet extends BaseServlet {
    private static final Logger logger = LogManager.getLogger(CVServlet.class);
    private final CVService cvService = new CVService();
    private final EgresadoDAOImpl egresadoDAO = new EgresadoDAOImpl();
    private final CompanyDAO companyDAO = new CompanyDAOImpl();
    private final Archivo_Servicio archivoServicio = new Archivo_Servicio();
    private final AccountDeletionService accountDeletionService = new AccountDeletionService();
    private final OllamaCVImportService importService = new OllamaCVImportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if ("/cv/pdf".equals(request.getServletPath())) {
                writePublicPdf(request, response);
                return;
            }
            if ("/graduate/cv/pdf".equals(request.getServletPath())) {
                writeGraduatePdf(request, response);
                return;
            }
            if ("/cv/view".equals(request.getServletPath())) {
                Long id = ValidacionUtil.parseLong(request.getParameter("id")).orElse(null);
                CV cv = id == null ? null : cvService.findById(id, true).orElse(null);
                if (!isPubliclyVisible(cv)) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                request.setAttribute("cv", cv);
                request.setAttribute("readonly", true);
                if (isCompanyUser(request)) {
                    request.setAttribute("favoriteIds", companyFavoriteIds(request));
                    request.setAttribute("returnTo", "/cv/view?id=" + cv.getCvId());
                }
                forward(request, response, "/WEB-INF/views/graduate/cv.jsp", "CV publicado");
                return;
            }
            Egresados graduate = currentGraduate(request);
            CV cv = cvService.getOrCreateByGraduateId(graduate.getGraduateId());
            request.setAttribute("graduate", graduate);
            request.setAttribute("cv", cv);
            if ("/graduate/cv/edit".equals(request.getServletPath())) {
                fillEditText(request, cv);
                forward(request, response, "/WEB-INF/views/graduate/editarCV.jsp", "Editar CV");
            } else {
                forward(request, response, "/WEB-INF/views/graduate/cv.jsp", "Mi CV");
            }
        } catch (Exception ex) {
            request.setAttribute("formError", ex.getMessage());
            forward(request, response, "/WEB-INF/views/graduate/cv.jsp", "Mi CV");
        }
    }

    private void writePublicPdf(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long id = ValidacionUtil.parseLong(request.getParameter("id")).orElse(null);
        CV cv = id == null ? null : cvService.findById(id, true).orElse(null);
        if (!isPubliclyVisible(cv)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        redirectToOriginalPdf(request, response, cv);
    }

    private void writeGraduatePdf(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Egresados graduate = currentGraduate(request);
        CV cv = cvService.getOrCreateByGraduateId(graduate.getGraduateId());
        fillGraduateData(cv, graduate);
        redirectToOriginalPdf(request, response, cv);
    }

    private void redirectToOriginalPdf(HttpServletRequest request, HttpServletResponse response, CV cv) throws IOException {
        if (cv == null || ValidacionUtil.isBlank(cv.getCvPdfUrl())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No hay CV original en PDF adjuntado.");
            return;
        }
        String pdfUrl = cv.getCvPdfUrl();
        if (pdfUrl.startsWith(request.getContextPath() + "/")) {
            response.sendRedirect(pdfUrl);
        } else if (pdfUrl.startsWith("/")) {
            response.sendRedirect(request.getContextPath() + pdfUrl);
        } else {
            response.sendRedirect(pdfUrl);
        }
    }

    private void fillGraduateData(CV cv, Egresados graduate) {
        if (ValidacionUtil.isBlank(cv.getGraduateName())) cv.setGraduateName(graduate.getFullName());
        if (ValidacionUtil.isBlank(cv.getGraduatePhotoUrl())) cv.setGraduatePhotoUrl(graduate.getPhotoUrl());
        if (ValidacionUtil.isBlank(cv.getCareerName())) cv.setCareerName(graduate.getCareerName());
        if (ValidacionUtil.isBlank(cv.getCity())) cv.setCity(graduate.getCity());
    }

    private boolean isPubliclyVisible(CV cv) {
        return cv != null && cv.isPublished() && cv.isGraduatePublic();
    }

    private boolean isCompanyUser(HttpServletRequest request) {
        User user = currentUser(request);
        return user != null && user.isCompany();
    }

    private List<Long> companyFavoriteIds(HttpServletRequest request) throws Exception {
        User user = currentUser(request);
        if (user == null || user.getUserId() == null) return List.of();
        return companyDAO.findByUserId(user.getUserId())
                .map(company -> {
                    try {
                        return companyDAO.findFavoriteCvIds(company.getCompanyId());
                    } catch (Exception ex) {
                        return List.<Long>of();
                    }
                })
                .orElse(List.of());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Egresados graduate = currentGraduate(request);
            if ("deleteCv".equals(request.getParameter("action"))) {
                deleteGraduateCv(request, response, graduate);
                return;
            }
            if ("togglePublished".equals(request.getParameter("action"))) {
                togglePublished(request, response, graduate);
                return;
            }
            if ("importAi".equals(request.getParameter("action"))) {
                importGraduateCv(request, response, graduate);
                return;
            }
            CV cv = cvService.getOrCreateByGraduateId(graduate.getGraduateId());
            cv.setTitle(ValidacionUtil.sanitize(request.getParameter("title")));
            cv.setProfessionalSummary(ValidacionUtil.sanitize(request.getParameter("professionalSummary")));
            cv.setPublished("on".equals(request.getParameter("published")));
            Part pdf = request.getPart("cvPdf");
            String savedPdf = archivoServicio.saveCvPdf(pdf, getServletContext());
            if (savedPdf != null) cv.setCvPdfUrl(request.getContextPath() + savedPdf);
            cv.setEducationList(parseEducations(request.getParameter("educationEntries")));
            cv.setExperienceList(parseExperiences(request.getParameter("experienceEntries")));
            cv.setSkills(parseSkills(request.getParameter("skillEntries")));
            cv.setLanguages(parseLanguages(request.getParameter("languageEntries")));
            cv.setCertifications(parseCertifications(request.getParameter("certificationEntries")));
            cvService.save(cv);
            setSuccess(request, "CV guardado correctamente.");
            redirect(request, response, "/graduate/cv");
        } catch (Exception ex) {
            logger.warn("No se pudo guardar el CV.", ex);
            request.setAttribute("formError", ex.getMessage() == null ? "No se pudo guardar el CV." : ex.getMessage());
            doGet(request, response);
        }
    }

    private void importGraduateCv(HttpServletRequest request, HttpServletResponse response, Egresados graduate) throws Exception {
        CV cv = cvService.getOrCreateByGraduateId(graduate.getGraduateId());
        Part pdf = request.getPart("cvPdf");
        CVImportDraft draft = importService.importFromPdf(pdf);
        String previousPdf = cv.getCvPdfUrl();
        String savedPdf = archivoServicio.saveCvPdf(pdf, getServletContext());
        if (savedPdf != null) {
            cv.setCvPdfUrl(request.getContextPath() + savedPdf);
            cvService.save(cv);
            safeDeleteStoredFile(previousPdf, request);
        }
        cv.setTitle(firstNonBlank(draft.getTitle(), request.getParameter("title"), cv.getTitle()));
        cv.setProfessionalSummary(firstNonBlank(draft.getProfessionalSummary(), request.getParameter("professionalSummary"), cv.getProfessionalSummary()));
        cv.setPublished("on".equals(request.getParameter("published")));
        request.setAttribute("graduate", graduate);
        request.setAttribute("cv", cv);
        request.setAttribute("educationText", firstNonBlank(draft.getEducationEntries(), request.getParameter("educationEntries")));
        request.setAttribute("experienceText", firstNonBlank(draft.getExperienceEntries(), request.getParameter("experienceEntries")));
        request.setAttribute("skillsText", firstNonBlank(draft.getSkillEntries(), request.getParameter("skillEntries")));
        request.setAttribute("languagesText", firstNonBlank(draft.getLanguageEntries(), request.getParameter("languageEntries")));
        request.setAttribute("certificationsText", firstNonBlank(draft.getCertificationEntries(), request.getParameter("certificationEntries")));
        setSuccess(request, "CV importado con IA. Revisa los campos antes de guardar.");
        forward(request, response, "/WEB-INF/views/graduate/editarCV.jsp", "Editar CV");
    }

    private void togglePublished(HttpServletRequest request, HttpServletResponse response, Egresados graduate) throws Exception {
        CV cv = cvService.getOrCreateByGraduateId(graduate.getGraduateId());
        cv.setPublished("true".equals(request.getParameter("published")));
        cvService.save(cv);
        setSuccess(request, cv.isPublished() ? "Tu CV ahora es visible para empresas." : "Tu CV ya no es visible para empresas.");
        redirect(request, response, "/graduate/dashboard");
    }

    private void deleteGraduateCv(HttpServletRequest request, HttpServletResponse response, Egresados graduate) throws Exception {
        CV existing = cvService.findByGraduateId(graduate.getGraduateId()).orElse(null);
        accountDeletionService.deleteCvForGraduate(graduate.getGraduateId(), request.getParameter("confirmDeleteCv"));
        if (existing != null) {
            safeDeleteStoredFile(existing.getCvPdfUrl(), request);
        }
        setSuccess(request, "CV eliminado completamente. Puedes crear uno nuevo cuando lo necesites.");
        redirect(request, response, "/graduate/profile");
    }

    private void safeDeleteStoredFile(String url, HttpServletRequest request) {
        try {
            archivoServicio.deleteStoredFile(url, getServletContext(), request.getContextPath());
        } catch (IOException ignored) {
        }
    }

    private Egresados currentGraduate(HttpServletRequest request) throws Exception {
        Egresados graduate = (Egresados) request.getSession().getAttribute(Constantes.SESSION_GRADUATE);
        if (graduate != null) return graduate;
        User user = currentUser(request);
        if (user == null) throw new IllegalStateException("La sesion no tiene usuario autenticado.");
        graduate = egresadoDAO.findByUserId(user.getUserId()).orElseThrow(() -> new IllegalStateException("No existe perfil de egresado para el usuario actual."));
        request.getSession().setAttribute(Constantes.SESSION_GRADUATE, graduate);
        return graduate;
    }

    private void fillEditText(HttpServletRequest request, CV cv) {
        request.setAttribute("educationText", joinEducation(cv.getEducationList()));
        request.setAttribute("experienceText", joinExperience(cv.getExperienceList()));
        request.setAttribute("skillsText", joinSkills(cv.getSkills()));
        request.setAttribute("languagesText", joinLanguages(cv.getLanguages()));
        request.setAttribute("certificationsText", joinCertifications(cv.getCertifications()));
    }

    private List<Educacion> parseEducations(String text) {
        List<Educacion> list = new ArrayList<>();
        for (String line : lines(text)) {
            String[] p = line.split("\\|", -1);
            Educacion e = new Educacion();
            e.setInstitution(get(p, 0));
            e.setDegree(get(p, 1));
            e.setFieldOfStudy(get(p, 2));
            e.setStartDate(date(get(p, 3)));
            e.setEndDate(date(get(p, 4)));
            e.setDescription(get(p, 5));
            if (usefulEducation(e)) list.add(e);
        }
        return list;
    }

    private List<Experiencia> parseExperiences(String text) {
        List<Experiencia> list = new ArrayList<>();
        for (String line : lines(text)) {
            String[] p = line.split("\\|", -1);
            Experiencia e = new Experiencia();
            e.setEmpresaNombre(get(p, 0));
            e.setPosicion(get(p, 1));
            e.setStartDate(date(get(p, 2)));
            e.setEndDate(date(get(p, 3)));
            e.setEmploymentType(get(p, 4));
            e.setResponsibilities(get(p, 5));
            e.setAchievements(get(p, 6));
            if (usefulExperience(e)) list.add(e);
        }
        return list;
    }

    private List<Habilidades> parseSkills(String text) {
        List<Habilidades> list = new ArrayList<>();
        for (String line : lines(text)) {
            String[] p = line.split("\\|", -1);
            Habilidades h = new Habilidades();
            h.setHabilidadName(get(p, 0));
            h.setHabilidadCategory(Habilidades.Category.from(get(p, 1)));
            h.setPreferenciaLevel(ValidacionUtil.parseInteger(get(p, 2)).orElse(3));
            if (!ValidacionUtil.isBlank(h.getHabilidadName())) list.add(h);
        }
        return list;
    }

    private List<Idioma> parseLanguages(String text) {
        List<Idioma> list = new ArrayList<>();
        for (String line : lines(text)) {
            String[] p = line.split("\\|", -1);
            Idioma i = new Idioma();
            i.setLanguageName(get(p, 0));
            i.setProficiencyLevel(Idioma.Proficiency.from(get(p, 1)));
            i.setCertifications(get(p, 2));
            if (!ValidacionUtil.isBlank(i.getLanguageName())) list.add(i);
        }
        return list;
    }

    private List<Certificacion> parseCertifications(String text) {
        List<Certificacion> list = new ArrayList<>();
        for (String line : lines(text)) {
            String[] p = line.split("\\|", -1);
            Certificacion c = new Certificacion();
            c.setName(get(p, 0));
            c.setIssuingOrganization(get(p, 1));
            c.setIssueDate(date(get(p, 2)));
            c.setExpirationDate(date(get(p, 3)));
            c.setCredentialId(get(p, 4));
            c.setCredentialUrl(get(p, 5));
            if (usefulCertification(c)) list.add(c);
        }
        return list;
    }

    private List<String> lines(String text) {
        List<String> lines = new ArrayList<>();
        if (text == null) return lines;
        for (String line : text.split("\\R")) if (!line.trim().isEmpty()) lines.add(line.trim());
        return lines;
    }

    private String get(String[] values, int index) {
        if (index >= values.length) return "";
        String value = ValidacionUtil.sanitize(values[index]);
        if (ValidacionUtil.isBlank(value)) return "";
        String trimmed = value.trim();
        return "null".equalsIgnoreCase(trimmed) || "/".equals(trimmed) ? "" : trimmed;
    }
    private LocalDate date(String value) { try { return ValidacionUtil.isBlank(value) ? null : LocalDate.parse(value); } catch (Exception ex) { return null; } }
    private String firstNonBlank(String... values) {
        for (String value : values) if (!ValidacionUtil.isBlank(value)) return value;
        return "";
    }

    private String joinEducation(List<Educacion> list) {
        StringBuilder sb = new StringBuilder();
        for (Educacion e : list) {
            if (usefulEducation(e)) sb.append(text(e.getInstitution())).append('|').append(text(e.getDegree())).append('|').append(text(e.getFieldOfStudy())).append('|').append(text(e.getStartDate())).append('|').append(text(e.getEndDate())).append('|').append(text(e.getDescription())).append('\n');
        }
        return sb.toString();
    }
    private String joinExperience(List<Experiencia> list) {
        StringBuilder sb = new StringBuilder();
        for (Experiencia e : list) {
            if (usefulExperience(e)) sb.append(text(e.getEmpresaNombre())).append('|').append(text(e.getPosicion())).append('|').append(text(e.getStartDate())).append('|').append(text(e.getEndDate())).append('|').append(text(e.getEmploymentType())).append('|').append(text(e.getResponsibilities())).append('|').append(text(e.getAchievements())).append('\n');
        }
        return sb.toString();
    }
    private String joinSkills(List<Habilidades> list) {
        StringBuilder sb = new StringBuilder();
        for (Habilidades h : list) sb.append(text(h.getHabilidadName())).append('|').append(h.getHabilidadCategory() == null ? "other" : h.getHabilidadCategory().getValue()).append('|').append(h.getPreferenciaLevel()).append('\n');
        return sb.toString();
    }
    private String joinLanguages(List<Idioma> list) {
        StringBuilder sb = new StringBuilder();
        for (Idioma i : list) sb.append(text(i.getLanguageName())).append('|').append(i.getProficiencyLevel() == null ? "basic" : i.getProficiencyLevel().getValue()).append('|').append(text(i.getCertifications())).append('\n');
        return sb.toString();
    }
    private String joinCertifications(List<Certificacion> list) {
        StringBuilder sb = new StringBuilder();
        for (Certificacion c : list) {
            if (usefulCertification(c)) sb.append(text(c.getName())).append('|').append(text(c.getIssuingOrganization())).append('|').append(text(c.getIssueDate())).append('|').append(text(c.getExpirationDate())).append('|').append(text(c.getCredentialId())).append('|').append(text(c.getCredentialUrl())).append('\n');
        }
        return sb.toString();
    }

    private boolean usefulEducation(Educacion education) {
        if (education == null || ValidacionUtil.isBlank(education.getInstitution())) return false;
        return !ValidacionUtil.isBlank(education.getDegree())
                || !ValidacionUtil.isBlank(education.getFieldOfStudy())
                || education.getStartDate() != null
                || education.getEndDate() != null
                || !ValidacionUtil.isBlank(education.getDescription());
    }

    private boolean usefulExperience(Experiencia experience) {
        if (experience == null || ValidacionUtil.isBlank(experience.getEmpresaNombre()) || ValidacionUtil.isBlank(experience.getPosicion())) return false;
        String company = experience.getEmpresaNombre().toLowerCase();
        return !company.contains("sin experiencia") && !company.contains("no aplica");
    }

    private boolean usefulCertification(Certificacion certification) {
        if (certification == null || ValidacionUtil.isBlank(certification.getName())) return false;
        return !ValidacionUtil.isBlank(certification.getIssuingOrganization())
                || certification.getIssueDate() != null
                || certification.getExpirationDate() != null
                || !ValidacionUtil.isBlank(certification.getCredentialId())
                || !ValidacionUtil.isBlank(certification.getCredentialUrl());
    }

    private String text(Object value) {
        if (value == null) return "";
        String text = value.toString().trim();
        return "null".equalsIgnoreCase(text) || "/".equals(text) ? "" : text;
    }
}
