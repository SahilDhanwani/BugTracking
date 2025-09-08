package com.wu.achievers.BugTracking.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wu.achievers.BugTracking.entity.Bug;
import com.wu.achievers.BugTracking.repository.BugRepo;

@Service
public class BugService {

    @Autowired
    private BugRepo bugRepo;

    public List<Bug> getAllBugs(Long projectId, String status, Long assigneeId, String priority, Date startDate, Date endDate) {

        return bugRepo.searchBugs(projectId, status, assigneeId, priority, startDate, endDate);
    }

    public Bug getBugById(Long id) {
        return bugRepo.findById(id).orElse(null);
    }

    public Bug createBug(Bug bug) {
        return bugRepo.save(bug);
    }

    public Bug updateBug(Bug bug) {
        if (bugRepo.existsById(bug.getBugID())) {
            return bugRepo.save(bug);
        }
        return null;
    }

    public boolean deleteBug(Long id) {
        if (bugRepo.existsById(id)) {
            bugRepo.deleteById(id);
            return true;
        }
        return false;
    }

}
