package com.cvmanager.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ContactRequest implements Serializable {
    public enum Status {
        PENDING("pending"), ACCEPTED("accepted"), REJECTED("rejected");
        private final String value;
        Status(String value) { this.value = value; }
        public String getValue() { return value; }
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
    private String message;
    private Status status = Status.PENDING;
    private LocalDateTime createdAt;

    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    public Long getGraduateId() { return graduateId; }
    public void setGraduateId(Long graduateId) { this.graduateId = graduateId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status == null ? Status.PENDING : status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
