package com.wu.achievers.BugTracking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.wu.achievers.BugTracking.entity.Project;

@Repository
public interface ProjectRepo extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE p.managerID=?1")
    List<Project> findByManagerId(Long managerId);

}
