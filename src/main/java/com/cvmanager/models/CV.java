package com.cvmanager.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CV implements Serializable {
    private Long cvId;
    private Long graduateId;
    private String title;
    private String professionalSummary;
    private String cvPdfUrl;
    private boolean published;
    private int viewsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String graduateName;
    private String graduatePhotoUrl;
    private String careerName;
    private String city;
    private boolean graduatePublic = true;
    private double yearsOfExperience;
    private List<Educacion> educationList = new ArrayList<>();
    private List<Experiencia> experienceList = new ArrayList<>();
    private List<Habilidades> skills = new ArrayList<>();
    private List<Idioma> languages = new ArrayList<>();
    private List<Certificacion> certifications = new ArrayList<>();

    public Long getCvId() { return cvId; }
    public void setCvId(Long cvId) { this.cvId = cvId; }
    public Long getGraduateId() { return graduateId; }
    public void setGraduateId(Long graduateId) { this.graduateId = graduateId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getProfessionalSummary() { return professionalSummary; }
    public void setProfessionalSummary(String professionalSummary) { this.professionalSummary = professionalSummary; }
    public String getCvPdfUrl() { return cvPdfUrl; }
    public void setCvPdfUrl(String cvPdfUrl) { this.cvPdfUrl = cvPdfUrl; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public int getViewsCount() { return viewsCount; }
    public void setViewsCount(int viewsCount) { this.viewsCount = viewsCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getGraduateName() { return graduateName; }
    public void setGraduateName(String graduateName) { this.graduateName = graduateName; }
    public String getGraduatePhotoUrl() { return graduatePhotoUrl; }
    public void setGraduatePhotoUrl(String graduatePhotoUrl) { this.graduatePhotoUrl = graduatePhotoUrl; }
    public String getCareerName() { return careerName; }
    public void setCareerName(String careerName) { this.careerName = careerName; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public boolean isGraduatePublic() { return graduatePublic; }
    public void setGraduatePublic(boolean graduatePublic) { this.graduatePublic = graduatePublic; }
    public double getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(double yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public List<Educacion> getEducationList() { return educationList; }
    public void setEducationList(List<Educacion> educationList) { this.educationList = educationList; }
    public List<Experiencia> getExperienceList() { return experienceList; }
    public void setExperienceList(List<Experiencia> experienceList) { this.experienceList = experienceList; }
    public List<Habilidades> getSkills() { return skills; }
    public void setSkills(List<Habilidades> skills) { this.skills = skills; }
    public List<Idioma> getLanguages() { return languages; }
    public void setLanguages(List<Idioma> languages) { this.languages = languages; }
    public List<Certificacion> getCertifications() { return certifications; }
    public void setCertifications(List<Certificacion> certifications) { this.certifications = certifications; }
}
