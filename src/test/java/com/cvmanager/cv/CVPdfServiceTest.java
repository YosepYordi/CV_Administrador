package com.cvmanager.cv;

import com.cvmanager.models.CV;
import com.cvmanager.models.Certificacion;
import com.cvmanager.models.Educacion;
import com.cvmanager.models.Experiencia;
import com.cvmanager.models.Habilidades;
import com.cvmanager.models.Idioma;
import com.cvmanager.services.CVPdfService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CVPdfServiceTest {
    @Test
    void generateProducesReadablePdfWithStructuredSections() throws Exception {
        CV cv = new CV();
        cv.setGraduateName("Ada Lovelace");
        cv.setCareerName("Ingenieria de Software");
        cv.setCity("Lima");
        cv.setTitle("Desarrolladora Java");
        cv.setProfessionalSummary("Construye sistemas web para seleccion de talento.");

        Educacion education = new Educacion();
        education.setInstitution("Instituto Central");
        education.setDegree("Tecnico");
        education.setFieldOfStudy("Software");
        education.setStartDate(LocalDate.of(2021, 1, 1));

        Experiencia experience = new Experiencia();
        experience.setEmpresaNombre("Acme");
        experience.setPosicion("Analista");
        experience.setResponsibilities("Desarrollo de modulos MVC.");

        Habilidades skill = new Habilidades();
        skill.setHabilidadName("Java");
        skill.setPreferenciaLevel(5);

        Idioma language = new Idioma();
        language.setLanguageName("Ingles");
        language.setProficiencyLevel(Idioma.Proficiency.ADVANCED);

        Certificacion certification = new Certificacion();
        certification.setName("Scrum");
        certification.setIssuingOrganization("PMI");

        cv.setEducationList(List.of(education));
        cv.setExperienceList(List.of(experience));
        cv.setSkills(List.of(skill));
        cv.setLanguages(List.of(language));
        cv.setCertifications(List.of(certification));

        byte[] pdf = new CVPdfService().generate(cv);

        assertEquals("%PDF", new String(pdf, 0, 4, StandardCharsets.US_ASCII));
        try (PDDocument document = PDDocument.load(pdf)) {
            String text = new PDFTextStripper().getText(document);
            assertTrue(text.contains("Ada Lovelace"));
            assertTrue(text.contains("Desarrolladora Java"));
            assertTrue(text.contains("Instituto Central"));
            assertTrue(text.contains("Acme"));
            assertTrue(text.contains("Java"));
            assertTrue(text.contains("Scrum"));
        }
    }
}
