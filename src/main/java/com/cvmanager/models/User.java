package com.cvmanager.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {
    public enum Role {
        GRADUATE("graduate", "Egresado"), COMPANY("company", "Empresa"), ADMIN("admin", "Administrador");
        private final String value;
        private final String label;
        Role(String value, String label) {
            this.value = value;
            this.label = label;
        }
        public String getValue() { return value; }
        public String getLabel() { return label; }
        public static Role from(String value) {
            for (Role role : values()) {
                if (role.value.equalsIgnoreCase(value) || role.name().equalsIgnoreCase(value)) return role;
            }
            return GRADUATE;
        }
    }

    public enum Status {
        ACTIVE("active", "Activo"), INACTIVE("inactive", "Inactivo"), PENDING("pending", "Pendiente");
        private final String value;
        private final String label;
        Status(String value, String label) {
            this.value = value;
            this.label = label;
        }
        public String getValue() { return value; }
        public String getLabel() { return label; }
        public static Status from(String value) {
            for (Status status : values()) {
                if (status.value.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) return status;
            }
            return PENDING;
        }
    }

    private Long userId;
    private String email;
    private String passwordHash;
    private Role role;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    public boolean isGraduate() { return role == Role.GRADUATE; }
    public boolean isCompany() { return role == Role.COMPANY; }
    public boolean isAdmin() { return role == Role.ADMIN; }
}
