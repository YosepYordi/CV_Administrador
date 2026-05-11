<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="row g-4">
    <div class="col-12">
        <div class="dashboard-card p-4">
            <div class="d-flex flex-wrap justify-content-between align-items-center gap-3">
                <div>
                    <h1 class="section-title h3 mb-1">Reportes y exportacion</h1>
                    <p class="muted mb-0">Descarga informacion operativa en CSV para analisis externo.</p>
                </div>
                <div class="d-flex flex-wrap gap-2">
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/reports?export=csv&type=metrics">M&eacute;tricas CSV</a>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/reports?export=csv&type=users">Usuarios CSV</a>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/reports?export=csv&type=cvs">CV publicados CSV</a>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/reports?export=csv&type=careers">Carreras CSV</a>
                </div>
            </div>
        </div>
    </div>
    <div class="col-lg-5">
        <div class="dashboard-card p-4">
            <h2 class="section-title h3 mb-4">Distribucion por carrera</h2>
            <canvas id="careerChart"></canvas>
        </div>
    </div>
    <div class="col-lg-7">
        <div class="dashboard-card p-4 mb-4">
            <h2 class="section-title h4 mb-3">M&eacute;tricas operativas</h2>
            <div class="admin-grid">
                <div class="metric-card"><div class="muted small">Usuarios</div><div class="metric-value">${stats.totalUsers}</div></div>
                <div class="metric-card"><div class="muted small">CV totales</div><div class="metric-value">${stats.totalCvs}</div></div>
                <div class="metric-card"><div class="muted small">Carreras activas</div><div class="metric-value">${stats.activeCareers}</div></div>
                <div class="metric-card"><div class="muted small">Favoritos</div><div class="metric-value">${stats.totalFavorites}</div></div>
                <div class="metric-card"><div class="muted small">Solicitudes pendientes</div><div class="metric-value">${stats.pendingContactRequests}</div></div>
                <div class="metric-card"><div class="muted small">Solicitudes cerradas</div><div class="metric-value">${stats.acceptedContactRequests + stats.rejectedContactRequests}</div></div>
            </div>
        </div>
        <div class="dashboard-card p-4 h-100">
            <h2 class="section-title h4 mb-3">CV publicados recientemente</h2>
            <div class="table-responsive">
                <table class="table align-middle">
                    <thead><tr><th>Nombre</th><th>Carrera</th><th>Titulo</th><th>Vistas</th></tr></thead>
                    <tbody>
                    <c:forEach items="${recentCvs}" var="cv">
                        <tr>
                            <td>${cv.graduateName}</td>
                            <td>${cv.careerName}</td>
                            <td>${cv.title}</td>
                            <td>${cv.viewsCount}</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty recentCvs}">
                        <tr>
                            <td colspan="4" class="text-center muted py-4">No hay CV publicados disponibles.</td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="col-12">
        <div class="dashboard-card p-4">
            <h2 class="section-title h4 mb-3">Solicitudes por estado</h2>
            <div class="row g-3">
                <c:forEach items="${stats.contactRequestsByStatus}" var="entry">
                    <div class="col-md-4">
                        <div class="metric-card">
                            <div class="muted small">${entry.key}</div>
                            <div class="metric-value">${entry.value}</div>
                        </div>
                    </div>
                </c:forEach>
                <c:if test="${empty stats.contactRequestsByStatus}">
                    <div class="col-12"><div class="empty-state">No hay solicitudes de contacto registradas.</div></div>
                </c:if>
            </div>
        </div>
    </div>
    <div class="col-12">
        <div class="dashboard-card p-4">
            <h2 class="section-title h4 mb-3">Auditoria reciente</h2>
            <div class="table-responsive">
                <table class="table align-middle">
                    <thead><tr><th>Fecha</th><th>Usuario</th><th>Accion</th><th>Entidad</th><th>Detalle</th><th>IP</th></tr></thead>
                    <tbody>
                    <c:forEach items="${auditLogs}" var="log">
                        <tr>
                            <td>${log.createdAt}</td>
                            <td>${log.userId}</td>
                            <td>${log.action}</td>
                            <td>${log.entityType} #${log.entityId}</td>
                            <td>${log.details}</td>
                            <td>${log.ipAddress}</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty auditLogs}">
                        <tr>
                            <td colspan="6" class="text-center muted py-4">No hay registros de auditoria disponibles.</td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</section>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.3/dist/chart.umd.min.js"></script>
<script>
    (() => {
        const canvas = document.getElementById("careerChart");
        if (!canvas) return;
        const labels = [
            <c:forEach items="${stats.graduatesByCareer}" var="entry" varStatus="loop">
                "${entry.key}"<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
        ];
        const values = [
            <c:forEach items="${stats.graduatesByCareer}" var="entry" varStatus="loop">
                ${entry.value}<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
        ];
        new Chart(canvas, {
            type: "doughnut",
            data: {
                labels,
                datasets: [{
                    data: values,
                    backgroundColor: ["#0e7490", "#f59e0b", "#155e75", "#94a3b8", "#f97316"]
                }]
            },
            options: {
                plugins: {
                    legend: {
                        position: "bottom"
                    }
                }
            }
        });
    })();
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
