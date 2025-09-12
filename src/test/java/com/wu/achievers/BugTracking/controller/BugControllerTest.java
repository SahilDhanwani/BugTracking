package com.wu.achievers.BugTracking.controller;

import com.wu.achievers.BugTracking.entity.Bug;
import com.wu.achievers.BugTracking.service.BugService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.junit.jupiter.api.extension.ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class BugControllerTest {

    private MockMvc mockMvc;
    @Mock
    private BugService bugService;
    @InjectMocks
    private BugController bugController;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bugController).build();
    }

    @Test
    void testGetAllBugs() throws Exception {
        List<Bug> bugs = Arrays.asList(new Bug(), new Bug());
        when(bugService.fetchAllBugs(any(), any(), any(), any(), any(), any())).thenReturn(bugs);
        mockMvc.perform(get("/api/bugs"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBugById_Found() throws Exception {
        Bug bug = new Bug();
        when(bugService.fetchBugById(eq(1L), anyString())).thenReturn(bug);
        mockMvc.perform(get("/api/bugs/1").header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBugById_NotFound() throws Exception {
        when(bugService.fetchBugById(eq(99L), anyString())).thenReturn(null);
        mockMvc.perform(get("/api/bugs/99").header("Authorization", "token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateBug() throws Exception {
        Bug bug = new Bug();
        when(bugService.createBug(any(Bug.class), anyString())).thenReturn(bug);
        mockMvc.perform(post("/api/bugs")
                .header("Authorization", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateBug() throws Exception {
        Bug bug = new Bug();
        when(bugService.updateBug(any(Bug.class), anyString())).thenReturn(bug);
        mockMvc.perform(put("/api/bugs")
                .header("Authorization", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteBug() throws Exception {
        Bug deletedBug = new Bug();
        deletedBug.setBugId(1L);
        deletedBug.setBugTitle("Sample Bug");

        when(bugService.deleteBug(eq(1L), anyString())).thenReturn(deletedBug);

        mockMvc.perform(delete("/api/bugs/1")
                .header("Authorization", "token"))
                .andExpect(status().isOk()) // assuming controller returns 200
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Sample Bug"));
    }
}
