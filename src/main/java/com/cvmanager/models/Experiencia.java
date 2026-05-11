package com.cvmanager.models;

import java.io.Serializable;
import java.time.LocalDate;

public class Experiencia implements Serializable {
    private Long experienciaId;
    private Long cvId;
    private String empresaNombre;
    private String posicion;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean current;
    private String responsibilities;
    private String achievements;
    private String employmentType;

    public Long getExperienciaId() { return experienciaId; }
    public void setExperienciaId(Long experienciaId) { this.experienciaId = experienciaId; }
    public Long getCvId() { return cvId; }
    public void setCvId(Long cvId) { this.cvId = cvId; }
    public String getEmpresaNombre() { return empresaNombre; }
    public void setEmpresaNombre(String empresaNombre) { this.empresaNombre = empresaNombre; }
    public String getPosicion() { return posicion; }
    public void setPosicion(String posicion) { this.posicion = posicion; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public boolean isCurrent() { return current; }
    public void setCurrent(boolean current) { this.current = current; }
    public String getResponsibilities() { return responsibilities; }
    public void setResponsibilities(String responsibilities) { this.responsibilities = responsibilities; }
    public String getAchievements() { return achievements; }
    public void setAchievements(String achievements) { this.achievements = achievements; }
    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
}
