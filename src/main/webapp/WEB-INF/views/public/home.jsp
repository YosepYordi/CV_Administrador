<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="home-hero" aria-label="Inicio CV Manager">
    <div class="home-hero-image" aria-hidden="true"></div>
    <div class="home-hero-overlay" aria-hidden="true"></div>

    <div class="home-hero-content">
        <div class="home-hero-copy">
            <h1>Talento egresado listo para empresas que deciden con datos.</h1>
            <p>CV Manager conecta egresados, empresas e instituto en una experiencia profesional: perfiles claros, b&uacute;squeda por habilidades y control administrativo centralizado.</p>
            <div class="home-actions">
                <a class="btn btn-brand" href="${pageContext.request.contextPath}/auth/register">Crear cuenta</a>
                <a class="btn btn-outline-secondary" href="#talento-publicado">Ver CV recientes</a>
            </div>
        </div>

        <div class="home-command-panel" aria-label="Indicadores principales">
            <div class="home-panel-header">
                <span>Vista institucional</span>
                <strong>Operaci&oacute;n activa</strong>
            </div>
            <div class="home-stat-grid">
                <div class="metric-card">
                    <div class="muted small">CV recientes</div>
                    <div class="metric-value">${empty recentCvs ? 0 : recentCvs.size()}</div>
                </div>
                <div class="metric-card">
                    <div class="muted small">Carreras activas</div>
                    <div class="metric-value">${empty careers ? 0 : careers.size()}</div>
                </div>
            </div>
            <div class="home-panel-note">
                Gestiona perfiles, valida cuentas, mide b&uacute;squeda de talento y publica reportes desde un solo panel.
            </div>
        </div>
    </div>
</section>

<section class="home-module-strip row g-4 mb-4">
    <div class="col-lg-4">
        <div class="info-card p-4 h-100">
            <div class="module-icon"><i class="fa-regular fa-user" aria-hidden="true"></i></div>
            <h2 class="section-title h4">Egresados</h2>
            <p class="muted mb-0">Construyen su perfil profesional, publican su CV y controlan qu&eacute; informaci&oacute;n queda visible.</p>
        </div>
    </div>
    <div class="col-lg-4">
        <div class="info-card p-4 h-100">
            <div class="module-icon"><i class="fa-solid fa-building" aria-hidden="true"></i></div>
            <h2 class="section-title h4">Empresas</h2>
            <p class="muted mb-0">Exploran talento por carrera, habilidades, ubicaci&oacute;n e idiomas, con favoritos y solicitudes de contacto.</p>
        </div>
    </div>
    <div class="col-lg-4">
        <div class="info-card p-4 h-100">
            <div class="module-icon"><i class="fa-solid fa-chart-pie" aria-hidden="true"></i></div>
            <h2 class="section-title h4">Administraci&oacute;n</h2>
            <p class="muted mb-0">Supervisa usuarios, carreras, CV publicados, reportes y actividad del sistema.</p>
        </div>
    </div>
</section>

<section id="talento-publicado" class="home-talent-section">
    <div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-3">
        <div>
            <span class="eyebrow">Talento publicado</span>
            <h2 class="section-title h3 mb-0">CV recientes</h2>
        </div>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/auth/register">Acceder a b&uacute;squeda avanzada</a>
    </div>
    <div class="row g-4">
        <c:forEach items="${recentCvs}" var="cv">
            <div class="col-md-6 col-xl-4">
                <div class="cv-card p-4 h-100">
                    <div class="d-flex justify-content-between align-items-start mb-3">
                        <div>
                            <h3 class="h5 mb-1">${cv.graduateName}</h3>
                            <div class="muted small">${cv.careerName} &middot; ${cv.city}</div>
                        </div>
                        <span class="tag-chip">${cv.yearsOfExperience} a&ntilde;os</span>
                    </div>
                    <p class="muted">${cv.title}</p>
                    <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/cv/view?id=${cv.cvId}">Ver perfil</a>
                </div>
            </div>
        </c:forEach>
        <c:if test="${empty recentCvs}">
            <div class="col-12"><div class="empty-state">A&uacute;n no hay CV publicados o la base de datos no tiene registros cargados.</div></div>
        </c:if>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
