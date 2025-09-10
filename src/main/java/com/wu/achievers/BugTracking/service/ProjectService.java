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

    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    public Project getProjectById(String token, long id) {
        String role = jwtUtil.extractRole(token);

        if(role.equals("Developer") || role.equals("Tester")) {
            Project p = projectRepo.findForUsers(jwtUtil.extractUserId(token), id);
        }
        else if(role.equals("Manager")) {
            Project p = projectRepo.findByManagerId(jwtUtil.extractUserId(token), id);
        }
        return projectRepo.findById(id).orElse(null);
    }

    public Project createProject(Project project) {
        return projectRepo.save(project);
    }

    public Project updateProject(Project project) {
        if (projectRepo.existsById(project.getProjectID())) {
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
        // TODO Auto-generated method stub
        String role = jwtUtil.extractRole(token);
        if(role.equals("Admin")) {
            return projectRepo.findAll();
        }
        // else if(role.equals("Manager") || role.equals("Developer") || role.equals("Tester")) {
        //     return projectRepo.findByManagerId(jwtUtil.extractUserId(token));
        // }
        
        return projectRepo.findByManagerId(jwtUtil.extractUserId(token));
    }

}
