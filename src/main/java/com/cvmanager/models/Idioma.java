package com.cvmanager.models;

import java.io.Serializable;

public class Idioma implements Serializable {
    public enum Proficiency {
        BASIC("basic"), INTERMEDIATE("intermediate"), ADVANCED("advanced"), NATIVE("native");
        private final String value;
        Proficiency(String value) { this.value = value; }
        public String getValue() { return value; }
        public static Proficiency from(String value) {
            for (Proficiency proficiency : values()) {
                if (proficiency.value.equalsIgnoreCase(value) || proficiency.name().equalsIgnoreCase(value)) return proficiency;
            }
            return BASIC;
        }
    }

    private Long languageId;
    private Long cvId;
    private String languageName;
    private Proficiency proficiencyLevel = Proficiency.BASIC;
    private String certifications;

    public Long getLanguageId() { return languageId; }
    public void setLanguageId(Long languageId) { this.languageId = languageId; }
    public Long getCvId() { return cvId; }
    public void setCvId(Long cvId) { this.cvId = cvId; }
    public String getLanguageName() { return languageName; }
    public void setLanguageName(String languageName) { this.languageName = languageName; }
    public Proficiency getProficiencyLevel() { return proficiencyLevel; }
    public void setProficiencyLevel(Proficiency proficiencyLevel) { this.proficiencyLevel = proficiencyLevel; }
    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }
}
