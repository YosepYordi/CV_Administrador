<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="dashboard-card p-4">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
        <div>
            <h1 class="section-title h2 mb-1">Solicitudes de contacto</h1>
            <p class="muted mb-0">Empresas interesadas en contactar contigo.</p>
        </div>
    </div>

    <div class="row g-3">
        <c:forEach items="${requests}" var="request">
            <div class="col-12">
                <div class="metric-card cv-list-card">
                    <div class="d-flex flex-wrap justify-content-between align-items-start gap-3">
                        <div>
                            <h2 class="h5 mb-1">${empty request.companyName ? 'Empresa sin nombre' : request.companyName}</h2>
                            <div class="muted">${request.companyEmail}</div>
                            <div class="muted small mt-1">${request.createdAt}</div>
                        </div>
                        <span class="tag-chip">${request.status.label}</span>
                    </div>

                    <c:choose>
                        <c:when test="${empty request.message}">
                            <p class="muted mt-3 mb-0">La empresa no dejó mensaje adicional.</p>
                        </c:when>
                        <c:otherwise>
                            <p class="mt-3 mb-0">${request.message}</p>
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${request.status.value == 'pending'}">
                        <div class="d-flex flex-wrap gap-2 mt-3">
                            <form method="post" action="${pageContext.request.contextPath}/graduate/requests">
                                <input type="hidden" name="action" value="acceptRequest">
                                <input type="hidden" name="requestId" value="${request.requestId}">
                                <button class="btn btn-brand btn-sm" type="submit">Aceptar</button>
                            </form>
                            <form method="post" action="${pageContext.request.contextPath}/graduate/requests">
                                <input type="hidden" name="action" value="rejectRequest">
                                <input type="hidden" name="requestId" value="${request.requestId}">
                                <button class="btn btn-outline-secondary btn-sm" type="submit">Rechazar</button>
                            </form>
                        </div>
                    </c:if>
                </div>
            </div>
        </c:forEach>

        <c:if test="${empty requests}">
            <div class="col-12">
                <div class="empty-state">Aun no tienes solicitudes de contacto.</div>
            </div>
        </c:if>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
