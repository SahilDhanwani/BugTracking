package com.wu.achievers.BugTracking.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;

@Entity
@Table(name = "Bug")

@SequenceGenerator(name = "bug_seq", sequenceName = "BUG_SEQ", allocationSize = 1)
public class Bug {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bug_seq")
    @Column(name = "BugID")
    private Long bugId;

    @Column(name = "ProjectID", nullable = false)
    private Long projectId;

    @Column(name = "AssignedTo", nullable = false)
    private Long assigneeId;

    @Column(name = "Environment", length = 100)
    private String environment;

    @Column(name = "BugTitle", length = 100, nullable = false)
    private String bugTitle;

    @Column(name = "BugDescription", columnDefinition = "CLOB")
    private String bugDescription;

    @Column(name = "Priority", length = 20)
    private String priority;

    @Column(name = "Status", length = 20)
    private String status;

    @Column(name = "StartDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private java.util.Date startDate;

    @Column(name = "EndDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private java.util.Date endDate;

    public Bug() {
    }

    public Bug(Long bugId, Long projectId, Long assigneeId, String environment, String bugTitle, String bugDescription, String priority, String status, java.util.Date startDate, java.util.Date endDate) {
        this.bugId = bugId;
        this.projectId = projectId;
        this.assigneeId = assigneeId;
        this.environment = environment;
        this.bugTitle = bugTitle;
        this.bugDescription = bugDescription;
        this.priority = priority;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public Long getBugId() {
        return bugId;
    }

    public void setBugId(Long bugId) {
        this.bugId = bugId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getBugTitle() {
        return bugTitle;
    }

    public void setBugTitle(String bugTitle) {
        this.bugTitle = bugTitle;
    }

    public String getBugDescription() {
        return bugDescription;
    }

    public void setBugDescription(String bugDescription) {
        this.bugDescription = bugDescription;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    public java.util.Date getEndDate() {
        return endDate;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }
}
