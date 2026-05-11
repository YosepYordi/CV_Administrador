<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="hero-card">
    <span class="eyebrow">Proyecto académico</span>
    <h1 class="display-6 mt-3">Arquitectura MVC + DAO para la empleabilidad de egresados.</h1>
    <p class="muted mt-3 mb-0">Esta aplicación fue pensada para ejecutarse con Java Servlet/JSP, Maven y MySQL. Organiza el backend por capas para separar presentación, control, lógica de negocio y persistencia.</p>
</section>

<section class="row g-4 mt-1">
    <div class="col-lg-6">
        <div class="dashboard-card p-4 h-100">
            <h2 class="section-title h4">Objetivo</h2>
            <p class="muted mb-0">Conectar a egresados con empleadores mientras el instituto centraliza perfiles, publicaciones y estadísticas de empleabilidad.</p>
        </div>
    </div>
    <div class="col-lg-6">
        <div class="dashboard-card p-4 h-100">
            <h2 class="section-title h4">Stack</h2>
            <p class="muted mb-0">Tomcat 9, Servlets 4.0, JSP/JSTL, patrón DAO con JDBC, Bootstrap 5, BCrypt y MySQL 8.</p>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
