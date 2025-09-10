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
    private Long projectID;

    @Column(name = "ManagerID", nullable = false)
    private Long managerID;

    @Column(name = "ProjectName", length = 100, nullable = false)
    private String projectName;

    @Column(name = "ProjectDescription", columnDefinition = "CLOB")
    private String projectDescription;

    public Project() {
    }

    public Project(Long managerID, String projectName, String projectDescription) {
        this.managerID = managerID;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
    }

    public Project(Long projectID, Long managerID, String projectName, String projectDescription) {
        this.projectID = projectID;
        this.managerID = managerID;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
    }

    // Getters and setters
    public Long getProjectID() {
        return projectID;
    }

    public void setProjectID(Long projectID) {
        this.projectID = projectID;
    }

    public Long getManagerID() {
        return managerID;
    }

    public void setManagerID(Long managerID) {
        this.managerID = managerID;
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
