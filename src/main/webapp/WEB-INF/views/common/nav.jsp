<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<nav class="navbar navbar-expand-lg sticky-top app-topbar" aria-label="Navegaci&oacute;n principal">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/public/home">
            <span class="brand-mark"><i class="fa-regular fa-id-card" aria-hidden="true"></i></span>
            <span>CV Manager</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav"
                aria-controls="mainNav" aria-expanded="false" aria-label="Abrir navegaci&oacute;n">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav ms-auto align-items-lg-center gap-lg-2">
                <c:choose>
                    <c:when test="${not empty sessionScope.currentUser}">
                        <li class="nav-item">
                            <span class="nav-system-chip">
                                <c:choose>
                                    <c:when test="${sessionScope.currentUser.admin}">Administraci&oacute;n</c:when>
                                    <c:when test="${sessionScope.currentUser.company}">Empresa</c:when>
                                    <c:otherwise>Egresado</c:otherwise>
                                </c:choose>
                            </span>
                        </li>
                        <li class="nav-item">
                            <span class="nav-user">${sessionScope.currentUser.email}</span>
                        </li>
                        <li class="nav-item ms-lg-2">
                            <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/auth/logout">
                                <i class="fa-solid fa-arrow-right-from-bracket me-1" aria-hidden="true"></i>
                                Salir
                            </a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/public/home">Inicio</a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/public/about">Acerca</a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/public/contact">Contacto</a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/auth/login">Ingresar</a></li>
                        <li class="nav-item ms-lg-2"><a class="btn btn-brand btn-sm" href="${pageContext.request.contextPath}/auth/register">Crear cuenta</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>
