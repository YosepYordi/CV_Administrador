<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="dashboard-card p-4">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
        <h1 class="section-title h2 mb-0">Gestion de usuarios</h1>
        <form method="get" action="${pageContext.request.contextPath}/admin/users" class="row g-2 align-items-center">
            <div class="col-auto">
                <input class="form-control" name="q" value="${query}" placeholder="Buscar email">
            </div>
            <div class="col-auto">
                <select class="form-select" name="role">
                    <option value="">Todos los roles</option>
                    <option value="graduate">Egresado</option>
                    <option value="company">Empresa</option>
                    <option value="admin">Administrador</option>
                </select>
            </div>
            <div class="col-auto">
                <select class="form-select" name="status">
                    <option value="">Todos los estados</option>
                    <option value="active">Activo</option>
                    <option value="pending">Pendiente</option>
                    <option value="inactive">Inactivo</option>
                </select>
            </div>
            <div class="col-auto">
                <button class="btn btn-outline-primary" type="submit">Filtrar</button>
            </div>
            <div class="col-auto">
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/users">Limpiar</a>
            </div>
        </form>
    </div>
    <div class="table-responsive">
        <table class="table align-middle">
            <thead>
                <tr>
                    <th>Email</th>
                    <th>Rol</th>
                    <th>Estado</th>
                    <th>Accion</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${users}" var="user">
                    <tr>
                        <td>${user.email}</td>
                        <td>${user.role.label}</td>
                        <td>${user.status.label}</td>
                        <td>
                            <form class="d-flex flex-wrap gap-2" method="post" action="${pageContext.request.contextPath}/admin/users">
                                <input type="hidden" name="userId" value="${user.userId}">
                                <button class="btn btn-outline-success btn-sm" name="status" value="active" type="submit">Activo</button>
                                <button class="btn btn-outline-warning btn-sm" name="status" value="pending" type="submit">Pendiente</button>
                                <button class="btn btn-outline-secondary btn-sm" name="status" value="inactive" type="submit">Inactivo</button>
                            </form>
                            <c:if test="${user.userId ne sessionScope.currentUser.userId}">
                                <form class="d-flex flex-wrap gap-2 mt-2" method="post" action="${pageContext.request.contextPath}/admin/users">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="userId" value="${user.userId}">
                                    <input class="form-control form-control-sm" name="confirmDelete" placeholder="ELIMINAR USUARIO" pattern="ELIMINAR USUARIO" required>
                                    <button class="btn btn-outline-danger btn-sm" type="submit">Eliminar total</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty users}">
                    <tr>
                        <td colspan="4" class="text-center muted py-4">No hay usuarios con los filtros seleccionados.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
