package com.wu.achievers.BugTracking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wu.achievers.BugTracking.entity.Project;
import com.wu.achievers.BugTracking.exceptionHandling.NotFoundException;
import com.wu.achievers.BugTracking.service.ProjectService;

@RestController
@RequestMapping("/api")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/projects")
    public List<Project> fetchAllProjects(@RequestHeader("Authorization") String token) {
        return projectService.fetchAllProjects(token);
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> fetchProjectById(@PathVariable Long id, @RequestHeader("Authorization") String token) throws NotFoundException {
        Project project = projectService.fetchProjectById(token, id);
        if (project == null) {
            throw new NotFoundException("Project with ID " + id + " not found");
        }
        return ResponseEntity.ok(project);
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> createProject(@RequestBody Project project, @RequestHeader("Authorization") String token) {
        Project newProject = projectService.createProject(project, token);
        return ResponseEntity.ok(newProject);
    }

    @PutMapping("/projects")
    public ResponseEntity<Project> updateProject(@RequestHeader("Authorization") String token, @RequestBody Project project) {
        Project updatedProject = projectService.updateProject(token, project);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> removeProject(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        projectService.removeProject(id, token);
        return ResponseEntity.ok().build();
    }

}
