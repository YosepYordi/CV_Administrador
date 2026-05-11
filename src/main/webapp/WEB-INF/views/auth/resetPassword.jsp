<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="row justify-content-center">
    <div class="col-lg-5">
        <div class="dashboard-card p-4 p-lg-5">
            <span class="eyebrow">Nueva clave</span>
            <h1 class="section-title h2 mt-3 mb-4">Restablecer contrase&ntilde;a</h1>
            <form method="post" action="${pageContext.request.contextPath}/auth/reset-password" class="d-grid gap-3">
                <input type="hidden" name="token" value="${token}">
                <div>
                    <label class="form-label">Nueva contrase&ntilde;a</label>
                    <input type="password" class="form-control" name="password" required minlength="8">
                </div>
                <div>
                    <label class="form-label">Confirmar contrase&ntilde;a</label>
                    <input type="password" class="form-control" name="confirmPassword" required minlength="8">
                </div>
                <button type="submit" class="btn btn-brand">Actualizar contrase&ntilde;a</button>
            </form>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
