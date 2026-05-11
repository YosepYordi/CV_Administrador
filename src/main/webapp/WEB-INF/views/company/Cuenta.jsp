<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="row g-4">
    <div class="col-lg-7">
        <div class="dashboard-card p-4 h-100">
            <h1 class="section-title h3 mb-3">Cuenta de empresa</h1>
            <div class="row g-3">
                <div class="col-md-6"><strong>Empresa:</strong><br>${empty company.companyName ? sessionScope.currentUser.email : company.companyName}</div>
                <div class="col-md-6"><strong>Correo:</strong><br>${sessionScope.currentUser.email}</div>
                <div class="col-md-6"><strong>RUC:</strong><br>${company.ruc}</div>
                <div class="col-md-6"><strong>Industria:</strong><br>${company.industry}</div>
                <div class="col-md-6"><strong>Tama&ntilde;o:</strong><br>${company.companySize}</div>
                <div class="col-md-6"><strong>Sitio web:</strong><br>${company.website}</div>
                <div class="col-12"><strong>Descripci&oacute;n:</strong><br>${company.description}</div>
            </div>
        </div>
    </div>
    <div class="col-lg-5">
        <div class="dashboard-card p-4 h-100">
            <h2 class="section-title h4 mb-3">Eliminar cuenta</h2>
            <form method="post" action="${pageContext.request.contextPath}/company/account" class="row g-3">
                <input type="hidden" name="action" value="deleteAccount">
                <p class="muted mb-0">Elimina el usuario de empresa, favoritos, solicitudes de contacto, tokens y datos asociados.</p>
                <div class="col-12">
                    <label class="form-label">Escribe el correo actual para confirmar</label>
                    <input class="form-control" name="confirmEmail" autocomplete="off" required>
                </div>
                <div class="col-12">
                    <button class="btn btn-outline-danger" type="submit">Eliminar cuenta completamente</button>
                </div>
            </form>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
