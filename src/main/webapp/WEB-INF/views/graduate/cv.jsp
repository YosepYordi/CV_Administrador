<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="cv-card p-4 p-lg-5">
    <div class="cv-header mb-4">
        <div>
            <h1 class="section-title h2 mb-1">${empty cv.graduateName ? graduate.fullName : cv.graduateName}</h1>
            <div class="muted">${empty cv.careerName ? graduate.careerName : cv.careerName} &middot; ${empty cv.city ? graduate.city : cv.city}</div>
            <p class="mt-3 mb-0">${cv.title}</p>
        </div>
        <div class="d-flex flex-wrap gap-2 justify-content-end">
            <c:choose>
                <c:when test="${readonly}">
                    <a class="btn btn-brand btn-sm" href="${pageContext.request.contextPath}/cv/pdf?id=${cv.cvId}" target="_blank">PDF</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/graduate/cv/edit">Editar</a>
                    <a class="btn btn-brand btn-sm" href="${pageContext.request.contextPath}/graduate/cv/pdf" target="_blank">Generar PDF</a>
                    <c:if test="${not empty cv.cvPdfUrl}">
                        <a class="btn btn-outline-secondary btn-sm" href="${cv.cvPdfUrl}" target="_blank">PDF adjunto</a>
                    </c:if>
                    <form method="post" action="${pageContext.request.contextPath}/graduate/cv" class="d-flex gap-2">
                        <input type="hidden" name="action" value="deleteCv">
                        <input class="form-control form-control-sm" name="confirmDeleteCv" placeholder="DELETE CV" pattern="DELETE CV" required>
                        <button class="btn btn-outline-danger btn-sm" type="submit">Eliminar CV</button>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="cv-section">
        <h2 class="section-title h4">Resumen profesional</h2>
        <p class="muted mb-0">${cv.professionalSummary}</p>
    </div>

    <div class="cv-section">
        <h2 class="section-title h4">Educacion</h2>
        <c:forEach items="${cv.educationList}" var="item">
            <div class="mb-3">
                <strong>${item.degree}</strong> - ${item.institution}<br>
                <span class="muted">${item.fieldOfStudy} &middot; ${item.startDate} / ${item.endDate}</span>
                <div>${item.description}</div>
            </div>
        </c:forEach>
        <c:if test="${empty cv.educationList}">
            <div class="empty-state">Aun no se registro formacion academica.</div>
        </c:if>
    </div>

    <div class="cv-section">
        <h2 class="section-title h4">Experiencia</h2>
        <c:forEach items="${cv.experienceList}" var="item">
            <div class="mb-3">
                <strong>${item.posicion}</strong> - ${item.empresaNombre}<br>
                <span class="muted">${item.startDate} / ${item.endDate} &middot; ${item.employmentType}</span>
                <div>${item.responsibilities}</div>
                <div class="muted">${item.achievements}</div>
            </div>
        </c:forEach>
        <c:if test="${empty cv.experienceList}">
            <div class="empty-state">Aun no se registro experiencia laboral.</div>
        </c:if>
    </div>

    <div class="cv-section">
        <h2 class="section-title h4">Habilidades</h2>
        <div class="tag-list">
            <c:forEach items="${cv.skills}" var="skill">
                <span class="tag-chip">${skill.habilidadName} (${skill.preferenciaLevel}/5)</span>
            </c:forEach>
        </div>
        <c:if test="${empty cv.skills}">
            <div class="empty-state mt-3">No hay habilidades cargadas.</div>
        </c:if>
    </div>

    <div class="cv-section">
        <h2 class="section-title h4">Idiomas y certificaciones</h2>
        <div class="row g-3">
            <div class="col-lg-6">
                <c:forEach items="${cv.languages}" var="language">
                    <div class="metric-card mb-2">
                        <strong>${language.languageName}</strong><br>
                        <span class="muted">${language.proficiencyLevel.value}</span>
                    </div>
                </c:forEach>
            </div>
            <div class="col-lg-6">
                <c:forEach items="${cv.certifications}" var="certification">
                    <div class="metric-card mb-2">
                        <strong>${certification.name}</strong><br>
                        <span class="muted">${certification.issuingOrganization}</span>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
