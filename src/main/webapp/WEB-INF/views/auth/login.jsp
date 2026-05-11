<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="row justify-content-center">
    <div class="col-lg-5">
        <div class="dashboard-card p-4 p-lg-5">
            <span class="eyebrow">Acceso seguro</span>
            <h1 class="section-title h2 mt-3 mb-4">Inicia sesi&oacute;n</h1>
            <form method="post" action="${pageContext.request.contextPath}/auth/login" class="d-grid gap-3">
                <div>
                    <label class="form-label">Correo electr&oacute;nico</label>
                    <input type="email" class="form-control" name="email" value="${email}" required>
                </div>
                <div>
                    <label class="form-label">Contrase&ntilde;a</label>
                    <input type="password" class="form-control" name="password" required>
                </div>
                <button type="submit" class="btn btn-brand">Ingresar</button>
            </form>
            <div class="d-flex justify-content-between mt-4 small">
                <a href="${pageContext.request.contextPath}/auth/forgot-password">Olvid&eacute; mi contrase&ntilde;a</a>
                <a href="${pageContext.request.contextPath}/auth/register">Crear cuenta</a>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
