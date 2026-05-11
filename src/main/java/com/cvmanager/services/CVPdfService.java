package com.cvmanager.services;

import com.cvmanager.models.CV;
import com.cvmanager.models.Certificacion;
import com.cvmanager.models.Educacion;
import com.cvmanager.models.Experiencia;
import com.cvmanager.models.Habilidades;
import com.cvmanager.models.Idioma;
import com.cvmanager.utils.ValidacionUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class CVPdfService {
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    public byte[] generate(CV cv) throws IOException {
        try (PDDocument document = new PDDocument(); PdfWriter writer = new PdfWriter(document)) {
            writer.title(firstNonBlank(cv.getGraduateName(), "Curriculum Vitae"));
            writer.text(metaLine(cv), PDType1Font.HELVETICA, 10);
            writer.spacer(10);

            writer.section("Perfil");
            writer.text(firstNonBlank(cv.getTitle(), "Perfil profesional"), PDType1Font.HELVETICA_BOLD, 12);
            writer.paragraph(cv.getProfessionalSummary());

            writeEducation(writer, cv.getEducationList());
            writeExperience(writer, cv.getExperienceList());
            writeSkills(writer, cv.getSkills());
            writeLanguages(writer, cv.getLanguages());
            writeCertifications(writer, cv.getCertifications());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writer.close();
            document.save(out);
            return out.toByteArray();
        }
    }

    private void writeEducation(PdfWriter writer, List<Educacion> educationList) throws IOException {
        writer.section("Educacion");
        List<Educacion> items = safeList(educationList);
        if (items.isEmpty()) {
            writer.text("Sin registros.", PDType1Font.HELVETICA_OBLIQUE, 10);
            return;
        }
        for (Educacion item : items) {
            writer.text(firstNonBlank(item.getDegree(), "Formacion") + " - " + firstNonBlank(item.getInstitution(), "Institucion"), PDType1Font.HELVETICA_BOLD, 11);
            writer.text(joinNonBlank(item.getFieldOfStudy(), range(item.getStartDate(), item.getEndDate(), item.isCurrent())), PDType1Font.HELVETICA, 9);
            writer.paragraph(item.getDescription());
            writer.spacer(4);
        }
    }

    private void writeExperience(PdfWriter writer, List<Experiencia> experienceList) throws IOException {
        writer.section("Experiencia");
        List<Experiencia> items = safeList(experienceList);
        if (items.isEmpty()) {
            writer.text("Sin registros.", PDType1Font.HELVETICA_OBLIQUE, 10);
            return;
        }
        for (Experiencia item : items) {
            writer.text(firstNonBlank(item.getPosicion(), "Cargo") + " - " + firstNonBlank(item.getEmpresaNombre(), "Empresa"), PDType1Font.HELVETICA_BOLD, 11);
            writer.text(joinNonBlank(range(item.getStartDate(), item.getEndDate(), item.isCurrent()), item.getEmploymentType()), PDType1Font.HELVETICA, 9);
            writer.paragraph(item.getResponsibilities());
            writer.paragraph(item.getAchievements());
            writer.spacer(4);
        }
    }

    private void writeSkills(PdfWriter writer, List<Habilidades> skills) throws IOException {
        writer.section("Habilidades");
        List<Habilidades> items = safeList(skills);
        if (items.isEmpty()) {
            writer.text("Sin registros.", PDType1Font.HELVETICA_OBLIQUE, 10);
            return;
        }
        for (Habilidades skill : items) {
            writer.text("- " + firstNonBlank(skill.getHabilidadName(), "Habilidad") + " (" + skill.getPreferenciaLevel() + "/5)", PDType1Font.HELVETICA, 10);
        }
    }

    private void writeLanguages(PdfWriter writer, List<Idioma> languages) throws IOException {
        writer.section("Idiomas");
        List<Idioma> items = safeList(languages);
        if (items.isEmpty()) {
            writer.text("Sin registros.", PDType1Font.HELVETICA_OBLIQUE, 10);
            return;
        }
        for (Idioma language : items) {
            writer.text("- " + firstNonBlank(language.getLanguageName(), "Idioma") + " - " + language.getProficiencyLevel().getValue(), PDType1Font.HELVETICA, 10);
            writer.paragraph(language.getCertifications());
        }
    }

    private void writeCertifications(PdfWriter writer, List<Certificacion> certifications) throws IOException {
        writer.section("Certificaciones");
        List<Certificacion> items = safeList(certifications);
        if (items.isEmpty()) {
            writer.text("Sin registros.", PDType1Font.HELVETICA_OBLIQUE, 10);
            return;
        }
        for (Certificacion certification : items) {
            writer.text(firstNonBlank(certification.getName(), "Certificacion"), PDType1Font.HELVETICA_BOLD, 11);
            writer.text(joinNonBlank(certification.getIssuingOrganization(), range(certification.getIssueDate(), certification.getExpirationDate(), false), certification.getCredentialId()), PDType1Font.HELVETICA, 9);
            writer.paragraph(certification.getCredentialUrl());
            writer.spacer(4);
        }
    }

    private String metaLine(CV cv) {
        return joinNonBlank(cv.getCareerName(), cv.getCity(), cv.getYearsOfExperience() > 0 ? cv.getYearsOfExperience() + " anos de experiencia" : "");
    }

    private String range(LocalDate start, LocalDate end, boolean current) {
        if (start == null && end == null && !current) return "";
        String from = start == null ? "" : MONTH_FORMAT.format(start);
        String to = current ? "Actualidad" : (end == null ? "" : MONTH_FORMAT.format(end));
        return joinNonBlank(from, to).replace(" | ", " / ");
    }

    private String firstNonBlank(String value, String fallback) {
        return ValidacionUtil.isBlank(value) ? fallback : value.trim();
    }

    private String joinNonBlank(String... values) {
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (ValidacionUtil.isBlank(value)) continue;
            if (sb.length() > 0) sb.append(" | ");
            sb.append(value.trim());
        }
        return sb.toString();
    }

    private <T> List<T> safeList(List<T> values) {
        return values == null ? Collections.emptyList() : values;
    }

    private static class PdfWriter implements AutoCloseable {
        private static final PDRectangle PAGE_SIZE = PDRectangle.LETTER;
        private static final float MARGIN = 48;
        private static final float MAX_WIDTH = PAGE_SIZE.getWidth() - (MARGIN * 2);

        private final PDDocument document;
        private PDPageContentStream content;
        private float y;
        private boolean closed;

        PdfWriter(PDDocument document) throws IOException {
            this.document = document;
            newPage();
        }

        void title(String text) throws IOException {
            text(text, PDType1Font.HELVETICA_BOLD, 20);
        }

        void section(String text) throws IOException {
            spacer(10);
            text(text.toUpperCase(), PDType1Font.HELVETICA_BOLD, 12);
            spacer(2);
        }

        void paragraph(String text) throws IOException {
            if (ValidacionUtil.isBlank(text)) return;
            for (String line : wrap(clean(text), PDType1Font.HELVETICA, 10)) {
                text(line, PDType1Font.HELVETICA, 10);
            }
        }

        void text(String text, PDFont font, float size) throws IOException {
            for (String line : wrap(clean(text), font, size)) {
                writeLine(line, font, size);
            }
        }

        void spacer(float size) throws IOException {
            ensureSpace(size);
            y -= size;
        }

        @Override
        public void close() throws IOException {
            if (!closed && content != null) {
                content.close();
                closed = true;
            }
        }

        private void writeLine(String text, PDFont font, float size) throws IOException {
            float leading = size + 4;
            ensureSpace(leading);
            content.beginText();
            content.setFont(font, size);
            content.newLineAtOffset(MARGIN, y);
            content.showText(text);
            content.endText();
            y -= leading;
        }

        private void ensureSpace(float required) throws IOException {
            if (y - required < MARGIN) {
                newPage();
            }
        }

        private void newPage() throws IOException {
            if (content != null) {
                content.close();
            }
            PDPage page = new PDPage(PAGE_SIZE);
            document.addPage(page);
            content = new PDPageContentStream(document, page);
            y = PAGE_SIZE.getHeight() - MARGIN;
            closed = false;
        }

        private List<String> wrap(String text, PDFont font, float size) throws IOException {
            if (ValidacionUtil.isBlank(text)) return List.of("");
            java.util.ArrayList<String> lines = new java.util.ArrayList<>();
            for (String paragraph : text.split("\\R")) {
                StringBuilder current = new StringBuilder();
                for (String word : paragraph.split("\\s+")) {
                    String candidate = current.length() == 0 ? word : current + " " + word;
                    if (width(candidate, font, size) <= MAX_WIDTH) {
                        current.setLength(0);
                        current.append(candidate);
                    } else {
                        if (current.length() > 0) lines.add(current.toString());
                        current.setLength(0);
                        current.append(word);
                    }
                }
                if (current.length() > 0) lines.add(current.toString());
            }
            return lines.isEmpty() ? List.of("") : lines;
        }

        private float width(String text, PDFont font, float size) throws IOException {
            return font.getStringWidth(text) / 1000 * size;
        }

        private String clean(String text) {
            if (text == null) return "";
            String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "");
            return normalized
                    .replace('\u00A0', ' ')
                    .replaceAll("[^\\x20-\\x7E\\r\\n]", " ")
                    .replaceAll("[ \\t]+", " ")
                    .trim();
        }
    }
}
