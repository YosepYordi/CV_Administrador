package com.cvmanager.models;

import java.io.Serializable;

public class Habilidades implements Serializable {
    public enum Category {
        TECHNICAL("technical"), SOFT("soft"), OTHER("other");
        private final String value;
        Category(String value) { this.value = value; }
        public String getValue() { return value; }
        public static Category from(String value) {
            for (Category category : values()) {
                if (category.value.equalsIgnoreCase(value) || category.name().equalsIgnoreCase(value)) return category;
            }
            return OTHER;
        }
    }

    private Long habilidadId;
    private Long cvId;
    private String habilidadName;
    private Category habilidadCategory = Category.OTHER;
    private int preferenciaLevel = 3;

    public Long getHabilidadId() { return habilidadId; }
    public void setHabilidadId(Long habilidadId) { this.habilidadId = habilidadId; }
    public Long getCvId() { return cvId; }
    public void setCvId(Long cvId) { this.cvId = cvId; }
    public String getHabilidadName() { return habilidadName; }
    public void setHabilidadName(String habilidadName) { this.habilidadName = habilidadName; }
    public Category getHabilidadCategory() { return habilidadCategory; }
    public void setHabilidadCategory(Category habilidadCategory) { this.habilidadCategory = habilidadCategory; }
    public int getPreferenciaLevel() { return preferenciaLevel; }
    public void setPreferenciaLevel(int preferenciaLevel) { this.preferenciaLevel = preferenciaLevel; }
}
