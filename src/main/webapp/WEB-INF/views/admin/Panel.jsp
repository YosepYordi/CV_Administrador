<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="row g-4">
    <div class="col-12">
        <div class="hero-card">
            <span class="eyebrow">Administraci&oacute;n central</span>
            <h1 class="display-6 mt-3">Visibilidad r&aacute;pida del sistema de curr&iacute;culums.</h1>
            <p class="muted mb-4">Supervisa cuentas, carreras, curr&iacute;culums publicados y actividad general desde un panel consolidado.</p>
            <div class="admin-grid">
                <div class="metric-card">
                    <div class="muted small">Usuarios</div>
                    <div class="metric-value">${stats.totalUsers}</div>
                </div>
                <div class="metric-card">
                    <div class="muted small">Egresados</div>
                    <div class="metric-value">${stats.totalGraduates}</div>
                </div>
                <div class="metric-card">
                    <div class="muted small">Empresas</div>
                    <div class="metric-value">${stats.totalCompanies}</div>
                </div>
                <div class="metric-card">
                    <div class="muted small">CV publicados</div>
                    <div class="metric-value">${stats.totalPublishedCvs}</div>
                </div>
                <div class="metric-card">
                    <div class="muted small">CV borradores</div>
                    <div class="metric-value">${stats.draftCvs}</div>
                </div>
                <div class="metric-card">
                    <div class="muted small">B&uacute;squedas</div>
                    <div class="metric-value">${stats.totalSearches}</div>
                </div>
                <div class="metric-card">
                    <div class="muted small">Favoritos</div>
                    <div class="metric-value">${stats.totalFavorites}</div>
                </div>
                <div class="metric-card">
                    <div class="muted small">Solicitudes</div>
                    <div class="metric-value">${stats.totalContactRequests}</div>
                </div>
                <div class="metric-card">
                    <div class="muted small">Vistas de CV</div>
                    <div class="metric-value">${stats.totalViews}</div>
                </div>
            </div>
        </div>
    </div>
    <div class="col-12">
        <div class="dashboard-card p-4">
            <div class="row g-4">
                <div class="col-lg-4">
                    <h2 class="section-title h5 mb-3">Usuarios por rol</h2>
                    <c:forEach items="${roleCounts}" var="entry">
                        <div class="d-flex justify-content-between border-bottom py-2">
                            <span>${entry.key}</span>
                            <strong>${entry.value}</strong>
                        </div>
                    </c:forEach>
                </div>
                <div class="col-lg-4">
                    <h2 class="section-title h5 mb-3">Usuarios por estado</h2>
                    <c:forEach items="${statusCounts}" var="entry">
                        <div class="d-flex justify-content-between border-bottom py-2">
                            <span>${entry.key}</span>
                            <strong>${entry.value}</strong>
                        </div>
                    </c:forEach>
                </div>
                <div class="col-lg-4">
                    <h2 class="section-title h5 mb-3">Accesos rapidos</h2>
                    <div class="d-grid gap-2">
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/users">Gestionar usuarios</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/careers">Gestionar carreras</a>
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/reports">Ver reportes</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="col-12">
        <div class="dashboard-card p-4">
            <h2 class="section-title h4 mb-3">Usuarios recientes</h2>
            <div class="table-responsive">
                <table class="table align-middle">
                    <thead><tr><th>Email</th><th>Rol</th><th>Estado</th></tr></thead>
                    <tbody>
                    <c:forEach items="${recentUsers}" var="user">
                        <tr>
                            <td>${user.email}</td>
                            <td>${user.role.label}</td>
                            <td>${user.status.label}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
