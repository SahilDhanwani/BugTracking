package com.wu.achievers.BugTracking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wu.achievers.BugTracking.entity.Project;
import com.wu.achievers.BugTracking.exceptionHandling.BadRequestException;
import com.wu.achievers.BugTracking.exceptionHandling.NotFoundException;
import com.wu.achievers.BugTracking.repository.ProjectRepo;
import com.wu.achievers.BugTracking.util.JwtUtil;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    public Project getProjectById(String token, long id) {
        String role = jwtUtil.extractRole(token);
        Project p = projectRepo.findById(id).orElse(null);
        if (p == null) {
            throw new NotFoundException("Project with id " + id + " Not found");
        }

        else if(role.equals("Developer") || role.equals("Tester")) {
            boolean userExists =  userService.checkUserByManagerID(p.getManagerID(), jwtUtil.extractUserId(token));
            if(!userExists) {
                throw new BadRequestException("Project with id " + id + " not under your management");
            }
        }
        else if(role.equals("Manager")) {
            
            if(!p.getManagerID().equals(jwtUtil.extractUserId(token))) {
                throw new BadRequestException("Project with id " + id + " not under your management");
            }
        }
        return projectRepo.findById(id).orElse(null);
    }

    public Project createProject(Project project, String token) {
        String role = jwtUtil.extractRole(token);

        if(!role.equals("Admin")) {
            throw new BadRequestException("Only Admin can create project");
        }

        long managerId = project.getManagerID();
        if(!userService.checkUserById(managerId)) {
            throw new NotFoundException("Manager with id " + managerId + " does not exist");
        }
        return projectRepo.save(project);
    }

    public Project updateProject(String token, Project project) {
        if(jwtUtil.extractRole(token).equals("Developer") || jwtUtil.extractRole(token).equals("Tester")) {
            throw new BadRequestException("You cannot update project");
        }

        if (projectRepo.existsById(project.getProjectID())) {
            Project currentProject = projectRepo.findById(project.getProjectID()).orElseThrow(() -> new RuntimeException("Project not found"));
            if(!currentProject.getManagerID().equals(project.getManagerID()) && jwtUtil.extractRole(token).equals("Manager")) {
                throw new BadRequestException("Managers cannot change project manager");

            }
            return projectRepo.save(project);
        }
        throw new NotFoundException("Project with id " + project.getProjectID() + " does not exist");
    }

    public Void deleteProject(Long id, String token) {
        String role = jwtUtil.extractRole(token);
        if (role == null || !"Admin".equals(role)) {
            throw new BadRequestException("Only Admin can delete project");
        }

        if (!projectRepo.existsById(id)) {
            throw new NotFoundException("Project with ID " + id + " does not exist");
        }
        projectRepo.deleteById(id);
        return null;
    }

    public List<Project> getProjectsByManagerId(Long managerId) {
        return projectRepo.findByManagerId(managerId);
    }

    public List<Project> getProjectsByRole(String token) {
        String role = jwtUtil.extractRole(token);
        List<Project> project = null;
        if(role.equals("Admin")) {
            project = projectRepo.findAll();
            if(project.isEmpty()) {
                throw new NotFoundException("No projects found");
            }
            return project;
        }

        else if(role.equals("Manager")) {
            project = projectRepo.findByManagerId(jwtUtil.extractUserId(token));
            if(project.isEmpty()) {
                throw new NotFoundException("No projects found under your management");
            }
            return project;
        }

        Long managerId = userService.getUserById(jwtUtil.extractUserId(token), token).orElseThrow().getManagerID();
        
        project = projectRepo.findByManagerId(managerId); 
        if(project.isEmpty()) {
            throw new NotFoundException("You are not assigned to any project");
        }
        return project;
    }

}
