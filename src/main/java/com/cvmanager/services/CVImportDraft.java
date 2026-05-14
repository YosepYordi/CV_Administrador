package com.cvmanager.services;

public class CVImportDraft {
    private String title = "";
    private String professionalSummary = "";
    private String educationEntries = "";
    private String experienceEntries = "";
    private String skillEntries = "";
    private String languageEntries = "";
    private String certificationEntries = "";

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = value(title); }
    public String getProfessionalSummary() { return professionalSummary; }
    public void setProfessionalSummary(String professionalSummary) { this.professionalSummary = value(professionalSummary); }
    public String getEducationEntries() { return educationEntries; }
    public void setEducationEntries(String educationEntries) { this.educationEntries = value(educationEntries); }
    public String getExperienceEntries() { return experienceEntries; }
    public void setExperienceEntries(String experienceEntries) { this.experienceEntries = value(experienceEntries); }
    public String getSkillEntries() { return skillEntries; }
    public void setSkillEntries(String skillEntries) { this.skillEntries = value(skillEntries); }
    public String getLanguageEntries() { return languageEntries; }
    public void setLanguageEntries(String languageEntries) { this.languageEntries = value(languageEntries); }
    public String getCertificationEntries() { return certificationEntries; }
    public void setCertificationEntries(String certificationEntries) { this.certificationEntries = value(certificationEntries); }

    private String value(String text) {
        return text == null ? "" : text;
    }
}
