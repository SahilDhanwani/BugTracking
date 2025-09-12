package com.wu.achievers.BugTracking.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wu.achievers.BugTracking.entity.Bug;
import com.wu.achievers.BugTracking.entity.User;
import com.wu.achievers.BugTracking.repository.BugRepo;
import com.wu.achievers.BugTracking.util.JwtUtil;

@Service
public class BugService {

    @Autowired
    private BugRepo bugRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userServcie;

    public List<Bug> getAllBugs(Long projectId, String status, Long assignedTo, String priority, Date startDate, Date endDate) {

        return bugRepo.searchBugs(projectId, status, assignedTo, priority, startDate, endDate);
    }

    public Bug getBugById(Long id, String token) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserId(token);
        if ("Admin".equals(role)) {
            return bugRepo.findById(id).orElse(null);
        } else if ("Manager".equals(role)) {
            Bug bug = bugRepo.findById(id).orElse(null);
            if (bug != null && bugRepo.findByProjectManagerId(userId).contains(bug)) {
                return bug;
            }
            return null;
        }
        return bugRepo.findById(id).orElse(null);
    }

    public Bug createBug(Bug bug, String token) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserId(token);
        List<User> users = userServcie.getUsersByManagerId(userId, token);
        if ("Admin".equals(role)) {
            return bugRepo.save(bug);
        } else if ("Manager".equals(role)) {
            if (users.stream().anyMatch(u -> u.getUserID().equals(bug.getAssignedTo()))) {
                return bugRepo.save(bug);
            }
        }
        return null;
    }

    public Bug updateBug(Bug bug, String token) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserId(token);
        System.out.println("Update Bug Role: " + role); // Debugging line
        if (bugRepo.existsById(bug.getBugID())) {
            if ("Admin".equals(role)) {
                return bugRepo.save(bug);
            } else if ("Manager".equals(role)) {
                boolean existsOrNot = userServcie.checkUserByManagerIdAndUserId(userId, bug.getAssignedTo());
                System.out.println(existsOrNot);
                if (existsOrNot) {
                    System.out.println("Manager updating bug");
                    return bugRepo.save(bug);
                }

            } else if ("Developer".equals(role) || "Tester".equals(role)) {
                Bug existingBug = bugRepo.findById(bug.getBugID()).orElseThrow();
                if (existingBug != null && existingBug.getAssignedTo().equals(userId)) {
                    existingBug.setStatus(bug.getStatus());
                    existingBug.setEndDate(bug.getEndDate());
                    return bugRepo.save(existingBug);
                }
            }
        }
        return null;
    }

    public Bug deleteBug(Long id, String token) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserId(token);
        if (bugRepo.existsById(id)) {
            Bug bug = bugRepo.findById(id).orElse(null);
            if ("Admin".equals(role)) {
                
                bugRepo.deleteById(id);
                return bug;
            } else if ("Manager".equals(role)) {
                if (bug != null && bugRepo.findByProjectManagerId(userId).contains(bug)) {
                    bugRepo.deleteById(id);
                    return bug;
                }
            }
        }
        return null;
    }

}
