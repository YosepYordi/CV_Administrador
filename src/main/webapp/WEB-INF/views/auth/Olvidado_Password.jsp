<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="row justify-content-center">
    <div class="col-lg-5">
        <div class="dashboard-card p-4 p-lg-5">
            <span class="eyebrow">Recuperaci&oacute;n</span>
            <h1 class="section-title h2 mt-3 mb-4">Restablecer acceso</h1>
            <form method="post" action="${pageContext.request.contextPath}/auth/forgot-password" class="d-grid gap-3">
                <div>
                    <label class="form-label">Correo registrado</label>
                    <input type="email" class="form-control" name="email" required>
                </div>
                <button type="submit" class="btn btn-brand">Enviar instrucciones</button>
            </form>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
