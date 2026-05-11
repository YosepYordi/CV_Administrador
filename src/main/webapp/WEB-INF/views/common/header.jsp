<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es" data-theme="dark" data-bs-theme="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${empty pageTitle ? 'CV Manager' : pageTitle}</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/images/logo.png">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/admin.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/cv-Plantillas.css" rel="stylesheet">
</head>
<body class="app-body ${not empty sessionScope.currentUser ? 'has-sidebar' : 'public-body'}">
    <jsp:include page="/WEB-INF/views/common/nav.jsp" />
    <div class="app-shell">
        <c:if test="${not empty sessionScope.currentUser}">
            <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
        </c:if>
        <main class="app-main">
        <c:if test="${not empty requestScope.flashSuccess}">
            <div class="alert alert-success shadow-sm auto-dismiss">${requestScope.flashSuccess}</div>
        </c:if>
        <c:if test="${not empty requestScope.flashError}">
            <div class="alert alert-danger shadow-sm auto-dismiss">${requestScope.flashError}</div>
        </c:if>
        <c:if test="${not empty requestScope.databaseWarning}">
            <div class="alert alert-warning shadow-sm">${requestScope.databaseWarning}</div>
        </c:if>
        <c:if test="${not empty requestScope.formError}">
            <div class="alert alert-danger shadow-sm">${requestScope.formError}</div>
        </c:if>
