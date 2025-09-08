package com.wu.achievers.BugTracking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wu.achievers.BugTracking.entity.Project;
import com.wu.achievers.BugTracking.entity.User;
import com.wu.achievers.BugTracking.repository.ProjectRepo;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepo projectRepo;

    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    public Project getProjectById(Long id) {
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

    public List<Project> getProjectsByRole(User user) {
        // TODO Auto-generated method stub
        if(user.getRole().equals("Admin")) {
            return projectRepo.findAll();
        }
        else if(user.getRole().equals("Manager") || user.getRole().equals("Developer") || user.getRole().equals("Tester")) {
            return projectRepo.findByManagerId(user.getUserID());
        }
        return List.of();
    }

}
