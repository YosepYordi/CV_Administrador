package com.cvmanager.models;

public class DeletionSummary {
    private long deletedUsers;
    private long deletedGraduates;
    private long deletedCompanies;
    private long deletedCvs;
    private long deletedCvSections;
    private long deletedFavorites;
    private long deletedContactRequests;
    private long deletedPasswordResetTokens;
    private long anonymizedAuditLogs;

    public long getDeletedUsers() { return deletedUsers; }
    public void setDeletedUsers(long deletedUsers) { this.deletedUsers = deletedUsers; }
    public long getDeletedGraduates() { return deletedGraduates; }
    public void setDeletedGraduates(long deletedGraduates) { this.deletedGraduates = deletedGraduates; }
    public long getDeletedCompanies() { return deletedCompanies; }
    public void setDeletedCompanies(long deletedCompanies) { this.deletedCompanies = deletedCompanies; }
    public long getDeletedCvs() { return deletedCvs; }
    public void setDeletedCvs(long deletedCvs) { this.deletedCvs = deletedCvs; }
    public long getDeletedCvSections() { return deletedCvSections; }
    public void setDeletedCvSections(long deletedCvSections) { this.deletedCvSections = deletedCvSections; }
    public long getDeletedFavorites() { return deletedFavorites; }
    public void setDeletedFavorites(long deletedFavorites) { this.deletedFavorites = deletedFavorites; }
    public long getDeletedContactRequests() { return deletedContactRequests; }
    public void setDeletedContactRequests(long deletedContactRequests) { this.deletedContactRequests = deletedContactRequests; }
    public long getDeletedPasswordResetTokens() { return deletedPasswordResetTokens; }
    public void setDeletedPasswordResetTokens(long deletedPasswordResetTokens) { this.deletedPasswordResetTokens = deletedPasswordResetTokens; }
    public long getAnonymizedAuditLogs() { return anonymizedAuditLogs; }
    public void setAnonymizedAuditLogs(long anonymizedAuditLogs) { this.anonymizedAuditLogs = anonymizedAuditLogs; }
}
