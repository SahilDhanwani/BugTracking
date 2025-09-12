package com.wu.achievers.BugTracking.repository;

import com.wu.achievers.BugTracking.entity.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest   // spins up in-memory DB & configures Spring Data JPA
class ProjectRepoTest {

    @Autowired
    private ProjectRepo projectRepo;

    private Project project1;
    private Project project2;

    @BeforeEach
    void setup() {
        // create sample projects
        project1 = new Project();
        project1.setProjectName("Alpha");
        project1.setManagerID(1L);
        projectRepo.save(project1);

        project2 = new Project();
        project2.setProjectName("Beta");
        project2.setManagerID(2L);
        projectRepo.save(project2);
    }

    @Test
    @DisplayName("findByManagerID should return projects managed by given manager")
    void findByManagerID_returnsMatchingProjects() {
        List<Project> result = projectRepo.findByManagerId(1L);

        assertEquals(1, result.size());
        assertEquals("Alpha", result.get(0).getProjectName());
        assertEquals(1L, result.get(0).getManagerID());
    }

    @Test
    @DisplayName("findByManagerID should return empty list when no project matches")
    void findByManagerID_returnsEmptyList() {
        List<Project> result = projectRepo.findByManagerId(999L);
        assertTrue(result.isEmpty(), "Expected no projects for unknown manager");
    }

    @Test
    @DisplayName("save should persist and assign generated ID")
    void save_assignsGeneratedId() {
        Project p = new Project();
        p.setProjectName("Gamma");
        p.setManagerID(3L);

        Project saved = projectRepo.save(p);
        assertNotNull(saved.getProjectID(), "Generated ID should not be null");
        assertEquals("Gamma", saved.getProjectName());
    }

    @Test
    @DisplayName("findById should retrieve an existing project")
    void findById_returnsExistingProject() {
        Optional<Project> found = projectRepo.findById(project1.getProjectID());
        assertTrue(found.isPresent());
        assertEquals("Alpha", found.get().getProjectName());
    }

    @Test
    @DisplayName("deleteById should remove project")
    void deleteById_removesProject() {
        Long idToDelete = project2.getProjectID();
        projectRepo.deleteById(idToDelete);

        assertFalse(projectRepo.findById(idToDelete).isPresent(),
                "Project should be deleted");
    }

    @Test
    @DisplayName("deleteById does nothing when ID does not exist")
    void deleteById_doesNothingWhenIdNotFound() {
        assertDoesNotThrow(() -> projectRepo.deleteById(999L));
        assertFalse(projectRepo.findById(999L).isPresent());
    }
}
