package com.cvmanager.controllers;

import com.cvmanager.dao.impl.CVDAOImpl;
import com.cvmanager.dao.impl.CareerDAOImpl;
import com.cvmanager.dao.impl.EgresadoDAOImpl;
import com.cvmanager.dao.impl.UsuarioDAOImpl;
import com.cvmanager.models.Career;
import com.cvmanager.models.CV;
import com.cvmanager.models.DeletionSummary;
import com.cvmanager.models.DashboardStats;
import com.cvmanager.models.User;
import com.cvmanager.services.AccountDeletionService;
import com.cvmanager.services.AuditLogService;
import com.cvmanager.services.DashboardStatsService;
import com.cvmanager.utils.ValidacionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@WebServlet(urlPatterns = {"/admin/dashboard", "/admin/users", "/admin/careers", "/admin/reports"})
public class AdminServlet extends BaseServlet {
    private final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
    private final EgresadoDAOImpl egresadoDAO = new EgresadoDAOImpl();
    private final CVDAOImpl cvDAO = new CVDAOImpl();
    private final CareerDAOImpl careerDAO = new CareerDAOImpl();
    private final AuditLogService auditLogService = new AuditLogService();
    private final DashboardStatsService dashboardStatsService = new DashboardStatsService();
    private final AccountDeletionService accountDeletionService = new AccountDeletionService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getServletPath();
            if ("/admin/users".equals(path)) {
                request.setAttribute("users", filterUsers(request));
                request.setAttribute("selectedRole", normalize(request.getParameter("role")));
                request.setAttribute("selectedStatus", normalize(request.getParameter("status")));
                request.setAttribute("query", request.getParameter("q"));
                forward(request, response, "/WEB-INF/views/admin/usuarios.jsp", "Gestion de usuarios");
                return;
            }
            if ("/admin/careers".equals(path)) {
                request.setAttribute("careers", careerDAO.findAll());
                forward(request, response, "/WEB-INF/views/admin/Carreras.jsp", "Gestion de carreras");
                return;
            }
            if ("/admin/reports".equals(path)) {
                if ("csv".equalsIgnoreCase(request.getParameter("export"))) {
                    exportCsv(request, response);
                    return;
                }
                request.setAttribute("stats", buildStats());
                request.setAttribute("recentCvs", cvDAO.findPublished(10));
                request.setAttribute("auditLogs", auditLogService.recent(25));
                forward(request, response, "/WEB-INF/views/admin/Reportes.jsp", "Reportes");
                return;
            }
            DashboardStats stats = buildStats();
            request.setAttribute("stats", stats);
            java.util.List<User> users = usuarioDAO.findAll();
            request.setAttribute("recentUsers", users.subList(0, Math.min(10, users.size())));
            request.setAttribute("roleCounts", stats.getRoleCounts());
            request.setAttribute("statusCounts", stats.getStatusCounts());
            forward(request, response, "/WEB-INF/views/admin/Panel.jsp", "Dashboard administrativo");
        } catch (Exception ex) {
            request.setAttribute("formError", "No se pudo cargar el panel. " + ex.getMessage());
            forward(request, response, "/WEB-INF/views/admin/Panel.jsp", "Dashboard administrativo");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getServletPath();
            if ("/admin/users".equals(path)) {
                Long userId = ValidacionUtil.parseLong(request.getParameter("userId")).orElseThrow();
                if ("delete".equals(request.getParameter("action"))) {
                    User target = usuarioDAO.findById(userId).orElseThrow();
                    DeletionSummary summary = accountDeletionService.deleteUserAsAdmin(
                            currentUser(request), userId, request.getParameter("confirmDelete"));
                    auditLogService.record(currentUser(request), "admin.user.delete", "usuarios", userId,
                            "Usuario eliminado: " + target.getEmail() + "; cvs=" + summary.getDeletedCvs()
                                    + "; favoritos=" + summary.getDeletedFavorites()
                                    + "; solicitudes=" + summary.getDeletedContactRequests(), request);
                    setSuccess(request, "Usuario eliminado completamente.");
                    redirect(request, response, "/admin/users");
                    return;
                }
                User.Status status = User.Status.from(request.getParameter("status"));
                usuarioDAO.updateStatus(userId, status);
                auditLogService.record(currentUser(request), "admin.user.status", "usuarios", userId,
                        "Estado actualizado a " + status.getValue(), request);
                setSuccess(request, "Estado de usuario actualizado.");
                redirect(request, response, "/admin/users");
                return;
            }
            if ("/admin/careers".equals(path)) {
                if ("toggle".equals(request.getParameter("action"))) {
                    Long careerId = ValidacionUtil.parseLong(request.getParameter("careerId")).orElseThrow();
                    Career career = careerDAO.findById(careerId).orElseThrow();
                    career.setActive(Boolean.parseBoolean(request.getParameter("active")));
                    careerDAO.update(career);
                    auditLogService.record(currentUser(request), "admin.career.status", "careers", careerId,
                            "Carrera " + career.getCode() + " activa=" + career.isActive(), request);
                    setSuccess(request, "Estado de carrera actualizado.");
                    redirect(request, response, "/admin/careers");
                    return;
                }
                Career career = new Career();
                career.setName(ValidacionUtil.sanitize(request.getParameter("name")));
                career.setCode(ValidacionUtil.sanitize(request.getParameter("code")));
                career.setDescription(ValidacionUtil.sanitize(request.getParameter("description")));
                career.setDurationYears(ValidacionUtil.parseInteger(request.getParameter("durationYears")).orElse(null));
                career.setActive("on".equals(request.getParameter("active")));
                Long careerId = careerDAO.create(career);
                auditLogService.record(currentUser(request), "admin.career.create", "careers", careerId,
                        "Carrera creada: " + career.getName(), request);
                setSuccess(request, "Carrera registrada correctamente.");
                redirect(request, response, "/admin/careers");
                return;
            }
            doGet(request, response);
        } catch (Exception ex) {
            request.setAttribute("formError", ex.getMessage());
            doGet(request, response);
        }
    }

    private DashboardStats buildStats() throws Exception {
        return dashboardStatsService.buildStats();
    }

    private List<User> filterUsers(HttpServletRequest request) throws Exception {
        String role = normalize(request.getParameter("role"));
        String status = normalize(request.getParameter("status"));
        String query = normalize(request.getParameter("q"));
        List<User> users = usuarioDAO.findByRoleAndStatus(role, status);
        if (query == null) return users;
        List<User> filtered = new ArrayList<>();
        for (User user : users) {
            if (user.getEmail() != null && user.getEmail().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) {
                filtered.add(user);
            }
        }
        return filtered;
    }

    private void exportCsv(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String type = normalize(request.getParameter("type"));
        if (type == null) type = "users";
        List<String[]> rows;
        if ("metrics".equals(type)) {
            rows = metricsRows(buildStats());
        } else if ("cvs".equals(type)) {
            rows = publishedCvRows();
        } else if ("careers".equals(type)) {
            rows = careerRows();
        } else {
            rows = userRows();
        }
        auditLogService.record(currentUser(request), "admin.report.export", "reports", null,
                "Exportacion CSV: " + type, request);
        byte[] content = CsvExporter.toCsv(rows).getBytes(StandardCharsets.UTF_8);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"admin-" + type + ".csv\"");
        response.setContentLength(content.length);
        response.getOutputStream().write(content);
    }

    private List<String[]> userRows() throws Exception {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"ID", "Email", "Rol", "Estado", "Creado", "Ultimo login"});
        for (User user : usuarioDAO.findAll()) {
            rows.add(new String[]{
                    String.valueOf(user.getUserId()),
                    user.getEmail(),
                    user.getRole() == null ? "" : user.getRole().getValue(),
                    user.getStatus() == null ? "" : user.getStatus().getValue(),
                    String.valueOf(user.getCreatedAt()),
                    String.valueOf(user.getLastLogin())
            });
        }
        return rows;
    }

    private List<String[]> publishedCvRows() throws Exception {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"ID", "Egresado", "Carrera", "Titulo", "Ciudad", "Vistas", "Actualizado"});
        for (CV cv : cvDAO.findPublished(10000)) {
            rows.add(new String[]{
                    String.valueOf(cv.getCvId()),
                    cv.getGraduateName(),
                    cv.getCareerName(),
                    cv.getTitle(),
                    cv.getCity(),
                    String.valueOf(cv.getViewsCount()),
                    String.valueOf(cv.getUpdatedAt())
            });
        }
        return rows;
    }

    private List<String[]> careerRows() throws Exception {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"ID", "Nombre", "Codigo", "Duracion", "Activa"});
        for (Career career : careerDAO.findAll()) {
            rows.add(new String[]{
                    String.valueOf(career.getCareerId()),
                    career.getName(),
                    career.getCode(),
                    String.valueOf(career.getDurationYears()),
                    String.valueOf(career.isActive())
            });
        }
        return rows;
    }

    private List<String[]> metricsRows(DashboardStats stats) {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"Metrica", "Valor"});
        rows.add(new String[]{"Usuarios", String.valueOf(stats.getTotalUsers())});
        rows.add(new String[]{"Egresados", String.valueOf(stats.getTotalGraduates())});
        rows.add(new String[]{"Empresas", String.valueOf(stats.getTotalCompanies())});
        rows.add(new String[]{"CV totales", String.valueOf(stats.getTotalCvs())});
        rows.add(new String[]{"CV publicados", String.valueOf(stats.getTotalPublishedCvs())});
        rows.add(new String[]{"CV borradores", String.valueOf(stats.getDraftCvs())});
        rows.add(new String[]{"Carreras activas", String.valueOf(stats.getActiveCareers())});
        rows.add(new String[]{"Vistas de CV", String.valueOf(stats.getTotalViews())});
        rows.add(new String[]{"Favoritos", String.valueOf(stats.getTotalFavorites())});
        rows.add(new String[]{"Solicitudes", String.valueOf(stats.getTotalContactRequests())});
        rows.add(new String[]{"Solicitudes pendientes", String.valueOf(stats.getPendingContactRequests())});
        rows.add(new String[]{"Solicitudes aceptadas", String.valueOf(stats.getAcceptedContactRequests())});
        rows.add(new String[]{"Solicitudes rechazadas", String.valueOf(stats.getRejectedContactRequests())});
        rows.add(new String[]{"Busquedas", String.valueOf(stats.getTotalSearches())});
        return rows;
    }

    private Map<String, Long> countByRole(List<User> users) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (User.Role role : User.Role.values()) counts.put(role.getValue(), 0L);
        for (User user : users) {
            String key = user.getRole() == null ? "sin rol" : user.getRole().getValue();
            counts.put(key, counts.getOrDefault(key, 0L) + 1);
        }
        return counts;
    }

    private Map<String, Long> countByStatus(List<User> users) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (User.Status status : User.Status.values()) counts.put(status.getValue(), 0L);
        for (User user : users) {
            String key = user.getStatus() == null ? "sin estado" : user.getStatus().getValue();
            counts.put(key, counts.getOrDefault(key, 0L) + 1);
        }
        return counts;
    }

    private String normalize(String value) {
        String sanitized = ValidacionUtil.sanitize(value);
        return sanitized.isBlank() ? null : sanitized;
    }

    public static final class CsvExporter {
        private CsvExporter() {
        }

        public static String toCsv(List<String[]> rows) {
            StringBuilder csv = new StringBuilder();
            for (String[] row : rows) {
                for (int i = 0; i < row.length; i++) {
                    if (i > 0) csv.append(',');
                    csv.append(escape(row[i]));
                }
                csv.append("\r\n");
            }
            return csv.toString();
        }

        private static String escape(String value) {
            if (value == null || "null".equals(value)) return "";
            String normalized = value.replace('\r', ' ').replace('\n', ' ');
            boolean quote = normalized.contains(",") || normalized.contains("\"");
            normalized = normalized.replace("\"", "\"\"");
            return quote ? "\"" + normalized + "\"" : normalized;
        }
    }
}
