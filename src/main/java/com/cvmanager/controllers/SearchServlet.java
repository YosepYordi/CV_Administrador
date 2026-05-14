package com.cvmanager.controllers;

import com.cvmanager.dao.impl.CompanyDAOImpl;
import com.cvmanager.dao.impl.ContactRequestDAOImpl;
import com.cvmanager.dao.interfaces.CompanyDAO;
import com.cvmanager.dao.interfaces.ContactRequestDAO;
import com.cvmanager.models.CV;
import com.cvmanager.models.Company;
import com.cvmanager.models.ContactRequest;
import com.cvmanager.models.SearchCriteria;
import com.cvmanager.models.User;
import com.cvmanager.services.AccountDeletionService;
import com.cvmanager.services.Archivo_Servicio;
import com.cvmanager.services.AuditLogService;
import com.cvmanager.services.BusquedaServicio;
import com.cvmanager.services.CVService;
import com.cvmanager.utils.Constantes;
import com.cvmanager.utils.ValidacionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet(urlPatterns = {"/company/dashboard", "/company/search", "/company/favorites", "/company/requests", "/company/account"})
public class SearchServlet extends BaseServlet {
    private final BusquedaServicio busquedaServicio = new BusquedaServicio();
    private final CVService cvService = new CVService();
    private final CompanyDAO companyDAO = new CompanyDAOImpl();
    private final ContactRequestDAO contactRequestDAO = new ContactRequestDAOImpl();
    private final AuditLogService auditLogService = new AuditLogService();
    private final AccountDeletionService accountDeletionService = new AccountDeletionService();
    private final Archivo_Servicio archivoServicio = new Archivo_Servicio();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Long> favoriteIds = getFavoriteIds(request);
            request.setAttribute("careers", busquedaServicio.listarCarrerasActivas());
            request.setAttribute("favoriteIds", favoriteIds);
            request.setAttribute("favoriteCount", favoriteIds.size());
            request.setAttribute("returnTo", currentCompanyPath(request));
            if ("/company/favorites".equals(request.getServletPath())) {
                List<CV> favoriteCvs = new ArrayList<>();
                for (Long id : favoriteIds) cvService.findById(id, false).ifPresent(favoriteCvs::add);
                request.setAttribute("favorites", favoriteCvs);
                forward(request, response, "/WEB-INF/views/company/Preferencia.jsp", "Perfiles favoritos");
                return;
            }
            if ("/company/requests".equals(request.getServletPath())) {
                Long companyId = resolveCompanyId(request).orElseThrow(() -> new IllegalStateException("No existe perfil de empresa para la sesion actual."));
                request.setAttribute("requests", contactRequestDAO.findByCompanyId(companyId));
                forward(request, response, "/WEB-INF/views/company/Solicitudes.jsp", "Solicitudes enviadas");
                return;
            }
            if ("/company/account".equals(request.getServletPath())) {
                request.setAttribute("company", resolveCompanyId(request)
                        .flatMap(id -> {
                            try { return companyDAO.findById(id); } catch (Exception ex) { return Optional.empty(); }
                        }).orElse(null));
                forward(request, response, "/WEB-INF/views/company/Cuenta.jsp", "Cuenta de empresa");
                return;
            }
            if ("/company/dashboard".equals(request.getServletPath())) {
                request.setAttribute("recentCvs", busquedaServicio.cvsRecientes());
                forward(request, response, "/WEB-INF/views/company/Panel.jsp", "Panel de empresa");
                return;
            }
            SearchCriteria criteria = buildCriteria(request);
            request.setAttribute("criteria", criteria);
            if (hasSearchCriteria(criteria)) {
                List<CV> results = busquedaServicio.buscar(criteria);
                request.setAttribute("results", results);
                auditLogService.record(currentUser(request), "company.search", "cvs", null,
                        describeSearch(criteria, results.size()), request);
                forward(request, response, "/WEB-INF/views/company/resultados.jsp", "Resultados de busqueda");
            } else {
                forward(request, response, "/WEB-INF/views/company/Buscar.jsp", "Busqueda avanzada");
            }
        } catch (Exception ex) {
            request.setAttribute("formError", ex.getMessage());
            forward(request, response, "/WEB-INF/views/company/Buscar.jsp", "Busqueda avanzada");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("deleteAccount".equals(action)) {
                deleteCompanyAccount(request, response);
                return;
            }
            if ("favorite".equals(action)) {
                Long cvId = requiredCvId(request);
                Optional<Long> companyId = resolveCompanyId(request);
                if (companyId.isPresent()) {
                    companyDAO.addFavorite(companyId.get(), cvId);
                } else {
                    getSessionFavorites(request).add(cvId);
                }
                setSuccess(request, "Perfil guardado como favorito.");
                redirect(request, response, safeReturnTo(request));
                return;
            }
            if ("unfavorite".equals(action)) {
                Long cvId = requiredCvId(request);
                Optional<Long> companyId = resolveCompanyId(request);
                if (companyId.isPresent()) {
                    companyDAO.removeFavorite(companyId.get(), cvId);
                } else {
                    getSessionFavorites(request).remove(cvId);
                }
                setSuccess(request, "Perfil quitado de favoritos.");
                redirect(request, response, safeReturnTo(request));
                return;
            }
            if ("contact".equals(action)) {
                Long cvId = requiredCvId(request);
                Long companyId = resolveCompanyId(request).orElseThrow(() -> new IllegalStateException("No existe perfil de empresa para la sesion actual."));
                Long requestId = contactRequestDAO.createForCv(companyId, cvId, ValidacionUtil.sanitize(request.getParameter("message")));
                if (requestId == null) throw new IllegalArgumentException("Solo se puede solicitar contacto a CV publicados.");
                setSuccess(request, "Solicitud de contacto enviada.");
                redirect(request, response, safeReturnTo(request));
                return;
            }
            if ("acceptRequest".equals(action) || "rejectRequest".equals(action)) {
                Long requestId = ValidacionUtil.parseLong(request.getParameter("requestId"))
                        .orElseThrow(() -> new IllegalArgumentException("Solicitud no valida."));
                Long companyId = resolveCompanyId(request).orElseThrow(() -> new IllegalStateException("No existe perfil de empresa para la sesion actual."));
                ContactRequest.Status status = "acceptRequest".equals(action)
                        ? ContactRequest.Status.ACCEPTED
                        : ContactRequest.Status.REJECTED;
                if (!contactRequestDAO.updateStatusForCompany(requestId, companyId, status)) {
                    throw new IllegalArgumentException("No se pudo actualizar la solicitud.");
                }
                setSuccess(request, status == ContactRequest.Status.ACCEPTED ? "Solicitud aceptada." : "Solicitud rechazada.");
                redirect(request, response, "/company/requests");
                return;
            }
        } catch (Exception ex) {
            setError(request, ex.getMessage());
            redirect(request, response, safeReturnTo(request));
            return;
        }
        doGet(request, response);
    }

    private void deleteCompanyAccount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = currentUser(request);
        Company company = user == null ? null : companyDAO.findByUserId(user.getUserId()).orElse(null);
        String logoUrl = company == null ? null : company.getLogoUrl();
        accountDeletionService.deleteOwnAccount(user, request.getParameter("confirmEmail"));
        safeDeleteStoredFile(logoUrl, request);
        request.getSession().invalidate();
        request.getSession(true).setAttribute(Constantes.SESSION_FLASH_SUCCESS, "Cuenta de empresa eliminada completamente.");
        redirect(request, response, "/auth/login");
    }

    private SearchCriteria buildCriteria(HttpServletRequest request) {
        SearchCriteria c = new SearchCriteria();
        c.setCareer(ValidacionUtil.sanitize(request.getParameter("career")));
        c.setSkill(ValidacionUtil.sanitize(request.getParameter("skill")));
        c.setCity(ValidacionUtil.sanitize(request.getParameter("city")));
        c.setLanguage(ValidacionUtil.sanitize(request.getParameter("language")));
        c.setKeyword(ValidacionUtil.sanitize(request.getParameter("keyword")));
        c.setMinExperience(ValidacionUtil.parseInteger(request.getParameter("minExperience")).orElse(null));
        c.setPage(ValidacionUtil.parseInteger(request.getParameter("page")).orElse(1));
        return c;
    }

    private boolean hasSearchCriteria(SearchCriteria c) {
        return !ValidacionUtil.isBlank(c.getCareer()) || !ValidacionUtil.isBlank(c.getSkill()) ||
                !ValidacionUtil.isBlank(c.getCity()) || !ValidacionUtil.isBlank(c.getLanguage()) ||
                !ValidacionUtil.isBlank(c.getKeyword()) || c.getMinExperience() != null;
    }

    @SuppressWarnings("unchecked")
    private Set<Long> getSessionFavorites(HttpServletRequest request) {
        Object value = request.getSession().getAttribute(Constantes.SESSION_FAVORITES);
        if (value instanceof Set) return (Set<Long>) value;
        Set<Long> favorites = new LinkedHashSet<>();
        request.getSession().setAttribute(Constantes.SESSION_FAVORITES, favorites);
        return favorites;
    }

    private List<Long> getFavoriteIds(HttpServletRequest request) throws Exception {
        Optional<Long> companyId = resolveCompanyId(request);
        if (companyId.isPresent()) return companyDAO.findFavoriteCvIds(companyId.get());
        return new ArrayList<>(getSessionFavorites(request));
    }

    private Optional<Long> resolveCompanyId(HttpServletRequest request) throws Exception {
        User user = currentUser(request);
        if (user == null || user.getUserId() == null) return Optional.empty();
        return companyDAO.findByUserId(user.getUserId()).map(Company::getCompanyId);
    }

    private Long requiredCvId(HttpServletRequest request) {
        return ValidacionUtil.parseLong(request.getParameter("cvId"))
                .orElseThrow(() -> new IllegalArgumentException("CV no valido."));
    }

    private String currentCompanyPath(HttpServletRequest request) {
        String query = request.getQueryString();
        return request.getServletPath() + (ValidacionUtil.isBlank(query) ? "" : "?" + query);
    }

    private String safeReturnTo(HttpServletRequest request) {
        String returnTo = request.getParameter("returnTo");
        if (!ValidacionUtil.isBlank(returnTo) && (returnTo.startsWith("/company") || returnTo.startsWith("/cv/view"))) return returnTo;
        return "/company/favorites";
    }

    private String describeSearch(SearchCriteria criteria, int resultCount) {
        return "Filtros: carrera=" + value(criteria.getCareer()) +
                ", habilidad=" + value(criteria.getSkill()) +
                ", ciudad=" + value(criteria.getCity()) +
                ", idioma=" + value(criteria.getLanguage()) +
                ", palabra=" + value(criteria.getKeyword()) +
                ", experienciaMin=" + (criteria.getMinExperience() == null ? "-" : criteria.getMinExperience()) +
                ", resultados=" + resultCount;
    }

    private String value(String value) {
        return ValidacionUtil.isBlank(value) ? "-" : value;
    }

    private void safeDeleteStoredFile(String url, HttpServletRequest request) {
        try {
            archivoServicio.deleteStoredFile(url, getServletContext(), request.getContextPath());
        } catch (IOException ignored) {
        }
    }
}
