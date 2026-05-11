<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="dashboard-card p-4 p-lg-5">
    <h1 class="section-title h2 mb-4">Editar perfil</h1>
    <form method="post" enctype="multipart/form-data" action="${pageContext.request.contextPath}/graduate/profile/edit" class="row g-3">
        <input type="hidden" name="action" value="updateProfile">
        <div class="col-md-6">
            <label class="form-label">Nombres</label>
            <input class="form-control" name="firstName" value="${graduate.firstName}">
        </div>
        <div class="col-md-6">
            <label class="form-label">Apellidos</label>
            <input class="form-control" name="lastName" value="${graduate.lastName}">
        </div>
        <div class="col-md-3">
            <label class="form-label">Tipo doc.</label>
            <input class="form-control" name="documentType" value="${graduate.documentType}">
        </div>
        <div class="col-md-3">
            <label class="form-label">Nro doc.</label>
            <input class="form-control" name="documentNumber" value="${graduate.documentNumber}">
        </div>
        <div class="col-md-3">
            <label class="form-label">Teléfono</label>
            <input class="form-control" name="phone" value="${graduate.phone}">
        </div>
        <div class="col-md-3">
            <label class="form-label">Nacimiento</label>
            <input type="date" class="form-control" name="birthDate" value="${graduate.birthDate}">
        </div>
        <div class="col-md-6">
            <label class="form-label">Ciudad</label>
            <input class="form-control" name="city" value="${graduate.city}">
        </div>
        <div class="col-md-6">
            <label class="form-label">País</label>
            <input class="form-control" name="country" value="${graduate.country}">
        </div>
        <div class="col-12">
            <label class="form-label">Dirección</label>
            <input class="form-control" name="address" value="${graduate.address}">
        </div>
        <div class="col-md-6">
            <label class="form-label">LinkedIn</label>
            <input class="form-control" name="linkedinUrl" value="${graduate.linkedinUrl}">
        </div>
        <div class="col-md-6">
            <label class="form-label">Portafolio</label>
            <input class="form-control" name="portfolioUrl" value="${graduate.portfolioUrl}">
        </div>
        <div class="col-md-6">
            <label class="form-label">Carrera</label>
            <select class="form-select" name="careerId">
                <option value="">Seleccionar</option>
                <c:forEach items="${careers}" var="career">
                    <option value="${career.careerId}" ${graduate.careerId == career.careerId ? 'selected' : ''}>${career.name}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-3">
            <label class="form-label">Año de egreso</label>
            <input type="number" class="form-control" name="graduationYear" value="${graduate.graduationYear}">
        </div>
        <div class="col-md-3">
            <label class="form-label">Salario esperado</label>
            <input type="number" step="0.01" class="form-control" name="expectedSalary" value="${graduate.expectedSalary}">
        </div>
        <div class="col-md-6">
            <label class="form-label">Disponibilidad</label>
            <input class="form-control" name="availability" value="${graduate.availability}">
        </div>
        <div class="col-md-6">
            <label class="form-label">Foto de perfil</label>
            <input type="file" class="form-control" name="photo" accept="image/*">
        </div>
        <div class="col-12 form-check ms-2">
            <input class="form-check-input" type="checkbox" name="isPublic" ${graduate.public ? 'checked' : ''}>
            <label class="form-check-label">Hacer perfil visible a empresas</label>
        </div>
        <div class="col-12 d-flex gap-2">
            <button class="btn btn-brand" type="submit">Guardar cambios</button>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/graduate/profile">Cancelar</a>
        </div>
    </form>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
