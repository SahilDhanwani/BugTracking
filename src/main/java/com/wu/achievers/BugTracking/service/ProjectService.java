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

    public List<Project> fetchAllProjects(String token) {
        String role = jwtUtil.extractRole(token);
        List<Project> project;
        if (role.equals("Admin")) {
            project = projectRepository.findAll();
            if (project.isEmpty()) {
                throw new NotFoundException("No projects found");
            }
            return project;
        } else if (role.equals("Manager")) {
            project = projectRepository.findByManagerId(jwtUtil.extractUserId(token));
            if (project.isEmpty()) {
                throw new NotFoundException("No projects found under your management");
            }
            return project;
        }

        Long managerId = userService.fetchUserById(jwtUtil.extractUserId(token), token).getManagerId();

        project = projectRepository.findByManagerId(managerId);
        if (project.isEmpty()) {
            throw new NotFoundException("You are not assigned to any project");
        }
        return project;
    }

    public void removeProject(Long projectId, String jwtToken) {
        String userRole = jwtUtil.extractRole(jwtToken);
        if (userRole == null || !userRole.equals("Admin")) {
            throw new BadRequestException("Only Admin can delete project");
        }
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException("Project with ID " + projectId + " does not exist");
        }
        projectRepository.deleteById(projectId);
    }

    @Autowired
    private ProjectRepo projectRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    public Project fetchProjectById(String jwtToken, long projectId) {
        String userRole = jwtUtil.extractRole(jwtToken);
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            throw new NotFoundException("Project with id " + projectId + " not found");
        }
        if (userRole.equals("Developer") || userRole.equals("Tester")) {
            boolean userExists = userService.existsUserByManagerId(project.getManagerId(), jwtUtil.extractUserId(jwtToken));
            if (!userExists) {
                throw new BadRequestException("Project with id " + projectId + " not under your management");
            }
        } else if (userRole.equals("Manager")) {
            if (!project.getManagerId().equals(jwtUtil.extractUserId(jwtToken))) {
                throw new BadRequestException("Project with id " + projectId + " not under your management");
            }
        }
        return projectRepository.findById(projectId).orElse(null);
    }

    public Project createProject(Project project, String jwtToken) {
        String userRole = jwtUtil.extractRole(jwtToken);
        if (!userRole.equals("Admin")) {
            throw new BadRequestException("Only Admin can create project");
        }
        long managerId = project.getManagerId();
        if (!userService.existsUserById(managerId)) {
            throw new NotFoundException("Manager with id " + managerId + " does not exist");
        }
        return projectRepository.save(project);
    }

    public Project updateProject(String jwtToken, Project project) {
        String userRole = jwtUtil.extractRole(jwtToken);
        if (userRole.equals("Developer") || userRole.equals("Tester")) {
            throw new BadRequestException("You cannot update project");
        }
        if (projectRepository.existsById(project.getProjectId())) {
            Project currentProject = projectRepository.findById(project.getProjectId()).orElseThrow(() -> new RuntimeException("Project not found"));
            if (!currentProject.getManagerId().equals(project.getManagerId()) && userRole.equals("Manager")) {
                throw new BadRequestException("Manager cannot change project ownership");
            }
            return projectRepository.save(project);
        }
        throw new NotFoundException("Project with id " + project.getProjectId() + " does not exist");
    }

    public Void deleteProject(Long projectId, String jwtToken) {
        String userRole = jwtUtil.extractRole(jwtToken);
        if (userRole == null || !userRole.equals("Admin")) {
            throw new BadRequestException("Only Admin can delete project");
        }
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException("Project with ID " + projectId + " does not exist");
        }
        projectRepository.deleteById(projectId);
        return null;
    }

    public List<Project> fetchProjectsByManagerId(Long managerId) {
        return projectRepository.findByManagerId(managerId);
    }

    
}
