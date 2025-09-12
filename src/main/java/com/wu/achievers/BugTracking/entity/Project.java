package com.wu.achievers.BugTracking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "Project")

@SequenceGenerator(name = "project_seq", sequenceName = "PROJECT_SEQ", allocationSize = 1)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_seq")
    @Column(name = "ProjectID")
    private Long projectId;

    @Column(name = "ManagerID", nullable = false)
    private Long managerId;

    @Column(name = "ProjectName", length = 100, nullable = false)
    private String projectName;

    @Column(name = "ProjectDescription", columnDefinition = "CLOB")
    private String projectDescription;

    public Project() {
    }

    public Project(Long managerId, String projectName, String projectDescription) {
        this.managerId = managerId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
    }

    public Project(Long projectId, Long managerId, String projectName, String projectDescription) {
        this.projectId = projectId;
        this.managerId = managerId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
    }

    // Getters and setters
    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public Project orElseThrow(Object object) {
        throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
    }
}
