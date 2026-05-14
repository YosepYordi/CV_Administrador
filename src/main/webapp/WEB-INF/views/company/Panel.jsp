<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<c:set var="defaultAvatar" value="${pageContext.request.contextPath}/assets/images/default-avatar.png" />

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
                        <div class="metric-card company-profile-card h-100">
                            <img class="profile-thumb" src="${empty cv.graduatePhotoUrl ? defaultAvatar : cv.graduatePhotoUrl}" alt="Foto de ${cv.graduateName}">
                            <div class="profile-card-body">
                                <strong>${cv.graduateName}</strong><br>
                                <span class="muted">${cv.careerName}</span>
                                <div class="d-flex flex-wrap gap-2 mt-3">
                                    <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/cv/view?id=${cv.cvId}">Ver perfil</a>
                                    <c:choose>
                                        <c:when test="${favoriteIds.contains(cv.cvId)}">
                                            <form method="post" action="${pageContext.request.contextPath}/company/search">
                                                <input type="hidden" name="action" value="unfavorite">
                                                <input type="hidden" name="cvId" value="${cv.cvId}">
                                                <input type="hidden" name="returnTo" value="/company/dashboard">
                                                <button class="btn btn-outline-secondary btn-sm" type="submit">Quitar favorito</button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <form method="post" action="${pageContext.request.contextPath}/company/search">
                                                <input type="hidden" name="action" value="favorite">
                                                <input type="hidden" name="cvId" value="${cv.cvId}">
                                                <input type="hidden" name="returnTo" value="/company/dashboard">
                                                <button class="btn btn-brand btn-sm" type="submit">Guardar favorito</button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
