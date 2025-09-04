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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wu.achievers.BugTracking.entity.Bug;
import com.wu.achievers.BugTracking.service.BugService;

@RestController
@RequestMapping("/api")
public class BugController {

    @Autowired
    private BugService bugService;

    @GetMapping("/bugs")
    public List<Bug> getAllBugs(@RequestParam(required = false) Long projectId, @RequestParam(required = false) String status, @RequestParam(required = false) Long assignedTo, @RequestParam(required = false) String priority, @RequestParam(required = false) Date startDate, @RequestParam(required = false) Date endDate) {
        
        return bugService.getAllBugs(projectId, status, assignedTo, priority, startDate, endDate);
    }

    @GetMapping("/bugs/{id}")
    public Bug getBugById(@PathVariable Long id) {
        return bugService.getBugById(id);
    }

    @PostMapping("/bugs")
    public Bug createBug(@RequestBody Bug bug) {
        return bugService.createBug(bug);
    }

    @PutMapping("/bugs")
    public Bug updateBug(@RequestBody Bug bug) {
        return bugService.updateBug(bug);
    }

    @DeleteMapping("/bugs/{id}")
    public ResponseEntity<Void> deleteBug(@PathVariable Long id) {
        if (bugService.deleteBug(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
