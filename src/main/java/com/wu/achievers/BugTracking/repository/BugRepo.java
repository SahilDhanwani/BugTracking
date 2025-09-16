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

    @Query("SELECT b FROM Bug b " +
       "WHERE (:projectId IS NULL OR b.projectId IN :projectId) " +
       "AND (:status IS NULL OR b.status = :status) " +
       "AND (:assigneeIds IS NULL OR b.assigneeId IN :assigneeIds) " +
       "AND (:priority IS NULL OR b.priority = :priority) " +
       "AND (:startDate IS NULL OR b.startDate >= :startDate) " +
       "AND (:endDate IS NULL OR b.endDate <= :endDate)")
    List<Bug> searchBugs(@Param("projectId") List<Long> projectId,
                     @Param("status") String status,
                     @Param("assigneeIds") List<Long> assigneeIds,
                     @Param("priority") String priority,
                     @Param("startDate") Date startDate,
                     @Param("endDate") Date endDate);

    @Query("SELECT b FROM Bug b WHERE b.assigneeId = ?1")
    List<Bug> findByAssigneeId(Long assigneeId);

    @Query("SELECT b FROM Bug b WHERE b.projectId IN (SELECT p.projectId FROM Project p WHERE p.managerId = ?1)")
    List<Bug> findByProjectManagerId(Long managerId);
}
