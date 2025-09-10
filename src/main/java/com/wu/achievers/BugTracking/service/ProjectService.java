package com.wu.achievers.BugTracking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wu.achievers.BugTracking.entity.Project;
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

    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    public Project getProjectById(String token, long id) {
        String role = jwtUtil.extractRole(token);
        Project p = projectRepo.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
        if (p == null) {
            //Project doesn't exists
        }
        else if(role.equals("Developer") || role.equals("Tester")) {
            boolean userExists =  userService.checkUserByManagerID(p.getManagerID(), jwtUtil.extractUserId(token));
            if(!userExists) {
                //User not accessible exception
            }
        }
        else if(role.equals("Manager")) {
            
            if(!p.getManagerID().equals(jwtUtil.extractUserId(token))) {
                //Access exception
                return null;
            }
        }
        return projectRepo.findById(id).orElse(null);
    }

    public Project createProject(Project project) {
        return projectRepo.save(project);
    }

    public Project updateProject(String token, Project project) {
        if (projectRepo.existsById(project.getProjectID())) {
            Project currentProject = projectRepo.findById(project.getProjectID()).orElseThrow(() -> new RuntimeException("Project not found"));
            if(!currentProject.getManagerID().equals(project.getManagerID()) && jwtUtil.extractRole(token).equals("Manager")) {
                //Cannot change the manager exception
                return null;
            }
            return projectRepo.save(project);
        }
        return null;
    }

    public boolean deleteProject(Long id) {
        if (projectRepo.existsById(id)) {
            projectRepo.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Project> getProjectsByManagerId(Long managerId) {

        return projectRepo.findByManagerId(managerId);
    }

    public List<Project> getProjectsByRole(String token) {
        String role = jwtUtil.extractRole(token);
        
        if(role.equals("Admin")) {
            return projectRepo.findAll();
        }
        else if(role.equals("Manager")) {
            return projectRepo.findByManagerId(jwtUtil.extractUserId(token));
        }

        Long managerId = userService.getUserById(jwtUtil.extractUserId(token), token).orElseThrow().getManagerID();
        
        return projectRepo.findByManagerId(managerId);
    }

}
