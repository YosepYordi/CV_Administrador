package com.cvmanager.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ContactRequest implements Serializable {
    public enum Status {
        PENDING("pending", "Pendiente"), ACCEPTED("accepted", "Aceptada"), REJECTED("rejected", "Rechazada");
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

    private Long requestId;
    private Long companyId;
    private Long graduateId;
    private String companyName;
    private String companyEmail;
    private String graduateName;
    private String graduateEmail;
    private String graduatePhone;
    private String message;
    private Status status = Status.PENDING;
    private LocalDateTime createdAt;

    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    public Long getGraduateId() { return graduateId; }
    public void setGraduateId(Long graduateId) { this.graduateId = graduateId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyEmail() { return companyEmail; }
    public void setCompanyEmail(String companyEmail) { this.companyEmail = companyEmail; }
    public String getGraduateName() { return graduateName; }
    public void setGraduateName(String graduateName) { this.graduateName = graduateName; }
    public String getGraduateEmail() { return graduateEmail; }
    public void setGraduateEmail(String graduateEmail) { this.graduateEmail = graduateEmail; }
    public String getGraduatePhone() { return graduatePhone; }
    public void setGraduatePhone(String graduatePhone) { this.graduatePhone = graduatePhone; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status == null ? Status.PENDING : status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
