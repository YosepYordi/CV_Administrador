<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="row g-4">
    <div class="col-12">
        <div class="hero-card">
            <span class="eyebrow">Bienvenido</span>
            <h1 class="display-6 mt-3">${graduate.fullName}</h1>
            <p class="muted mb-4">${empty cv.title ? 'Aun no has definido un titular profesional.' : cv.title}</p>
            <div class="row g-3">
                <div class="col-md-4">
                    <div class="metric-card">
                        <div class="muted small">CV publicado</div>
                        <div class="metric-value">${cv.published ? 'Si' : 'No'}</div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="metric-card">
                        <div class="muted small">Visualizaciones</div>
                        <div class="metric-value">${cv.viewsCount}</div>
                    </div>
                </div>
                <div class="col-md-4">
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
            <p class="muted mb-0">${cv.professionalSummary}</p>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
