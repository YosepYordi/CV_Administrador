<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="dashboard-card p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="section-title h2 mb-0">Perfiles favoritos</h1>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/company/search">Seguir buscando</a>
    </div>
    <div class="row g-3">
        <c:forEach items="${favorites}" var="cv">
            <div class="col-md-6 col-xl-4">
                <div class="metric-card h-100">
                    <strong>${cv.graduateName}</strong><br>
                    <span class="muted">${cv.careerName}</span>
                    <p class="mt-2 mb-3">${cv.title}</p>
                    <div class="d-flex flex-wrap gap-2">
                        <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/cv/view?id=${cv.cvId}">Abrir perfil</a>
                        <c:if test="${not empty cv.cvPdfUrl}">
                            <a class="btn btn-outline-secondary btn-sm" href="${cv.cvPdfUrl}" target="_blank">Ver CV original en PDF</a>
                        </c:if>
                        <form method="post" action="${pageContext.request.contextPath}/company/search">
                            <input type="hidden" name="action" value="unfavorite">
                            <input type="hidden" name="cvId" value="${cv.cvId}">
                            <input type="hidden" name="returnTo" value="/company/favorites">
                            <button class="btn btn-outline-secondary btn-sm" type="submit">Quitar</button>
                        </form>
                    </div>
                    <form class="mt-3" method="post" action="${pageContext.request.contextPath}/company/search">
                        <input type="hidden" name="action" value="contact">
                        <input type="hidden" name="cvId" value="${cv.cvId}">
                        <input type="hidden" name="returnTo" value="/company/favorites">
                        <textarea class="form-control form-control-sm mb-2" name="message" rows="2" maxlength="1000" placeholder="Mensaje opcional"></textarea>
                        <button class="btn btn-brand btn-sm" type="submit">Solicitar contacto</button>
                    </form>
                </div>
            </div>
        </c:forEach>
        <c:if test="${empty favorites}">
            <div class="col-12"><div class="empty-state">Todavía no has guardado perfiles favoritos.</div></div>
        </c:if>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
