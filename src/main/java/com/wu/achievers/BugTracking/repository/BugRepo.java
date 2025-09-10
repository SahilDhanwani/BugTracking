package com.wu.achievers.BugTracking.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wu.achievers.BugTracking.entity.Bug;

@Repository
public interface BugRepo extends JpaRepository<Bug, Long> {

    @Query("SELECT b FROM Bug b WHERE (:projectId IS NULL OR b.projectID = ?1) AND (:status IS NULL OR b.status = ?2) AND (:assigneeId IS NULL OR b.assignedTo = ?3) AND (:priority IS NULL OR b.priority = ?4) AND (:startDate IS NULL OR b.startDate >= ?5) AND (:endDate IS NULL OR b.endDate <= ?6)")
    List<Bug> searchBugs(@Param("projectId") Long projectId,
            @Param("status") String status,
            @Param("assigneeId") Long assigneeId,
            @Param("priority") String priority,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query("SELECT b FROM Bug b WHERE b.assignedTo = ?1")
    List<Bug> findByAssigneeId(Long assignedTo);

    @Query("SELECT b FROM Bug b WHERE b.projectID IN (SELECT p.projectID FROM Project p WHERE p.managerID = ?1)")
    List<Bug> findByProjectManagerId(Long managerId);
}
