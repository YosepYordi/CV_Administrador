package com.cvmanager.models;

public class SearchCriteria {
    private String career;
    private String skill;
    private String city;
    private String language;
    private String keyword;
    private Integer minExperience;
    private int page = 1;
    private int pageSize = 10;

    public String getCareer() { return career; }
    public void setCareer(String career) { this.career = career; }
    public String getSkill() { return skill; }
    public void setSkill(String skill) { this.skill = skill; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public Integer getMinExperience() { return minExperience; }
    public void setMinExperience(Integer minExperience) { this.minExperience = minExperience; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = Math.max(1, page); }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = Math.max(1, Math.min(50, pageSize)); }
}
