<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="row g-4">
    <div class="col-lg-7">
        <div class="dashboard-card p-4 h-100">
            <span class="eyebrow">Mesa de ayuda</span>
            <h1 class="section-title h2 mt-3">Soporte del sistema CV Manager</h1>
            <p class="muted">Utiliza este canal para incidencias técnicas, activación de cuentas o actualización de catálogos institucionales.</p>
            <div class="row g-3 mt-2">
                <div class="col-md-6">
                    <div class="metric-card">
                        <div class="muted small">Correo</div>
                        <strong>bolsa.laboral@instituto.edu.pe</strong>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="metric-card">
                        <div class="muted small">Teléfono</div>
                        <strong>(01) 555-0123</strong>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="col-lg-5">
        <div class="dashboard-card p-4 h-100">
            <h2 class="section-title h4">Horario de atención</h2>
            <p class="muted mb-2">Lunes a viernes de 8:00 a.m. a 6:00 p.m.</p>
            <h2 class="section-title h4 mt-4">Cobertura</h2>
            <p class="muted mb-0">Registro, recuperación de contraseña, publicación de CV y gestión del panel administrativo.</p>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
