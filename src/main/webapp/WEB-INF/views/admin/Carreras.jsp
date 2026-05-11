<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="row g-4">
    <div class="col-lg-5">
        <div class="dashboard-card p-4">
            <h1 class="section-title h3 mb-4">Registrar carrera</h1>
            <form method="post" action="${pageContext.request.contextPath}/admin/careers" class="d-grid gap-3">
                <input class="form-control" name="name" placeholder="Nombre de la carrera" required>
                <input class="form-control" name="code" placeholder="Codigo">
                <textarea class="form-control" name="description" placeholder="Descripcion"></textarea>
                <input type="number" class="form-control" name="durationYears" placeholder="Duracion en anios">
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" name="active" checked>
                    <label class="form-check-label">Activa</label>
                </div>
                <button class="btn btn-brand" type="submit">Guardar carrera</button>
            </form>
        </div>
    </div>
    <div class="col-lg-7">
        <div class="dashboard-card p-4 h-100">
            <h2 class="section-title h4 mb-3">Carreras registradas</h2>
            <div class="table-responsive">
                <table class="table">
                    <thead><tr><th>Nombre</th><th>Codigo</th><th>Duracion</th><th>Activa</th><th>Accion</th></tr></thead>
                    <tbody>
                    <c:forEach items="${careers}" var="career">
                        <tr>
                            <td>${career.name}</td>
                            <td>${career.code}</td>
                            <td>${career.durationYears}</td>
                            <td>${career.active ? 'Si' : 'No'}</td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/admin/careers">
                                    <input type="hidden" name="action" value="toggle">
                                    <input type="hidden" name="careerId" value="${career.careerId}">
                                    <input type="hidden" name="active" value="${!career.active}">
                                    <button class="btn btn-outline-primary btn-sm" type="submit">
                                        ${career.active ? 'Desactivar' : 'Activar'}
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty careers}">
                        <tr>
                            <td colspan="5" class="text-center muted py-4">No hay carreras registradas.</td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
