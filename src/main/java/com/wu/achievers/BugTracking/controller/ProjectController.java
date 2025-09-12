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

    // @GetMapping("/projects")
    // public List<Project> getAllProjects(@RequestHeader("Authorization") String token) {
    //     return projectService.getProjectsByRole(token);
    // }
   @GetMapping("/projects")
public List<Project> getAllProjects(@RequestHeader("Authorization") String token) {
    //System.out.println("Authorization header: " + token);
    if (token.startsWith("Bearer ")) {
        token = token.substring(7);
    }
    //System.out.println("Token after strip: " + token);
    return projectService.getProjectsByRole(token);
}

    // @GetMapping("/projects/{id}")
    // public Project getProjectById(@PathVariable Long id) throws NotFoundException{
    //     return projectService.getProjectById(id);
    // }
    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id, @RequestHeader("Authorization") String token) throws NotFoundException {
        Project project = projectService.getProjectById(token, id);
        if (project == null) {
            throw new NotFoundException("Project with ID " + id + " not found");
        }
        return ResponseEntity.ok(project);
    }

    @PostMapping("/projects")

    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    @PutMapping("/projects")
    public Project updateProject(@RequestHeader("Authorization") String token, @RequestBody Project project) {
        return projectService.updateProject(token, project);
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        if (projectService.deleteProject(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
