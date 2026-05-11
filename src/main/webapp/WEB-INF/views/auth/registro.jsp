<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="row justify-content-center">
    <div class="col-xl-8">
        <div class="dashboard-card p-4 p-lg-5">
            <div class="auth-card-header">
                <div>
                    <span class="eyebrow">Alta de usuarios</span>
                    <h1 class="section-title h2 mt-3 mb-0">Crear nueva cuenta</h1>
                </div>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/auth/register" class="row g-3 mt-4">
                <div class="col-md-4">
                    <label class="form-label">Tipo de cuenta</label>
                    <select class="form-select" name="role" data-role-toggle>
                        <option value="graduate" ${selectedRole == 'graduate' || empty selectedRole ? 'selected' : ''}>Egresado</option>
                        <option value="company" ${selectedRole == 'company' ? 'selected' : ''}>Empresa</option>
                    </select>
                </div>
                <div class="col-md-8">
                    <label class="form-label">Correo</label>
                    <input type="email" class="form-control" name="email" value="${email}" autocomplete="email" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Contrase&ntilde;a</label>
                    <input type="password" class="form-control" name="password" autocomplete="new-password" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Confirmaci&oacute;n sugerida</label>
                    <input type="text" class="form-control" value="M&iacute;nimo 8 caracteres, letras y n&uacute;meros" readonly>
                </div>

                <div class="col-12 row g-3" data-graduate-fields>
                    <div class="col-md-6">
                        <label class="form-label">Nombres</label>
                        <input type="text" class="form-control" name="firstName" value="${firstName}" autocomplete="given-name">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Apellidos</label>
                        <input type="text" class="form-control" name="lastName" value="${lastName}" autocomplete="family-name">
                    </div>
                    <div class="col-md-8">
                        <label class="form-label">Carrera</label>
                        <select class="form-select" name="careerId">
                            <option value="">Seleccionar</option>
                            <c:forEach items="${careers}" var="career">
                                <option value="${career.careerId}" ${selectedCareerId == career.careerId ? 'selected' : ''}>${career.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">A&ntilde;o de egreso</label>
                        <input type="number" class="form-control" name="graduationYear" value="${graduationYear}">
                    </div>
                    <div class="col-12">
                        <div class="alert alert-info mb-0">Para egresados se requiere correo institucional.</div>
                    </div>
                </div>

                <div class="col-12 d-flex gap-2">
                    <button class="btn btn-brand" type="submit">Registrar</button>
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/auth/login">Volver al login</a>
                </div>
            </form>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
