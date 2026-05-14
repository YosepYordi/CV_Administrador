package com.cvmanager.models;

import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardStats {
    private long totalUsers;
    private long totalGraduates;
    private long totalCompanies;
    private long totalCvs;
    private long totalPublishedCvs;
    private long draftCvs;
    private long activeCareers;
    private long totalViews;
    private long totalFavorites;
    private long totalContactRequests;
    private long pendingContactRequests;
    private long acceptedContactRequests;
    private long rejectedContactRequests;
    private long totalSearches;
    private Map<String, Long> roleCounts = new LinkedHashMap<>();
    private Map<String, Long> statusCounts = new LinkedHashMap<>();
    private Map<String, Long> graduatesByCareer = new LinkedHashMap<>();
    private Map<String, Long> contactRequestsByStatus = new LinkedHashMap<>();

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalGraduates() { return totalGraduates; }
    public void setTotalGraduates(long totalGraduates) { this.totalGraduates = totalGraduates; }
    public long getTotalCompanies() { return totalCompanies; }
    public void setTotalCompanies(long totalCompanies) { this.totalCompanies = totalCompanies; }
    public long getTotalCvs() { return totalCvs; }
    public void setTotalCvs(long totalCvs) { this.totalCvs = totalCvs; }
    public long getTotalPublishedCvs() { return totalPublishedCvs; }
    public void setTotalPublishedCvs(long totalPublishedCvs) { this.totalPublishedCvs = totalPublishedCvs; }
    public long getDraftCvs() { return draftCvs; }
    public void setDraftCvs(long draftCvs) { this.draftCvs = draftCvs; }
    public long getActiveCareers() { return activeCareers; }
    public void setActiveCareers(long activeCareers) { this.activeCareers = activeCareers; }
    public long getTotalViews() { return totalViews; }
    public void setTotalViews(long totalViews) { this.totalViews = totalViews; }
    public long getTotalFavorites() { return totalFavorites; }
    public void setTotalFavorites(long totalFavorites) { this.totalFavorites = totalFavorites; }
    public long getTotalContactRequests() { return totalContactRequests; }
    public void setTotalContactRequests(long totalContactRequests) { this.totalContactRequests = totalContactRequests; }
    public long getPendingContactRequests() { return pendingContactRequests; }
    public void setPendingContactRequests(long pendingContactRequests) { this.pendingContactRequests = pendingContactRequests; }
    public long getAcceptedContactRequests() { return acceptedContactRequests; }
    public void setAcceptedContactRequests(long acceptedContactRequests) { this.acceptedContactRequests = acceptedContactRequests; }
    public long getRejectedContactRequests() { return rejectedContactRequests; }
    public void setRejectedContactRequests(long rejectedContactRequests) { this.rejectedContactRequests = rejectedContactRequests; }
    public long getTotalSearches() { return totalSearches; }
    public void setTotalSearches(long totalSearches) { this.totalSearches = totalSearches; }
    public Map<String, Long> getRoleCounts() { return roleCounts; }
    public void setRoleCounts(Map<String, Long> roleCounts) { this.roleCounts = roleCounts; }
    public Map<String, Long> getRoleCountsDisplay() { return labelsForRoles(roleCounts); }
    public Map<String, Long> getStatusCounts() { return statusCounts; }
    public void setStatusCounts(Map<String, Long> statusCounts) { this.statusCounts = statusCounts; }
    public Map<String, Long> getStatusCountsDisplay() { return labelsForUserStatuses(statusCounts); }
    public Map<String, Long> getGraduatesByCareer() { return graduatesByCareer; }
    public void setGraduatesByCareer(Map<String, Long> graduatesByCareer) { this.graduatesByCareer = graduatesByCareer; }
    public Map<String, Long> getContactRequestsByStatus() { return contactRequestsByStatus; }
    public void setContactRequestsByStatus(Map<String, Long> contactRequestsByStatus) { this.contactRequestsByStatus = contactRequestsByStatus; }
    public Map<String, Long> getContactRequestsByStatusDisplay() { return labelsForContactRequestStatuses(contactRequestsByStatus); }

    private Map<String, Long> labelsForRoles(Map<String, Long> values) {
        Map<String, Long> labels = new LinkedHashMap<>();
        if (values == null) return labels;
        for (Map.Entry<String, Long> entry : values.entrySet()) {
            labels.put(User.Role.from(entry.getKey()).getLabel(), entry.getValue());
        }
        return labels;
    }

    private Map<String, Long> labelsForUserStatuses(Map<String, Long> values) {
        Map<String, Long> labels = new LinkedHashMap<>();
        if (values == null) return labels;
        for (Map.Entry<String, Long> entry : values.entrySet()) {
            labels.put(User.Status.from(entry.getKey()).getLabel(), entry.getValue());
        }
        return labels;
    }

    private Map<String, Long> labelsForContactRequestStatuses(Map<String, Long> values) {
        Map<String, Long> labels = new LinkedHashMap<>();
        if (values == null) return labels;
        for (Map.Entry<String, Long> entry : values.entrySet()) {
            labels.put(ContactRequest.Status.from(entry.getKey()).getLabel(), entry.getValue());
        }
        return labels;
    }
}
