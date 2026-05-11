<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<section class="hero-card text-center">
    <span class="eyebrow">500</span>
    <h1 class="display-6 mt-3">Ocurrió un error interno del sistema.</h1>
    <p class="muted">Revisa la configuración de la base de datos, el despliegue del contenedor o el log del servidor.</p>
    <a class="btn btn-brand mt-3" href="${pageContext.request.contextPath}/public/home">Volver al inicio</a>
</section>
<jsp:include page="/WEB-INF/views/common/footer.jsp" />
