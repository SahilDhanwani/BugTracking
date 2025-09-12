package com.wu.achievers.BugTracking.controller;

import com.wu.achievers.BugTracking.entity.Project;
import com.wu.achievers.BugTracking.service.ProjectService;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wu.achievers.BugTracking.exceptionHandling.GlobalExceptionHandler;

class ProjectControllerTest {

    private MockMvc mockMvc;
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private ProjectController projectController;

    public ProjectControllerTest() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
    }

    @Test
    void testGetAllProjects() throws Exception {
        List<Project> projects = Arrays.asList(new Project(), new Project());
        when(projectService.getProjectsByRole(anyString())).thenReturn(projects);
        mockMvc.perform(get("/api/projects").header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProjectById_Found() throws Exception {
        Project project = new Project();
        when(projectService.getProjectById(anyString(), eq(1L))).thenReturn(project);
        mockMvc.perform(get("/api/projects/1").header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProjectById_NotFound() throws Exception {
        when(projectService.getProjectById(anyString(), eq(99L))).thenReturn(null);
        mockMvc.perform(get("/api/projects/99").header("Authorization", "token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProject() throws Exception {
        Project project = new Project();
        when(projectService.createProject(any(Project.class))).thenReturn(project);
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateProject() throws Exception {
        Project project = new Project();
        when(projectService.updateProject(anyString(), any(Project.class))).thenReturn(project);
        mockMvc.perform(put("/api/projects")
                .header("Authorization", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteProject() throws Exception {
        when(projectService.deleteProject(eq(1L))).thenReturn(true);
        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());
    }
}
