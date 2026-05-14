<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<c:set var="summaryPending" value="${empty cv.professionalSummary || cv.professionalSummary == 'Resumen profesional pendiente de completar.'}" />

<section class="row g-4">
    <div class="col-12">
        <div class="hero-card">
            <span class="eyebrow">Bienvenido</span>
            <h1 class="display-6 mt-3">${graduate.fullName}</h1>
            <p class="muted mb-4">${empty cv.title ? 'Aun no has definido un titular profesional.' : cv.title}</p>
            <div class="row g-3">
                <div class="col-md-6 col-xl-3">
                    <div class="metric-card">
                        <div class="muted small">Visible para empresas</div>
                        <div class="metric-value">${cv.published ? 'Si' : 'No'}</div>
                        <form class="mt-3" method="post" action="${pageContext.request.contextPath}/graduate/cv">
                            <input type="hidden" name="action" value="togglePublished">
                            <input type="hidden" name="published" value="${cv.published ? 'false' : 'true'}">
                            <button class="btn ${cv.published ? 'btn-outline-secondary' : 'btn-brand'} btn-sm" type="submit">
                                ${cv.published ? 'Ocultar a empresas' : 'Publicar CV'}
                            </button>
                        </form>
                    </div>
                </div>
                <div class="col-md-6 col-xl-3">
                    <div class="metric-card">
                        <div class="muted small">PDF adjunto</div>
                        <div class="metric-value">${empty cv.cvPdfUrl ? 'No' : 'Si'}</div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-3">
                    <div class="metric-card">
                        <div class="muted small">Visualizaciones</div>
                        <div class="metric-value">${cv.viewsCount}</div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-3">
                    <div class="metric-card">
                        <div class="muted small">Disponibilidad</div>
                        <div class="metric-value">${empty graduate.availability ? 'Por definir' : graduate.availability}</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="col-12">
        <div class="dashboard-card p-4">
            <h2 class="section-title h4">Resumen profesional</h2>
            <c:choose>
                <c:when test="${summaryPending}">
                    <div class="empty-state">Aun no hay resumen profesional guardado.</div>
                </c:when>
                <c:otherwise>
                    <p class="muted mb-0">${cv.professionalSummary}</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
