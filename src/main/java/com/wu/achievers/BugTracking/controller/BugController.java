package com.wu.achievers.BugTracking.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Bug> getAllBugs(@RequestParam(required = false) Long projectId, @RequestParam(required = false) String status, @RequestParam(required = false) Long assignedTo, @RequestParam(required = false) String priority, @RequestParam(required = false) Date startDate, @RequestParam(required = false) Date endDate , @RequestHeader("Authorization") String token) {
        return bugService.getAllBugs(projectId, status, assignedTo, priority, startDate, endDate, token);
    }

    // @GetMapping("/bugs/{id}")
    // public Bug getBugById(@PathVariable Long id) {
    //     return bugService.getBugById(id);
    // }

    @GetMapping("/bugs/{id}")
public ResponseEntity<Bug> getBugById(@PathVariable Long id, @RequestHeader("Authorization") String token) throws NotFoundException {
    Bug bug = bugService.getBugById(id, token);
    if (bug == null) {
        throw new NotFoundException("Bug with ID " + id + " not found");
    }
    return ResponseEntity.ok(bug);
}


    @PostMapping("/bugs")
    public Bug createBug(@RequestBody Bug bug,  @RequestHeader("Authorization") String token) {
        return bugService.createBug(bug, token);
    }

    @PutMapping("/bugs")
    public Bug updateBug(@RequestBody Bug bug, @RequestHeader("Authorization") String token) {
        System.out.println("Test1");
        return bugService.updateBug(bug, token);
    }

    @DeleteMapping("/bugs/{id}")
    public void deleteBug(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        bugService.deleteBug(id, token);
    }

}
