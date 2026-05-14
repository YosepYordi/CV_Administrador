<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="dashboard-card p-4">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
        <div>
            <h1 class="section-title h2 mb-1">Empresas registradas</h1>
            <p class="muted mb-0">Explora empresas y solicita contacto cuando te interese una oportunidad.</p>
        </div>
    </div>

    <div class="row g-3">
        <c:forEach items="${companies}" var="company">
            <div class="col-md-6 col-xl-4">
                <div class="metric-card cv-list-card h-100">
                    <h2 class="h5 mb-1">${empty company.companyName ? 'Empresa sin nombre' : company.companyName}</h2>
                    <div class="muted">${company.industry}</div>
                    <c:if test="${not empty company.website}">
                        <a class="btn btn-outline-secondary btn-sm mt-3" href="${company.website}" target="_blank">Sitio web</a>
                    </c:if>
                    <c:if test="${not empty company.description}">
                        <p class="muted mt-3 mb-0">${company.description}</p>
                    </c:if>
                    <form class="mt-3" method="post" action="${pageContext.request.contextPath}/graduate/companies">
                        <input type="hidden" name="action" value="requestCompanyContact">
                        <input type="hidden" name="companyId" value="${company.companyId}">
                        <textarea class="form-control form-control-sm mb-2" name="message" rows="2" maxlength="1000" placeholder="Mensaje opcional"></textarea>
                        <button class="btn btn-brand btn-sm" type="submit">Solicitar contacto</button>
                    </form>
                </div>
            </div>
        </c:forEach>

        <c:if test="${empty companies}">
            <div class="col-12">
                <div class="empty-state">Aun no hay empresas registradas.</div>
            </div>
        </c:if>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
