package com.wu.achievers.BugTracking.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wu.achievers.BugTracking.entity.Bug;
import com.wu.achievers.BugTracking.exceptionHandling.NotFoundException;
import com.wu.achievers.BugTracking.service.BugService;

@RestController
@RequestMapping("/api")
public class BugController {

    @Autowired
    private BugService bugService;

    @GetMapping("/bugs")
    public List<Bug> fetchAllBugs(@RequestParam(required = false) List<Long> projectId, @RequestParam(required = false) String status, @RequestParam(required = false) List<Long> assigneeId, @RequestParam(required = false) String priority, @RequestParam(required = false) Date startDate, @RequestParam(required = false) Date endDate, @RequestHeader("Authorization") String token) {
        return bugService.fetchAllBugs(projectId, status, assigneeId, priority, startDate, endDate, token);
    }

    @GetMapping("/bugs/{id}")
    public ResponseEntity<Bug> fetchBugById(@PathVariable Long id, @RequestHeader("Authorization") String token) throws NotFoundException {
        Bug bug = bugService.fetchBugById(id, token);
        return ResponseEntity.ok(bug);
    }

    @PostMapping("/bugs")
    public ResponseEntity<Bug> createBug(@RequestBody Bug bug, @RequestHeader("Authorization") String token) {
        Bug newBug = bugService.createBug(bug, token);
        return ResponseEntity.ok(newBug);
    }

    @PutMapping("/bugs")
    public ResponseEntity<Bug> updateBug(@RequestBody Bug bug, @RequestHeader("Authorization") String token) {
        Bug updatedBug = bugService.updateBug(bug, token);
        return ResponseEntity.ok(updatedBug);
    }

    @DeleteMapping("/bugs/{id}")
    public ResponseEntity<Bug> deleteBug(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Bug deletedBug = bugService.deleteBug(id, token);
        return ResponseEntity.ok(deletedBug);
    }

}
