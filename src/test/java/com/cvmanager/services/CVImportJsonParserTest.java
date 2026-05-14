package com.cvmanager.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CVImportJsonParserTest {

    @Test
    void parsesModelJsonIntoEditableCvFields() {
        String modelContent = """
                ```json
                {
                  "title": "Desarrollador Java Junior",
                  "professionalSummary": "Egresado con experiencia en aplicaciones web.",
                  "education": [
                    {
                      "institution": "Instituto Tecnologico Lima",
                      "degree": "Egresado",
                      "fieldOfStudy": "Desarrollo de Software",
                      "startDate": "2021-03-01",
                      "endDate": "2024-12-01",
                      "description": "Formacion en Java y bases de datos"
                    }
                  ],
                  "experience": [
                    {
                      "company": "Acme SAC",
                      "position": "Practicante de desarrollo",
                      "startDate": "2024-01-01",
                      "endDate": "",
                      "employmentType": "Practicas",
                      "responsibilities": "Desarrollo de modulos internos",
                      "achievements": "Automatizo reportes"
                    }
                  ],
                  "skills": [
                    {"name": "Java", "category": "technical", "level": 5}
                  ],
                  "languages": [
                    {"name": "Ingles", "level": "intermediate", "certifications": "B1"}
                  ],
                  "certifications": [
                    {
                      "name": "Scrum Fundamentals",
                      "issuingOrganization": "SCRUMstudy",
                      "issueDate": "2024-01-10",
                      "expirationDate": null,
                      "credentialId": "ABC123",
                      "credentialUrl": "https://example.com/abc123"
                    }
                  ]
                }
                ```
                """;

        CVImportDraft draft = new CVImportJsonParser().parse(modelContent);

        assertEquals("Desarrollador Java Junior", draft.getTitle());
        assertEquals("Egresado con experiencia en aplicaciones web.", draft.getProfessionalSummary());
        assertEquals("Instituto Tecnologico Lima|Egresado|Desarrollo de Software|2021-03-01|2024-12-01|Formacion en Java y bases de datos\n", draft.getEducationEntries());
        assertEquals("Acme SAC|Practicante de desarrollo|2024-01-01||Practicas|Desarrollo de modulos internos|Automatizo reportes\n", draft.getExperienceEntries());
        assertEquals("Java|technical|5\n", draft.getSkillEntries());
        assertEquals("Ingles|intermediate|B1\n", draft.getLanguageEntries());
        assertEquals("Scrum Fundamentals|SCRUMstudy|2024-01-10||ABC123|https://example.com/abc123\n", draft.getCertificationEntries());
    }

    @Test
    void skipsFragmentedEntriesAndNormalizesLanguageNames() {
        String modelContent = """
                {
                  "title": "Mi curriculum profesional",
                  "professionalSummary": "Resumen",
                  "education": [
                    {"institution": "ngeniería de Sistemas de Información — ELP", "degree": "", "fieldOfStudy": "", "startDate": "", "endDate": "", "description": ""},
                    {"institution": "Marzo 2024 – diciembre 2028, egreso estimado", "degree": "Actualmente cursando el 5.º", "fieldOfStudy": "", "startDate": "", "endDate": "", "description": ""},
                    {"institution": "ELP", "degree": "Ingeniería de Sistemas de Información", "fieldOfStudy": "Sistemas de Información", "startDate": "2024-03-01", "endDate": "2028-12-01", "description": "Actualmente cursando el quinto ciclo"}
                  ],
                  "experience": [
                    {"company": "sin experiencia", "position": "", "startDate": "", "endDate": "", "employmentType": "", "responsibilities": "", "achievements": ""}
                  ],
                  "skills": [],
                  "languages": [
                    {"name": "Español nativo", "level": "", "certifications": ""},
                    {"name": "Ingles básico", "level": "", "certifications": ""}
                  ],
                  "certifications": [
                    {"name": "Certificado ONPE: capacitación como Miembro de Mesa para las Elecciones", "issuingOrganization": "", "issueDate": "", "expirationDate": "", "credentialId": "", "credentialUrl": ""},
                    {"name": "Certificado Único Laboral vigente", "issuingOrganization": "Ministerio de Trabajo y Promoción del Empleo", "issueDate": "2026-05-02", "expirationDate": "", "credentialId": "", "credentialUrl": ""}
                  ]
                }
                """;

        CVImportDraft draft = new CVImportJsonParser().parse(modelContent);

        assertEquals("ELP|Ingeniería de Sistemas de Información|Sistemas de Información|2024-03-01|2028-12-01|Actualmente cursando el quinto ciclo\n", draft.getEducationEntries());
        assertEquals("", draft.getExperienceEntries());
        assertEquals("Español|native|\nIngles|basic|\n", draft.getLanguageEntries());
        assertEquals("Certificado Único Laboral vigente|Ministerio de Trabajo y Promoción del Empleo|2026-05-02|||\n", draft.getCertificationEntries());
    }
}
