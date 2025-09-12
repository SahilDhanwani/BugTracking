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
        Bug bug = bugRepo.findById(id).orElse(null);
        if(bug == null) {
            throw new NotFoundException("Bug with id " + id + " Not found");
        }
        
        if ("Admin".equals(role)) {
            return bug;

        } else if("Manager".equals(role)) {
            if(bug != null && bugRepo.findByProjectManagerId(userId).contains(bug)) {
                return bug;
            }
            throw new NotFoundException("Bug with id " + id + " Not found or not under your management");
        }
        else if("Developer".equals(role) || "Tester".equals(role)) {
            if(bug == null || !bug.getAssignedTo().equals(userId)) {
                throw new NotFoundException("Bug with id " + id + " Not found or not assigned to you");
            }
        } 
        return bug;
    }

    public Bug createBug(Bug bug, String token) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserId(token);
        List<User> users = userServcie.getUsersByManagerId(userId, token);
        if ("Admin".equals(role)) {
            return bugRepo.save(bug);
        } else if("Manager".equals(role)) {
            if(users.stream().anyMatch(u -> u.getUserID().equals(bug.getAssignedTo()))) {
                return bugRepo.save(bug);
            }

            throw new NotFoundException("Cannot assign bug to user not under your management");
        }

        throw new NotFoundException("Only Admin or Manager can create bugs");
    }

    public Bug updateBug(Bug bug, String token) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserId(token);
        if (bugRepo.existsById(bug.getBugID())) {
            Bug existingBug = bugRepo.findById(bug.getBugID()).orElseThrow();
            if("Admin".equals(role)) {
                return bugRepo.save(bug);
            } else if("Manager".equals(role)) {
                // Update all fields except bugID and projectID
                existingBug.setEnvironment(bug.getEnvironment());
                existingBug.setBugDescription(bug.getBugDescription());
                existingBug.setPriority(bug.getPriority());
                existingBug.setStartDate(bug.getStartDate());
                existingBug.setStatus(bug.getStatus());
                existingBug.setAssignedTo(bug.getAssignedTo());

                boolean existsOrNot = userServcie.checkUserByManagerIdAndUserId(userId, bug.getAssignedTo());
                if(existsOrNot) {

                    return bugRepo.save(bug);
                }
                throw new NotFoundException("Cannot assign bug to user not under your management");
                
            } else if("Developer".equals(role) || "Tester".equals(role)) {
                if(existingBug != null && existingBug.getAssignedTo().equals(userId)) {
                    existingBug.setStatus(bug.getStatus());
                    existingBug.setEndDate(bug.getEndDate());
                    return bugRepo.save(existingBug);
                }
                throw new NotFoundException("Cannot update bug not assigned to you");
            }
        }
        throw new NotFoundException("Bug not found");
    }

    public Bug deleteBug(Long id, String token) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserId(token);
        if (bugRepo.existsById(id)) {
            if("Admin".equals(role)) {
                Bug bug = bugRepo.findById(id).orElse(null);
                bugRepo.deleteById(id);  
                return bug;
                
            } else if("Manager".equals(role)) {
                Bug bug = bugRepo.findById(id).orElse(null);
                if(bug != null && bugRepo.findByProjectManagerId(userId).contains(bug)) {
                    bugRepo.deleteById(id);
                    return bug;
                }
                throw new NotFoundException("Bug with id " + id + " not found or not under your management");
            }
        }
        throw new NotFoundException("Bug not found");
    }

}
