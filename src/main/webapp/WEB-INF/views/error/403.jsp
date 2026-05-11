<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<section class="hero-card text-center">
    <span class="eyebrow">403</span>
    <h1 class="display-6 mt-3">No tienes permiso para acceder a este recurso.</h1>
    <a class="btn btn-brand mt-3" href="${pageContext.request.contextPath}/public/home">Volver al inicio</a>
</section>
<jsp:include page="/WEB-INF/views/common/footer.jsp" />
