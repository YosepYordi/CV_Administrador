package com.cvmanager.services;

import com.cvmanager.dao.impl.*;
import com.cvmanager.dao.interfaces.*;
import com.cvmanager.models.CV;

import java.sql.SQLException;
import java.util.Optional;

public class CVService {
    private final CVDAO cvDAO;
    private final EducacionDAO educacionDAO;
    private final ExperienciaDAO experienciaDAO;
    private final HabilidadesDAO habilidadesDAO;
    private final IdiomaDAO idiomaDAO;
    private final CertificacionDAO certificacionDAO;

    public CVService() {
        this(new CVDAOImpl(), new EducacionDAOImpl(), new ExperienciaDAOImpl(), new HabilidadesDAOImpl(), new IdiomaDAOImpl(), new CertificacionDAOImpl());
    }

    public CVService(CVDAO cvDAO, EducacionDAO educacionDAO, ExperienciaDAO experienciaDAO, HabilidadesDAO habilidadesDAO, IdiomaDAO idiomaDAO, CertificacionDAO certificacionDAO) {
        this.cvDAO = cvDAO;
        this.educacionDAO = educacionDAO;
        this.experienciaDAO = experienciaDAO;
        this.habilidadesDAO = habilidadesDAO;
        this.idiomaDAO = idiomaDAO;
        this.certificacionDAO = certificacionDAO;
    }

    public CV getOrCreateByGraduateId(Long graduateId) throws SQLException {
        Optional<CV> existing = cvDAO.findByGraduateId(graduateId);
        if (existing.isPresent()) return hydrate(existing.get(), false);
        CV cv = new CV();
        cv.setGraduateId(graduateId);
        cv.setTitle("Mi curriculum profesional");
        cv.setProfessionalSummary("Resumen profesional pendiente de completar.");
        cv.setPublished(false);
        cv.setCvId(cvDAO.create(cv));
        return hydrate(cv, false);
    }

    public Optional<CV> findById(Long id, boolean incrementViews) throws SQLException {
        Optional<CV> cv = cvDAO.findById(id);
        if (cv.isPresent() && incrementViews) cvDAO.incrementViews(id);
        return cv.map(value -> {
            try { return hydrate(value, false); } catch (SQLException ex) { throw new IllegalStateException(ex); }
        });
    }

    public Optional<CV> findByGraduateId(Long graduateId) throws SQLException {
        Optional<CV> cv = cvDAO.findByGraduateId(graduateId);
        return cv.map(value -> {
            try { return hydrate(value, false); } catch (SQLException ex) { throw new IllegalStateException(ex); }
        });
    }

    public CV save(CV cv) throws SQLException {
        if (cv.getCvId() == null) cv.setCvId(cvDAO.create(cv)); else cvDAO.update(cv);
        educacionDAO.replaceAll(cv.getCvId(), cv.getEducationList());
        experienciaDAO.replaceAll(cv.getCvId(), cv.getExperienceList());
        habilidadesDAO.replaceAll(cv.getCvId(), cv.getSkills());
        idiomaDAO.replaceAll(cv.getCvId(), cv.getLanguages());
        certificacionDAO.replaceAll(cv.getCvId(), cv.getCertifications());
        return hydrate(cvDAO.findById(cv.getCvId()).orElse(cv), false);
    }

    private CV hydrate(CV cv, boolean ignored) throws SQLException {
        if (cv.getCvId() != null) {
            cv.setEducationList(educacionDAO.findByCvId(cv.getCvId()));
            cv.setExperienceList(experienciaDAO.findByCvId(cv.getCvId()));
            cv.setSkills(habilidadesDAO.findByCvId(cv.getCvId()));
            cv.setLanguages(idiomaDAO.findByCvId(cv.getCvId()));
            cv.setCertifications(certificacionDAO.findByCvId(cv.getCvId()));
        }
        return cv;
    }
}
