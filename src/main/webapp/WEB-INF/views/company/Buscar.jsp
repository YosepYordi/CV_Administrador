<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="search-card p-4 p-lg-5">
    <h1 class="section-title h2 mb-4">Búsqueda avanzada de candidatos</h1>
    <form method="get" action="${pageContext.request.contextPath}/company/search" class="row g-3">
        <div class="col-md-4">
            <label class="form-label">Carrera</label>
            <select class="form-select" name="career">
                <option value="">Todas</option>
                <c:forEach items="${careers}" var="career">
                    <option value="${career.name}">${career.name}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-4">
            <label class="form-label">Habilidad</label>
            <input class="form-control" name="skill" placeholder="Java, SQL, liderazgo">
        </div>
        <div class="col-md-4">
            <label class="form-label">Idioma</label>
            <input class="form-control" name="language" placeholder="Inglés">
        </div>
        <div class="col-md-4">
            <label class="form-label">Ciudad</label>
            <input class="form-control" name="city">
        </div>
        <div class="col-md-4">
            <label class="form-label">Experiencia mínima (años)</label>
            <input type="number" class="form-control" name="minExperience">
        </div>
        <div class="col-md-4">
            <label class="form-label">Palabra clave</label>
            <input class="form-control" name="keyword" placeholder="backend, soporte, análisis">
        </div>
        <div class="col-12">
            <button class="btn btn-brand" type="submit">Buscar perfiles</button>
        </div>
    </form>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
