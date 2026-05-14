<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<section class="dashboard-card p-4 p-lg-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="section-title h2 mb-1">Editar CV</h1>
            <p class="muted mb-0">Completa las secciones por filas. El sistema ordena los datos al guardar.</p>
        </div>
    </div>
    <form method="post" enctype="multipart/form-data" action="${pageContext.request.contextPath}/graduate/cv/edit" class="row g-3" data-cv-edit-form>
        <div class="col-md-8">
            <label class="form-label">Titular profesional</label>
            <input class="form-control" name="title" value="${cv.title}">
        </div>
        <div class="col-md-4">
            <label class="form-label">Adjuntar CV PDF</label>
            <input type="file" class="form-control" name="cvPdf" accept="application/pdf">
            <c:if test="${not empty cv.cvPdfUrl}">
                <div class="muted small mt-2"><i class="fa-regular fa-file-pdf"></i> PDF adjunto guardado.</div>
            </c:if>
            <button class="btn btn-outline-info w-100 mt-2" type="submit" name="action" value="importAi">Importar con IA</button>
        </div>
        <div class="col-12">
            <label class="form-label">Resumen profesional</label>
            <textarea class="form-control" rows="4" name="professionalSummary" placeholder="Perfil breve, enfoque profesional y fortalezas principales."><c:out value="${cv.professionalSummary}" /></textarea>
        </div>
        <div class="col-12">
            <div class="cv-smart-section" data-cv-builder="education">
                <textarea class="cv-raw-field" name="educationEntries" data-raw-field><c:out value="${educationText}" /></textarea>
                <div class="cv-smart-header">
                    <div>
                        <label class="form-label mb-1">Educaci&oacute;n</label>
                        <p class="muted mb-0">Instituci&oacute;n, carrera, fechas y detalle acad&eacute;mico.</p>
                    </div>
                    <button class="btn btn-outline-primary btn-sm" type="button" data-add-row>
                        <i class="fa-solid fa-plus"></i> Agregar
                    </button>
                </div>
                <div class="cv-builder-list" data-builder-list></div>
                <div class="empty-state smart-empty" data-empty-state>Aun no hay educaci&oacute;n registrada.</div>
                <template data-row-template>
                    <div class="cv-row-card" data-builder-row>
                        <div class="cv-row-actions">
                            <strong>Formaci&oacute;n</strong>
                            <button class="btn btn-outline-danger btn-sm btn-icon" type="button" title="Eliminar" data-remove-row>
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        </div>
                        <div class="cv-row-grid education-grid">
                            <div>
                                <label class="form-label">Instituci&oacute;n</label>
                                <input class="form-control" data-field="institution" placeholder="Instituto o universidad">
                            </div>
                            <div>
                                <label class="form-label">Carrera o grado</label>
                                <input class="form-control" data-field="degree" placeholder="Ingenier&iacute;a, bachiller, curso">
                            </div>
                            <div>
                                <label class="form-label">Campo</label>
                                <input class="form-control" data-field="field" placeholder="Sistemas, dise&ntilde;o, administraci&oacute;n">
                            </div>
                            <div>
                                <label class="form-label">Inicio</label>
                                <input class="form-control" type="date" data-field="start">
                            </div>
                            <div>
                                <label class="form-label">Fin</label>
                                <input class="form-control" type="date" data-field="end">
                            </div>
                            <div class="span-2">
                                <label class="form-label">Detalle</label>
                                <textarea class="form-control" rows="2" data-field="description" placeholder="Ciclo actual, logros o cursos relevantes"></textarea>
                            </div>
                        </div>
                    </div>
                </template>
            </div>
        </div>
        <div class="col-12">
            <div class="cv-smart-section" data-cv-builder="experience">
                <textarea class="cv-raw-field" name="experienceEntries" data-raw-field><c:out value="${experienceText}" /></textarea>
                <div class="cv-smart-header">
                    <div>
                        <label class="form-label mb-1">Experiencia</label>
                        <p class="muted mb-0">Trabajos, pr&aacute;cticas, voluntariados o proyectos.</p>
                    </div>
                    <div class="d-flex flex-wrap gap-2">
                        <button class="btn btn-outline-secondary btn-sm" type="button" data-clear-rows>
                            <i class="fa-solid fa-circle-minus"></i> Sin experiencia
                        </button>
                        <button class="btn btn-outline-primary btn-sm" type="button" data-add-row>
                            <i class="fa-solid fa-plus"></i> Agregar
                        </button>
                    </div>
                </div>
                <div class="cv-builder-list" data-builder-list></div>
                <div class="empty-state smart-empty" data-empty-state>Aun no hay experiencia registrada.</div>
                <template data-row-template>
                    <div class="cv-row-card" data-builder-row>
                        <div class="cv-row-actions">
                            <strong>Experiencia</strong>
                            <button class="btn btn-outline-danger btn-sm btn-icon" type="button" title="Eliminar" data-remove-row>
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        </div>
                        <div class="cv-row-grid experience-grid">
                            <div>
                                <label class="form-label">Empresa</label>
                                <input class="form-control" data-field="company" placeholder="Empresa o proyecto">
                            </div>
                            <div>
                                <label class="form-label">Cargo</label>
                                <input class="form-control" data-field="position" placeholder="Practicante, asistente, freelance">
                            </div>
                            <div>
                                <label class="form-label">Inicio</label>
                                <input class="form-control" type="date" data-field="start">
                            </div>
                            <div>
                                <label class="form-label">Fin</label>
                                <input class="form-control" type="date" data-field="end">
                            </div>
                            <div>
                                <label class="form-label">Tipo</label>
                                <select class="form-select" data-field="type">
                                    <option value="">Seleccionar</option>
                                    <option value="Tiempo completo">Tiempo completo</option>
                                    <option value="Medio tiempo">Medio tiempo</option>
                                    <option value="Practicas">Pr&aacute;cticas</option>
                                    <option value="Freelance">Freelance</option>
                                    <option value="Voluntariado">Voluntariado</option>
                                    <option value="Proyecto">Proyecto</option>
                                </select>
                            </div>
                            <div class="span-2">
                                <label class="form-label">Responsabilidades</label>
                                <textarea class="form-control" rows="2" data-field="responsibilities" placeholder="Tareas principales"></textarea>
                            </div>
                            <div class="span-2">
                                <label class="form-label">Logros</label>
                                <textarea class="form-control" rows="2" data-field="achievements" placeholder="Resultados, mejoras o aprendizajes"></textarea>
                            </div>
                        </div>
                    </div>
                </template>
            </div>
        </div>
        <div class="col-lg-4">
            <div class="cv-smart-section compact" data-cv-builder="skills">
                <textarea class="cv-raw-field" name="skillEntries" data-raw-field><c:out value="${skillsText}" /></textarea>
                <div class="cv-smart-header">
                    <label class="form-label mb-0">Habilidades</label>
                    <button class="btn btn-outline-primary btn-sm btn-icon" type="button" title="Agregar" data-add-row>
                        <i class="fa-solid fa-plus"></i>
                    </button>
                </div>
                <div class="cv-builder-list" data-builder-list></div>
                <div class="empty-state smart-empty" data-empty-state>Sin habilidades.</div>
                <template data-row-template>
                    <div class="cv-row-card compact-row" data-builder-row>
                        <div class="cv-row-actions">
                            <strong>Habilidad</strong>
                            <button class="btn btn-outline-danger btn-sm btn-icon" type="button" title="Eliminar" data-remove-row>
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        </div>
                        <div class="cv-row-grid one-col">
                            <div>
                                <label class="form-label">Nombre</label>
                                <input class="form-control" data-field="name" placeholder="Java, Excel, trabajo en equipo">
                            </div>
                            <div>
                                <label class="form-label">Categor&iacute;a</label>
                                <select class="form-select" data-field="category">
                                    <option value="technical">T&eacute;cnica</option>
                                    <option value="soft">Blanda</option>
                                    <option value="other">Otra</option>
                                </select>
                            </div>
                            <div>
                                <label class="form-label">Nivel</label>
                                <select class="form-select" data-field="level">
                                    <option value="1">1 - Inicial</option>
                                    <option value="2">2 - B&aacute;sico</option>
                                    <option value="3">3 - Intermedio</option>
                                    <option value="4">4 - Alto</option>
                                    <option value="5">5 - Avanzado</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </template>
            </div>
        </div>
        <div class="col-lg-4">
            <div class="cv-smart-section compact" data-cv-builder="languages">
                <textarea class="cv-raw-field" name="languageEntries" data-raw-field><c:out value="${languagesText}" /></textarea>
                <div class="cv-smart-header">
                    <label class="form-label mb-0">Idiomas</label>
                    <button class="btn btn-outline-primary btn-sm btn-icon" type="button" title="Agregar" data-add-row>
                        <i class="fa-solid fa-plus"></i>
                    </button>
                </div>
                <div class="cv-builder-list" data-builder-list></div>
                <div class="empty-state smart-empty" data-empty-state>Sin idiomas.</div>
                <template data-row-template>
                    <div class="cv-row-card compact-row" data-builder-row>
                        <div class="cv-row-actions">
                            <strong>Idioma</strong>
                            <button class="btn btn-outline-danger btn-sm btn-icon" type="button" title="Eliminar" data-remove-row>
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        </div>
                        <div class="cv-row-grid one-col">
                            <div>
                                <label class="form-label">Idioma</label>
                                <input class="form-control" data-field="name" placeholder="Espa&ntilde;ol, Ingl&eacute;s">
                            </div>
                            <div>
                                <label class="form-label">Nivel</label>
                                <select class="form-select" data-field="level">
                                    <option value="basic">B&aacute;sico</option>
                                    <option value="intermediate">Intermedio</option>
                                    <option value="advanced">Avanzado</option>
                                    <option value="native">Nativo</option>
                                </select>
                            </div>
                            <div>
                                <label class="form-label">Certificado</label>
                                <input class="form-control" data-field="certification" placeholder="B2, TOEFL, certificado interno">
                            </div>
                        </div>
                    </div>
                </template>
            </div>
        </div>
        <div class="col-lg-4">
            <div class="cv-smart-section compact" data-cv-builder="certifications">
                <textarea class="cv-raw-field" name="certificationEntries" data-raw-field><c:out value="${certificationsText}" /></textarea>
                <div class="cv-smart-header">
                    <label class="form-label mb-0">Certificaciones</label>
                    <button class="btn btn-outline-primary btn-sm btn-icon" type="button" title="Agregar" data-add-row>
                        <i class="fa-solid fa-plus"></i>
                    </button>
                </div>
                <div class="cv-builder-list" data-builder-list></div>
                <div class="empty-state smart-empty" data-empty-state>Sin certificaciones.</div>
                <template data-row-template>
                    <div class="cv-row-card compact-row" data-builder-row>
                        <div class="cv-row-actions">
                            <strong>Certificaci&oacute;n</strong>
                            <button class="btn btn-outline-danger btn-sm btn-icon" type="button" title="Eliminar" data-remove-row>
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        </div>
                        <div class="cv-row-grid one-col">
                            <div>
                                <label class="form-label">Nombre</label>
                                <input class="form-control" data-field="name" placeholder="Scrum, Excel, ONPE">
                            </div>
                            <div>
                                <label class="form-label">Emisor</label>
                                <input class="form-control" data-field="issuer" placeholder="Instituci&oacute;n emisora">
                            </div>
                            <div>
                                <label class="form-label">Fecha</label>
                                <input class="form-control" type="date" data-field="issueDate">
                            </div>
                            <div>
                                <label class="form-label">Vence</label>
                                <input class="form-control" type="date" data-field="expirationDate">
                            </div>
                            <div>
                                <label class="form-label">C&oacute;digo</label>
                                <input class="form-control" data-field="credentialId" placeholder="ID o c&oacute;digo">
                            </div>
                            <div>
                                <label class="form-label">URL</label>
                                <input class="form-control" type="url" data-field="credentialUrl" placeholder="https://...">
                            </div>
                        </div>
                    </div>
                </template>
            </div>
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
