<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="row g-4">
    <div class="col-12">
        <div class="hero-card">
            <span class="eyebrow">Recruiting</span>
            <h1 class="display-6 mt-3">Explora talento egresado con filtros pr&aacute;cticos.</h1>
            <p class="muted mb-4">Busca por carrera, experiencia, ciudad o idioma. Puedes guardar perfiles favoritos para revisar despu&eacute;s.</p>
            <a class="btn btn-brand" href="${pageContext.request.contextPath}/company/search">Iniciar b&uacute;squeda</a>
        </div>
    </div>
    <div class="col-md-4">
        <div class="metric-card">
            <div class="muted small">Favoritos</div>
            <div class="metric-value">${favoriteCount}</div>
        </div>
    </div>
    <div class="col-md-8">
        <div class="dashboard-card p-4">
            <h2 class="section-title h4">CV recientes</h2>
            <div class="row g-3">
                <c:forEach items="${recentCvs}" var="cv">
                    <div class="col-md-6">
                        <div class="metric-card h-100">
                            <strong>${cv.graduateName}</strong><br>
                            <span class="muted">${cv.careerName}</span>
                            <div class="mt-3"><a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/cv/view?id=${cv.cvId}">Ver perfil</a></div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
