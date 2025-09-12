package com.wu.achievers.BugTracking.service;

import com.wu.achievers.BugTracking.entity.Bug;
import com.wu.achievers.BugTracking.repository.BugRepo;
import com.wu.achievers.BugTracking.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.Optional;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BugServiceTest {

    @Mock
    private BugRepo bugRepo;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private BugService bugService;


    @Test
    void testGetAllBugs() {
        List<Bug> bugs = Arrays.asList(new Bug(), new Bug());
        when(bugRepo.searchBugs(any(), any(), any(), any(), any(), any())).thenReturn(bugs);
    List<Bug> result = bugService.fetchAllBugs(null, null, null, null, null, null);
        assertEquals(2, result.size());
    }

    @Test
    void testGetBugById_Found() {
        Bug bug = new Bug();
        when(jwtUtil.extractRole(anyString())).thenReturn("Admin");
        when(bugRepo.findById(1L)).thenReturn(Optional.of(bug));
    Bug result = bugService.fetchBugById(1L, "token");
        assertNotNull(result);
    }

    @Test
    void testGetBugById_NotFound() {
        when(bugRepo.findById(99L)).thenReturn(Optional.empty());
    Bug result = bugService.fetchBugById(99L, "token");
        assertNull(result);
    }

    @Test
    void testCreateBug_Admin() {
        Bug bug = new Bug();
        when(jwtUtil.extractRole(anyString())).thenReturn("Admin");
        when(jwtUtil.extractUserId(anyString())).thenReturn(1L);
        when(bugRepo.save(any(Bug.class))).thenReturn(bug);
        Bug result = bugService.createBug(bug, "token");
        assertNotNull(result);
    }

    @Test
    void testUpdateBug() {
        Bug bug = new Bug();
        when(bugRepo.save(any(Bug.class))).thenReturn(bug);
        Bug result = bugService.updateBug(bug, "token");
        assertNotNull(result);
    }
}
