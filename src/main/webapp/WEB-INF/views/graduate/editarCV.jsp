<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="dashboard-card p-4 p-lg-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="section-title h2 mb-1">Editar CV</h1>
            <p class="muted mb-0">Cada l&iacute;nea usa formato separado por <strong>|</strong>. El sistema ya convierte esas l&iacute;neas en secciones del CV.</p>
        </div>
    </div>
    <form method="post" enctype="multipart/form-data" action="${pageContext.request.contextPath}/graduate/cv/edit" class="row g-3">
        <div class="col-md-8">
            <label class="form-label">Titular profesional</label>
            <input class="form-control" name="title" value="${cv.title}">
        </div>
        <div class="col-md-4">
            <label class="form-label">Adjuntar CV PDF</label>
            <input type="file" class="form-control" name="cvPdf" accept="application/pdf">
        </div>
        <div class="col-12">
            <label class="form-label">Resumen profesional</label>
            <textarea class="form-control" rows="4" name="professionalSummary">${cv.professionalSummary}</textarea>
        </div>
        <div class="col-12">
            <label class="form-label">Educaci&oacute;n</label>
            <textarea class="form-control mono-area" name="educationEntries" placeholder="Instituci&oacute;n|Grado|Campo|2020-03-01|2024-12-01|Descripci&oacute;n">${educationText}</textarea>
        </div>
        <div class="col-12">
            <label class="form-label">Experiencia</label>
            <textarea class="form-control mono-area" name="experienceEntries" placeholder="Empresa|Puesto|2023-01-01|2024-02-01|Tiempo completo|Responsabilidades|Logros">${experienceText}</textarea>
        </div>
        <div class="col-md-4">
            <label class="form-label">Habilidades</label>
            <textarea class="form-control mono-area" name="skillEntries" placeholder="Java|technical|5">${skillsText}</textarea>
        </div>
        <div class="col-md-4">
            <label class="form-label">Idiomas</label>
            <textarea class="form-control mono-area" name="languageEntries" placeholder="Ingl&eacute;s|advanced|B2">${languagesText}</textarea>
        </div>
        <div class="col-md-4">
            <label class="form-label">Certificaciones</label>
            <textarea class="form-control mono-area" name="certificationEntries" placeholder="SCRUM|PMI|2024-01-10||ABC123|https://...">${certificationsText}</textarea>
        </div>
        <div class="col-12 form-check ms-2">
            <input class="form-check-input" type="checkbox" name="published" ${cv.published ? 'checked' : ''}>
            <label class="form-check-label">Publicar CV para empresas</label>
        </div>
        <div class="col-12 d-flex gap-2">
            <button class="btn btn-brand" type="submit">Guardar CV</button>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/graduate/cv">Vista previa</a>
        </div>
    </form>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
