<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="defaultAvatar" value="${pageContext.request.contextPath}/assets/images/default-avatar.png" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="row g-4">
    <div class="col-lg-4">
        <div class="dashboard-card p-4 text-center h-100">
            <img class="cv-avatar mb-3" src="${empty graduate.photoUrl ? defaultAvatar : graduate.photoUrl}" alt="Foto de perfil">
            <h1 class="h3 mb-1">${graduate.fullName}</h1>
            <div class="muted">${graduate.careerName}</div>
            <div class="tag-list justify-content-center mt-3">
                <span class="tag-chip">${graduate.city}</span>
                <span class="tag-chip">${graduate.country}</span>
                <span class="tag-chip">${graduate.graduationYear}</span>
            </div>
        </div>
    </div>
    <div class="col-lg-8">
        <div class="dashboard-card p-4 h-100">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2 class="section-title h4 mb-0">Datos personales</h2>
                <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/graduate/profile/edit">Editar</a>
            </div>
            <div class="row g-3">
                <div class="col-md-6"><strong>Documento:</strong><br>${graduate.documentType} ${graduate.documentNumber}</div>
                <div class="col-md-6"><strong>Teléfono:</strong><br>${graduate.phone}</div>
                <div class="col-md-6"><strong>LinkedIn:</strong><br>${graduate.linkedinUrl}</div>
                <div class="col-md-6"><strong>Portafolio:</strong><br>${graduate.portfolioUrl}</div>
                <div class="col-12"><strong>Dirección:</strong><br>${graduate.address}</div>
                <div class="col-md-6"><strong>Carrera:</strong><br>${graduate.careerName}</div>
                <div class="col-md-6"><strong>A&ntilde;o de egreso:</strong><br>${graduate.graduationYear}</div>
                <div class="col-md-6"><strong>Salario esperado:</strong><br>${graduate.expectedSalary}</div>
                <div class="col-md-6"><strong>Visibilidad:</strong><br>${graduate.public ? 'Perfil visible para empresas' : 'Perfil privado'}</div>
                <div class="col-md-6"><strong>Disponibilidad:</strong><br>${graduate.availability}</div>
            </div>
        </div>
    </div>
</section>

<section class="row g-4 mt-1">
    <div class="col-lg-6">
        <div class="dashboard-card p-4 h-100">
            <h2 class="section-title h4 mb-3">Seguridad</h2>
            <form method="post" action="${pageContext.request.contextPath}/graduate/profile" class="row g-3">
                <input type="hidden" name="action" value="changePassword">
                <div class="col-12">
                    <label class="form-label">Contrasena actual</label>
                    <input type="password" class="form-control" name="currentPassword" autocomplete="current-password" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Nueva contrasena</label>
                    <input type="password" class="form-control" name="newPassword" autocomplete="new-password" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Confirmar contrasena</label>
                    <input type="password" class="form-control" name="confirmPassword" autocomplete="new-password" required>
                </div>
                <div class="col-12">
                    <button class="btn btn-outline-primary" type="submit">Cambiar contrasena</button>
                </div>
            </form>
        </div>
    </div>
    <div class="col-lg-6">
        <div class="dashboard-card p-4 h-100">
            <h2 class="section-title h4 mb-3">Eliminar cuenta</h2>
            <form method="post" action="${pageContext.request.contextPath}/graduate/profile" class="row g-3">
                <input type="hidden" name="action" value="deleteAccount">
                <p class="muted mb-0">Elimina tu usuario, perfil, CV, favoritos de empresas, solicitudes asociadas y tokens de recuperaci&oacute;n.</p>
                <div class="col-12">
                    <label class="form-label">Escribe tu correo actual para confirmar</label>
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
