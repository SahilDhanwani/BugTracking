package com.wu.achievers.BugTracking.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wu.achievers.BugTracking.entity.Bug;
import com.wu.achievers.BugTracking.entity.User;
import com.wu.achievers.BugTracking.exceptionHandling.NotFoundException;
import com.wu.achievers.BugTracking.repository.BugRepo;
import com.wu.achievers.BugTracking.util.JwtUtil;

@Service
public class BugService {

    @Autowired
    private BugRepo bugRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    public List<Bug> fetchAllBugs(Long projectId, String status, Long assigneeId, String priority, Date startDate, Date endDate) {
        return bugRepository.searchBugs(projectId, status, assigneeId, priority, startDate, endDate);
    }

    public Bug fetchBugById(Long bugId, String jwtToken) {
        String userRole = jwtUtil.extractRole(jwtToken);
        Long userId = jwtUtil.extractUserId(jwtToken);
        Bug bug = bugRepository.findById(bugId).orElse(null);
        if (bug == null) {
            throw new NotFoundException("Bug with id " + bugId + " not found");
        }
        if (userRole != null) {
            switch (userRole) {
                case "Admin" -> {
                    return bug;
                }
                case "Manager" -> {
                    if (bugRepository.findByProjectManagerId(userId).contains(bug)) {
                        return bug;
                    }
                    throw new NotFoundException("Bug with id " + bugId + " not found or not under your management");
                }
                case "Developer", "Tester" -> {
                    if (!bug.getAssigneeId().equals(userId)) {
                        throw new NotFoundException("Bug with id " + bugId + " not found or not assigned to you");
                    }
                }
                default -> {
                }
            }
        }
        return bug;
    }

    public Bug createBug(Bug bug, String jwtToken) {
        String userRole = jwtUtil.extractRole(jwtToken);
        Long userId = jwtUtil.extractUserId(jwtToken);
        List<User> managedUsers = userService.fetchUsersByManagerId(userId, jwtToken);
        if ("Admin".equals(userRole)) {
            return bugRepository.save(bug);
        } else if ("Manager".equals(userRole)) {
            if (managedUsers.stream().anyMatch(u -> u.getUserId().equals(bug.getAssigneeId()))) {
                return bugRepository.save(bug);
            }
            throw new NotFoundException("Cannot assign bug to user not under your management");
        }
        throw new NotFoundException("Only Admin or Manager can create bugs");
    }

    public Bug updateBug(Bug bug, String jwtToken) {
        String userRole = jwtUtil.extractRole(jwtToken);
        Long userId = jwtUtil.extractUserId(jwtToken);
        Bug existingBug = bugRepository.findById(bug.getBugId()).orElseThrow();
        if (userRole != null) {
            switch (userRole) {
                case "Admin" -> {
                    return bugRepository.save(bug);
                }
                case "Manager" -> {
                    // Update all fields except bugID and projectID
                    existingBug.setEnvironment(bug.getEnvironment());
                    existingBug.setBugDescription(bug.getBugDescription());
                    existingBug.setPriority(bug.getPriority());
                    existingBug.setStartDate(bug.getStartDate());
                    existingBug.setStatus(bug.getStatus());
                    existingBug.setAssigneeId(bug.getAssigneeId());
                    boolean isManagedUser = userService.checkUserByManagerIdAndUserId(userId, bug.getAssigneeId());
                    if (isManagedUser) {
                        return bugRepository.save(bug);
                    }
                    throw new NotFoundException("Cannot assign bug to user not under your management");
                }
                case "Developer", "Tester" -> {
                    if (existingBug != null && existingBug.getAssigneeId().equals(userId)) {
                        existingBug.setStatus(bug.getStatus());
                        existingBug.setEndDate(bug.getEndDate());
                        return bugRepository.save(existingBug);
                    }
                    throw new NotFoundException("Cannot update bug not assigned to you");
                }
                default -> {
                }
            }
        }
        throw new NotFoundException("Bug not found");
    }

    public Bug deleteBug(Long bugId, String jwtToken) {
        String userRole = jwtUtil.extractRole(jwtToken);
        Long userId = jwtUtil.extractUserId(jwtToken);
        if (bugRepository.existsById(bugId)) {
            Bug bug = bugRepository.findById(bugId).orElse(null);
            if ("Admin".equals(userRole)) {
                bugRepository.deleteById(bugId);
                return bug;
            } else if ("Manager".equals(userRole)) {
                if (bug != null && bugRepository.findByProjectManagerId(userId).contains(bug)) {
                    bugRepository.deleteById(bugId);
                    return bug;
                }
                throw new NotFoundException("Bug with id " + bugId + " not found or not under your management");
            }
        }
        throw new NotFoundException("Bug not found");
    }
}
