package com.ashcollege.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PrenatalTest {
    private Long id;
    private Integer weekFrom;
    private Integer weekTo;
    private Short trimester;
    private String title;
    private String purpose;
    private String howItDone;
    private Boolean recommended;
    private Boolean mandatory;
    private String notes;

    public PrenatalTest() {}

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getWeekFrom() { return weekFrom; }
    public void setWeekFrom(Integer weekFrom) { this.weekFrom = weekFrom; }

    public Integer getWeekTo() { return weekTo; }
    public void setWeekTo(Integer weekTo) { this.weekTo = weekTo; }

    public Short getTrimester() { return trimester; }
    public void setTrimester(Short trimester) { this.trimester = trimester; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getHowItDone() { return howItDone; }
    public void setHowItDone(String howItDone) { this.howItDone = howItDone; }

    public Boolean getRecommended() { return recommended; }
    public void setRecommended(Boolean recommended) { this.recommended = recommended; }

    public Boolean getMandatory() { return mandatory; }
    public void setMandatory(Boolean mandatory) { this.mandatory = mandatory; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
