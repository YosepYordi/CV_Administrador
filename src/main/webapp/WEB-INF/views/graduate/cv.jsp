<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<c:set var="defaultAvatar" value="${pageContext.request.contextPath}/assets/images/default-avatar.png" />
<c:set var="profilePhoto" value="${empty cv.graduatePhotoUrl ? graduate.photoUrl : cv.graduatePhotoUrl}" />

<section class="cv-card p-4 p-lg-5">
    <div class="cv-header mb-4">
        <img class="cv-avatar" src="${empty profilePhoto ? defaultAvatar : profilePhoto}" alt="Foto de perfil">
        <div>
            <h1 class="section-title h2 mb-1">${empty cv.graduateName ? graduate.fullName : cv.graduateName}</h1>
            <div class="muted">${empty cv.careerName ? graduate.careerName : cv.careerName} &middot; ${empty cv.city ? graduate.city : cv.city}</div>
            <p class="mt-3 mb-0">${cv.title}</p>
        </div>
        <div class="d-flex flex-wrap gap-2 justify-content-end">
            <c:choose>
                <c:when test="${readonly}">
                    <c:choose>
                        <c:when test="${not empty cv.cvPdfUrl}">
                            <a class="btn btn-brand btn-sm" href="${cv.cvPdfUrl}" target="_blank">Ver CV original en PDF</a>
                        </c:when>
                        <c:otherwise>
                            <span class="tag-chip">CV original no adjuntado</span>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${sessionScope.currentUser.company}">
                        <c:choose>
                            <c:when test="${favoriteIds.contains(cv.cvId)}">
                                <form method="post" action="${pageContext.request.contextPath}/company/search">
                                    <input type="hidden" name="action" value="unfavorite">
                                    <input type="hidden" name="cvId" value="${cv.cvId}">
                                    <input type="hidden" name="returnTo" value="${returnTo}">
                                    <button class="btn btn-outline-secondary btn-sm" type="submit">Quitar favorito</button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <form method="post" action="${pageContext.request.contextPath}/company/search">
                                    <input type="hidden" name="action" value="favorite">
                                    <input type="hidden" name="cvId" value="${cv.cvId}">
                                    <input type="hidden" name="returnTo" value="${returnTo}">
                                    <button class="btn btn-brand btn-sm" type="submit">Guardar favorito</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                        <form method="post" action="${pageContext.request.contextPath}/company/search">
                            <input type="hidden" name="action" value="contact">
                            <input type="hidden" name="cvId" value="${cv.cvId}">
                            <input type="hidden" name="returnTo" value="${returnTo}">
                            <button class="btn btn-outline-primary btn-sm" type="submit">Solicitar contacto</button>
                        </form>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/graduate/cv/edit">Editar</a>
                    <c:if test="${not empty cv.cvPdfUrl}">
                        <a class="btn btn-brand btn-sm" href="${cv.cvPdfUrl}" target="_blank">Ver CV original en PDF</a>
                    </c:if>
                    <form method="post" action="${pageContext.request.contextPath}/graduate/cv" class="d-flex gap-2">
                        <input type="hidden" name="action" value="deleteCv">
                        <input class="form-control form-control-sm" name="confirmDeleteCv" placeholder="ELIMINAR CV" pattern="ELIMINAR CV" required>
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
        <c:set var="hasEducation" value="false" />
        <c:forEach items="${cv.educationList}" var="item">
            <c:if test="${not empty item.fieldOfStudy or not empty item.startDate or not empty item.endDate or not empty item.description}">
                <c:set var="hasEducation" value="true" />
                <div class="mb-3">
                    <c:if test="${not empty item.degree or not empty item.institution}">
                        <strong>${item.degree}</strong><c:if test="${not empty item.degree and not empty item.institution}"> - </c:if>${item.institution}<br>
                    </c:if>
                    <c:if test="${not empty item.fieldOfStudy or not empty item.startDate or not empty item.endDate}">
                        <span class="muted">
                            <c:if test="${not empty item.fieldOfStudy}">${item.fieldOfStudy}</c:if>
                            <c:if test="${not empty item.fieldOfStudy and (not empty item.startDate or not empty item.endDate)}"> &middot; </c:if>
                            <c:if test="${not empty item.startDate}">${item.startDate}</c:if>
                            <c:if test="${not empty item.startDate and not empty item.endDate}"> / </c:if>
                            <c:if test="${not empty item.endDate}">${item.endDate}</c:if>
                        </span>
                    </c:if>
                    <c:if test="${not empty item.description}">
                        <div>${item.description}</div>
                    </c:if>
                </div>
            </c:if>
        </c:forEach>
        <c:if test="${not hasEducation}">
            <div class="empty-state">Aun no se registro formacion academica.</div>
        </c:if>
    </div>

    <div class="cv-section">
        <h2 class="section-title h4">Experiencia</h2>
        <c:set var="hasExperience" value="false" />
        <c:forEach items="${cv.experienceList}" var="item">
            <c:if test="${not empty item.posicion}">
                <c:set var="hasExperience" value="true" />
                <div class="mb-3">
                    <strong>${item.posicion}</strong><c:if test="${not empty item.empresaNombre}"> - ${item.empresaNombre}</c:if><br>
                    <c:if test="${not empty item.startDate or not empty item.endDate or not empty item.employmentType}">
                        <span class="muted">
                            <c:if test="${not empty item.startDate}">${item.startDate}</c:if>
                            <c:if test="${not empty item.startDate and not empty item.endDate}"> / </c:if>
                            <c:if test="${not empty item.endDate}">${item.endDate}</c:if>
                            <c:if test="${(not empty item.startDate or not empty item.endDate) and not empty item.employmentType}"> &middot; </c:if>
                            <c:if test="${not empty item.employmentType}">${item.employmentType}</c:if>
                        </span>
                    </c:if>
                    <c:if test="${not empty item.responsibilities}">
                        <div>${item.responsibilities}</div>
                    </c:if>
                    <c:if test="${not empty item.achievements}">
                        <div class="muted">${item.achievements}</div>
                    </c:if>
                </div>
            </c:if>
        </c:forEach>
        <c:if test="${not hasExperience}">
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
                <c:set var="hasLanguages" value="false" />
                <c:forEach items="${cv.languages}" var="language">
                    <c:if test="${not empty language.languageName}">
                        <c:set var="hasLanguages" value="true" />
                        <div class="metric-card cv-list-card mb-2">
                            <strong>${language.languageName}</strong><br>
                            <span class="muted">${language.proficiencyLevel.value}</span>
                        </div>
                    </c:if>
                </c:forEach>
                <c:if test="${not hasLanguages}">
                    <div class="empty-state">Aun no se registraron idiomas.</div>
                </c:if>
            </div>
            <div class="col-lg-6">
                <c:set var="hasCertifications" value="false" />
                <c:forEach items="${cv.certifications}" var="certification">
                    <c:if test="${not empty certification.name and (not empty certification.issuingOrganization or not empty certification.issueDate or not empty certification.credentialId or not empty certification.credentialUrl)}">
                        <c:set var="hasCertifications" value="true" />
                        <div class="metric-card cv-list-card mb-2">
                            <strong>${certification.name}</strong><br>
                            <c:if test="${not empty certification.issuingOrganization}">
                                <span class="muted">${certification.issuingOrganization}</span><br>
                            </c:if>
                            <c:if test="${not empty certification.issueDate or not empty certification.credentialId}">
                                <span class="muted">
                                    <c:if test="${not empty certification.issueDate}">${certification.issueDate}</c:if>
                                    <c:if test="${not empty certification.issueDate and not empty certification.credentialId}"> &middot; </c:if>
                                    <c:if test="${not empty certification.credentialId}">${certification.credentialId}</c:if>
                                </span>
                            </c:if>
                        </div>
                    </c:if>
                </c:forEach>
                <c:if test="${not hasCertifications}">
                    <div class="empty-state">Aun no se registraron certificaciones.</div>
                </c:if>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
