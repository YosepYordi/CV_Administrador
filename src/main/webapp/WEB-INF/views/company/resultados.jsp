<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<c:set var="defaultAvatar" value="${pageContext.request.contextPath}/assets/images/default-avatar.png" />

<section class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h1 class="section-title h2 mb-1">Resultados de búsqueda</h1>
        <p class="muted mb-0">Perfiles encontrados según los filtros seleccionados.</p>
    </div>
    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/company/search">Nueva búsqueda</a>
</section>

<section class="row g-4">
    <c:forEach items="${results}" var="cv">
        <div class="col-xl-6">
            <div class="cv-card p-4 h-100">
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        <img class="profile-thumb mb-3" src="${empty cv.graduatePhotoUrl ? defaultAvatar : cv.graduatePhotoUrl}" alt="Foto de ${cv.graduateName}">
                        <h2 class="h4 mb-1">${cv.graduateName}</h2>
                        <div class="muted">${cv.careerName} · ${cv.city}</div>
                    </div>
                    <span class="tag-chip">${cv.yearsOfExperience} años</span>
                </div>
                <p class="mt-3 mb-4">${cv.title}</p>
                <div class="d-flex flex-wrap gap-2">
                    <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/cv/view?id=${cv.cvId}">Ver perfil</a>
                    <c:if test="${not empty cv.cvPdfUrl}">
                        <a class="btn btn-outline-secondary btn-sm" href="${cv.cvPdfUrl}" target="_blank">Ver CV original en PDF</a>
                    </c:if>
                    <c:choose>
                        <c:when test="${favoriteIds.contains(cv.cvId)}">
                            <form method="post" action="${pageContext.request.contextPath}/company/search">
                                <input type="hidden" name="action" value="unfavorite">
                                <input type="hidden" name="cvId" value="${cv.cvId}">
                                <input type="hidden" name="returnTo" value="${fn:escapeXml(returnTo)}">
                                <button class="btn btn-outline-secondary btn-sm" type="submit">Quitar favorito</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <form method="post" action="${pageContext.request.contextPath}/company/search">
                                <input type="hidden" name="action" value="favorite">
                                <input type="hidden" name="cvId" value="${cv.cvId}">
                                <input type="hidden" name="returnTo" value="${fn:escapeXml(returnTo)}">
                                <button class="btn btn-brand btn-sm" type="submit">Guardar favorito</button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
                <form class="mt-3" method="post" action="${pageContext.request.contextPath}/company/search">
                    <input type="hidden" name="action" value="contact">
                    <input type="hidden" name="cvId" value="${cv.cvId}">
                    <input type="hidden" name="returnTo" value="${fn:escapeXml(returnTo)}">
                    <textarea class="form-control form-control-sm mb-2" name="message" rows="2" maxlength="1000" placeholder="Mensaje opcional"></textarea>
                    <button class="btn btn-outline-primary btn-sm" type="submit">Solicitar contacto</button>
                </form>
            </div>
        </div>
    </c:forEach>
    <c:if test="${empty results}">
        <div class="col-12"><div class="empty-state">No se encontraron perfiles con esos criterios.</div></div>
    </c:if>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
