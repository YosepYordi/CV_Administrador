package com.cvmanager.controllers;

import com.cvmanager.dao.impl.EgresadoDAOImpl;
import com.cvmanager.models.*;
import com.cvmanager.services.AccountDeletionService;
import com.cvmanager.services.Archivo_Servicio;
import com.cvmanager.services.CVPdfService;
import com.cvmanager.services.CVService;
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
    private final Archivo_Servicio archivoServicio = new Archivo_Servicio();
    private final CVPdfService pdfService = new CVPdfService();
    private final AccountDeletionService accountDeletionService = new AccountDeletionService();

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
        writePdf(response, cv);
    }

    private void writeGraduatePdf(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Egresados graduate = currentGraduate(request);
        CV cv = cvService.getOrCreateByGraduateId(graduate.getGraduateId());
        fillGraduateData(cv, graduate);
        writePdf(response, cv);
    }

    private void writePdf(HttpServletResponse response, CV cv) throws IOException {
        byte[] pdf = pdfService.generate(cv);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"" + pdfFileName(cv) + "\"");
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
    }

    private void fillGraduateData(CV cv, Egresados graduate) {
        if (ValidacionUtil.isBlank(cv.getGraduateName())) cv.setGraduateName(graduate.getFullName());
        if (ValidacionUtil.isBlank(cv.getCareerName())) cv.setCareerName(graduate.getCareerName());
        if (ValidacionUtil.isBlank(cv.getCity())) cv.setCity(graduate.getCity());
    }

    private String pdfFileName(CV cv) {
        String name = ValidacionUtil.isBlank(cv.getGraduateName()) ? "cv" : cv.getGraduateName().toLowerCase();
        String slug = name.replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        return (slug.isBlank() ? "cv" : slug) + ".pdf";
    }

    private boolean isPubliclyVisible(CV cv) {
        return cv != null && cv.isPublished() && cv.isGraduatePublic();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Egresados graduate = currentGraduate(request);
            if ("deleteCv".equals(request.getParameter("action"))) {
                deleteGraduateCv(request, response, graduate);
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
            if (!ValidacionUtil.isBlank(e.getInstitution())) list.add(e);
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
            if (!ValidacionUtil.isBlank(e.getEmpresaNombre())) list.add(e);
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
            if (!ValidacionUtil.isBlank(c.getName())) list.add(c);
        }
        return list;
    }

    private List<String> lines(String text) {
        List<String> lines = new ArrayList<>();
        if (text == null) return lines;
        for (String line : text.split("\\R")) if (!line.trim().isEmpty()) lines.add(line.trim());
        return lines;
    }

    private String get(String[] values, int index) { return index < values.length ? ValidacionUtil.sanitize(values[index]) : ""; }
    private LocalDate date(String value) { try { return ValidacionUtil.isBlank(value) ? null : LocalDate.parse(value); } catch (Exception ex) { return null; } }

    private String joinEducation(List<Educacion> list) {
        StringBuilder sb = new StringBuilder();
        for (Educacion e : list) sb.append(e.getInstitution()).append('|').append(e.getDegree()).append('|').append(e.getFieldOfStudy()).append('|').append(e.getStartDate()).append('|').append(e.getEndDate()).append('|').append(e.getDescription()).append('\n');
        return sb.toString();
    }
    private String joinExperience(List<Experiencia> list) {
        StringBuilder sb = new StringBuilder();
        for (Experiencia e : list) sb.append(e.getEmpresaNombre()).append('|').append(e.getPosicion()).append('|').append(e.getStartDate()).append('|').append(e.getEndDate()).append('|').append(e.getEmploymentType()).append('|').append(e.getResponsibilities()).append('|').append(e.getAchievements()).append('\n');
        return sb.toString();
    }
    private String joinSkills(List<Habilidades> list) {
        StringBuilder sb = new StringBuilder();
        for (Habilidades h : list) sb.append(h.getHabilidadName()).append('|').append(h.getHabilidadCategory().getValue()).append('|').append(h.getPreferenciaLevel()).append('\n');
        return sb.toString();
    }
    private String joinLanguages(List<Idioma> list) {
        StringBuilder sb = new StringBuilder();
        for (Idioma i : list) sb.append(i.getLanguageName()).append('|').append(i.getProficiencyLevel().getValue()).append('|').append(i.getCertifications()).append('\n');
        return sb.toString();
    }
    private String joinCertifications(List<Certificacion> list) {
        StringBuilder sb = new StringBuilder();
        for (Certificacion c : list) sb.append(c.getName()).append('|').append(c.getIssuingOrganization()).append('|').append(c.getIssueDate()).append('|').append(c.getExpirationDate()).append('|').append(c.getCredentialId()).append('|').append(c.getCredentialUrl()).append('\n');
        return sb.toString();
    }
}
