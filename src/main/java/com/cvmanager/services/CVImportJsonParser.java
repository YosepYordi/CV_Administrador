package com.cvmanager.services;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

import java.io.StringReader;
import java.time.LocalDate;

public class CVImportJsonParser {

    public CVImportDraft parse(String modelContent) {
        String json = extractJsonObject(modelContent);
        try (JsonReader reader = Json.createReader(new StringReader(json))) {
            JsonObject root = reader.readObject();
            CVImportDraft draft = new CVImportDraft();
            draft.setTitle(text(root, "title"));
            draft.setProfessionalSummary(text(root, "professionalSummary"));
            draft.setEducationEntries(joinEducation(root.getJsonArray("education")));
            draft.setExperienceEntries(joinExperience(root.getJsonArray("experience")));
            draft.setSkillEntries(joinSkills(root.getJsonArray("skills")));
            draft.setLanguageEntries(joinLanguages(root.getJsonArray("languages")));
            draft.setCertificationEntries(joinCertifications(root.getJsonArray("certifications")));
            return draft;
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("La IA no devolvio un JSON valido para importar el CV.", ex);
        }
    }

    private String extractJsonObject(String content) {
        if (content == null) throw new IllegalArgumentException("La IA no devolvio contenido.");
        String cleaned = content.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceFirst("^```[a-zA-Z]*\\s*", "");
            cleaned = cleaned.replaceFirst("\\s*```$", "");
        }
        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');
        if (start < 0 || end <= start) throw new IllegalArgumentException("La IA no devolvio un objeto JSON.");
        return cleaned.substring(start, end + 1);
    }

    private String joinEducation(JsonArray items) {
        StringBuilder sb = new StringBuilder();
        for (JsonObject item : objects(items)) {
            String institution = text(item, "institution");
            String degree = text(item, "degree");
            String field = text(item, "fieldOfStudy");
            String start = date(item, "startDate");
            String end = date(item, "endDate");
            String description = text(item, "description");
            if (validEducation(institution, degree, field, start, end, description)) {
                appendLine(sb, institution, degree, field, start, end, description);
            }
        }
        return sb.toString();
    }

    private String joinExperience(JsonArray items) {
        StringBuilder sb = new StringBuilder();
        for (JsonObject item : objects(items)) {
            String company = text(item, "company");
            String position = text(item, "position");
            if (validExperience(company, position)) {
                appendLine(sb,
                        company,
                        position,
                        date(item, "startDate"),
                        date(item, "endDate"),
                        text(item, "employmentType"),
                        text(item, "responsibilities"),
                        text(item, "achievements"));
            }
        }
        return sb.toString();
    }

    private String joinSkills(JsonArray items) {
        StringBuilder sb = new StringBuilder();
        for (JsonObject item : objects(items)) {
            appendLine(sb,
                    text(item, "name"),
                    skillCategory(text(item, "category")),
                    skillLevel(item.get("level")));
        }
        return sb.toString();
    }

    private String joinLanguages(JsonArray items) {
        StringBuilder sb = new StringBuilder();
        for (JsonObject item : objects(items)) {
            String rawName = text(item, "name");
            String inferredLevel = inferredLanguageLevel(rawName);
            String explicitLevel = text(item, "level");
            appendLine(sb,
                    languageName(rawName),
                    languageLevel(explicitLevel.isBlank() ? inferredLevel : explicitLevel),
                    text(item, "certifications"));
        }
        return sb.toString();
    }

    private String joinCertifications(JsonArray items) {
        StringBuilder sb = new StringBuilder();
        for (JsonObject item : objects(items)) {
            String name = text(item, "name");
            String issuer = text(item, "issuingOrganization");
            String issueDate = date(item, "issueDate");
            String expirationDate = date(item, "expirationDate");
            String credentialId = text(item, "credentialId");
            String credentialUrl = text(item, "credentialUrl");
            if (validCertification(name, issuer, issueDate, credentialId, credentialUrl)) {
                appendLine(sb, name, issuer, issueDate, expirationDate, credentialId, credentialUrl);
            }
        }
        return sb.toString();
    }

    private Iterable<JsonObject> objects(JsonArray items) {
        return () -> (items == null ? JsonArray.EMPTY_JSON_ARRAY : items)
                .stream()
                .filter(value -> value.getValueType() == JsonValue.ValueType.OBJECT)
                .map(JsonValue::asJsonObject)
                .iterator();
    }

    private void appendLine(StringBuilder sb, String... values) {
        boolean hasContent = false;
        for (String value : values) {
            if (!value.isBlank()) {
                hasContent = true;
                break;
            }
        }
        if (!hasContent) return;
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append('|');
            sb.append(values[i]);
        }
        sb.append('\n');
    }

    private String text(JsonObject object, String key) {
        if (object == null || !object.containsKey(key) || object.isNull(key)) return "";
        JsonValue value = object.get(key);
        String raw = switch (value.getValueType()) {
            case STRING -> object.getString(key, "");
            case NUMBER -> object.getJsonNumber(key).toString();
            case TRUE -> "true";
            case FALSE -> "false";
            default -> "";
        };
        return clean(raw);
    }

    private String date(JsonObject object, String key) {
        String value = text(object, key);
        if (value.isBlank()) return "";
        try {
            return LocalDate.parse(value).toString();
        } catch (RuntimeException ignored) {
            return "";
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.replace('|', '/').replaceAll("\\s+", " ").trim();
    }

    private String skillCategory(String value) {
        String normalized = value.toLowerCase();
        if (normalized.contains("technical") || normalized.contains("tecnic")) return "technical";
        if (normalized.contains("soft") || normalized.contains("bland")) return "soft";
        return "other";
    }

    private String languageLevel(String value) {
        String normalized = value.toLowerCase();
        if (normalized.contains("native") || normalized.contains("nativ")) return "native";
        if (normalized.contains("advanced") || normalized.contains("avanz")) return "advanced";
        if (normalized.contains("intermediate") || normalized.contains("intermedio")) return "intermediate";
        return "basic";
    }

    private String skillLevel(JsonValue value) {
        int level = 3;
        try {
            if (value != null && value.getValueType() == JsonValue.ValueType.NUMBER) {
                level = Integer.parseInt(value.toString());
            } else if (value != null && value.getValueType() == JsonValue.ValueType.STRING) {
                level = Integer.parseInt(clean(value.toString().replace("\"", "")));
            }
        } catch (RuntimeException ignored) {
            level = 3;
        }
        return Integer.toString(Math.max(1, Math.min(5, level)));
    }

    private boolean validEducation(String institution, String degree, String field, String start, String end, String description) {
        if (institution.isBlank() && degree.isBlank()) return false;
        return !field.isBlank() || !start.isBlank() || !end.isBlank() || !description.isBlank();
    }

    private boolean validExperience(String company, String position) {
        if (company.isBlank() || position.isBlank()) return false;
        String normalized = company.toLowerCase();
        return !normalized.contains("sin experiencia") && !normalized.contains("no aplica");
    }

    private boolean validCertification(String name, String issuer, String issueDate, String credentialId, String credentialUrl) {
        if (name.isBlank()) return false;
        return !issuer.isBlank() || !issueDate.isBlank() || !credentialId.isBlank() || !credentialUrl.isBlank();
    }

    private String languageName(String value) {
        return clean(value)
                .replaceAll("(?i)\\b(nativo|native|b[aá]sico|basic|intermedio|intermediate|avanzado|advanced)\\b", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String inferredLanguageLevel(String value) {
        String normalized = clean(value).toLowerCase();
        if (normalized.contains("nativo") || normalized.contains("native")) return "native";
        if (normalized.contains("avanzado") || normalized.contains("advanced")) return "advanced";
        if (normalized.contains("intermedio") || normalized.contains("intermediate")) return "intermediate";
        if (normalized.contains("basico") || normalized.contains("básico") || normalized.contains("basic")) return "basic";
        return "";
    }
}
