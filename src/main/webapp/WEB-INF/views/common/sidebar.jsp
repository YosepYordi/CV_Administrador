<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<aside class="app-sidebar" aria-label="Navegaci&oacute;n de m&oacute;dulos">
    <div class="sidebar-panel">
        <div class="sidebar-title">M&oacute;dulos</div>
        <nav class="sidebar-nav">
            <c:if test="${sessionScope.currentUser.graduate}">
                <a data-nav-link class="sidebar-link" href="${pageContext.request.contextPath}/graduate/dashboard">
                    <i class="fa-solid fa-chart-line" aria-hidden="true"></i><span>Panel</span>
                </a>
                <a data-nav-link class="sidebar-link" href="${pageContext.request.contextPath}/graduate/profile">
                    <i class="fa-regular fa-user" aria-hidden="true"></i><span>Perfil</span>
                </a>
                <a data-nav-link class="sidebar-link" href="${pageContext.request.contextPath}/graduate/cv">
                    <i class="fa-regular fa-file-lines" aria-hidden="true"></i><span>Mi CV</span>
                </a>
                <div class="sidebar-title mt-4">Acciones</div>
                <a class="sidebar-link sidebar-link-muted" href="${pageContext.request.contextPath}/graduate/profile/edit">
                    <i class="fa-solid fa-pen" aria-hidden="true"></i><span>Editar perfil</span>
                </a>
                <a class="sidebar-link sidebar-link-muted" href="${pageContext.request.contextPath}/graduate/cv/edit">
                    <i class="fa-solid fa-layer-group" aria-hidden="true"></i><span>Editar CV</span>
                </a>
                <a class="sidebar-link sidebar-link-muted" href="${pageContext.request.contextPath}/graduate/cv/pdf" target="_blank">
                    <i class="fa-regular fa-file-pdf" aria-hidden="true"></i><span>Generar PDF</span>
                </a>
            </c:if>
            <c:if test="${sessionScope.currentUser.company}">
                <a data-nav-link class="sidebar-link" href="${pageContext.request.contextPath}/company/dashboard">
                    <i class="fa-solid fa-building" aria-hidden="true"></i><span>Panel</span>
                </a>
                <a data-nav-link class="sidebar-link" href="${pageContext.request.contextPath}/company/search">
                    <i class="fa-solid fa-magnifying-glass" aria-hidden="true"></i><span>Buscar talento</span>
                </a>
                <a data-nav-link class="sidebar-link" href="${pageContext.request.contextPath}/company/favorites">
                    <i class="fa-regular fa-star" aria-hidden="true"></i><span>Favoritos</span>
                </a>
                <a data-nav-link class="sidebar-link" href="${pageContext.request.contextPath}/company/account">
                    <i class="fa-solid fa-user-gear" aria-hidden="true"></i><span>Cuenta</span>
                </a>
                <div class="sidebar-title mt-4">Acciones</div>
                <a class="sidebar-link sidebar-link-muted" href="${pageContext.request.contextPath}/company/search">
                    <i class="fa-solid fa-sliders" aria-hidden="true"></i><span>Nueva b&uacute;squeda</span>
                </a>
            </c:if>
            <c:if test="${sessionScope.currentUser.admin}">
                <a data-nav-link class="sidebar-link" href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="fa-solid fa-gauge-high" aria-hidden="true"></i><span>Dashboard</span>
                </a>
                <a data-nav-link class="sidebar-link" href="${pageContext.request.contextPath}/admin/users">
                    <i class="fa-solid fa-users-gear" aria-hidden="true"></i><span>Usuarios</span>
                </a>
                <a data-nav-link class="sidebar-link" href="${pageContext.request.contextPath}/admin/careers">
                    <i class="fa-solid fa-graduation-cap" aria-hidden="true"></i><span>Carreras</span>
                </a>
                <a data-nav-link class="sidebar-link" href="${pageContext.request.contextPath}/admin/reports">
                    <i class="fa-solid fa-chart-pie" aria-hidden="true"></i><span>Reportes</span>
                </a>
            </c:if>
        </nav>
    </div>
</aside>
