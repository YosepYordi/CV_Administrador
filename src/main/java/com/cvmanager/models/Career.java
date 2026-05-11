package com.cvmanager.models;

import java.io.Serializable;

public class Career implements Serializable {
    private Long careerId;
    private String name;
    private String code;
    private String description;
    private Integer durationYears;
    private boolean active = true;

    public Long getCareerId() { return careerId; }
    public void setCareerId(Long careerId) { this.careerId = careerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getDurationYears() { return durationYears; }
    public void setDurationYears(Integer durationYears) { this.durationYears = durationYears; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
