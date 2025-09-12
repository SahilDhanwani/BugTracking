package com.wu.achievers.BugTracking.service;

import com.wu.achievers.BugTracking.entity.Project;
import com.wu.achievers.BugTracking.repository.ProjectRepo;
import com.wu.achievers.BugTracking.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepo projectRepo;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserService userService;
    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProjects() {
        List<Project> projects = Arrays.asList(new Project(), new Project());
        when(projectRepo.findAll()).thenReturn(projects);
        List<Project> result = projectService.getAllProjects();
        assertEquals(2, result.size());
    }

    @Test
    void testGetProjectById_Found() {
        Project project = new Project();
        project.setManagerID(1L); // <-- set it directly

        when(jwtUtil.extractRole(anyString())).thenReturn("Manager");
        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(jwtUtil.extractUserId(anyString())).thenReturn(1L);

        Project result = projectService.getProjectById("token", 1L);
        assertNotNull(result);
        assertEquals(1L, result.getManagerID());
    }


    @Test
    void testGetProjectById_NotFound() {
        when(projectRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> projectService.getProjectById("token", 99L));
    }

    @Test
    void testCreateProject() {
        Project project = new Project();
        when(projectRepo.save(any(Project.class))).thenReturn(project);
        Project result = projectService.createProject(project);
        assertNotNull(result);
    }

    @Test
    void testUpdateProject() {
        Project project = new Project();
        project.setProjectID(1L);      
        project.setManagerID(1L);      

        when(projectRepo.existsById(1L)).thenReturn(true);
        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepo.save(any(Project.class))).thenReturn(project);

        when(jwtUtil.extractRole(anyString())).thenReturn("Manager");

        Project result = projectService.updateProject("token", project);
        assertNotNull(result);   
    }


}
