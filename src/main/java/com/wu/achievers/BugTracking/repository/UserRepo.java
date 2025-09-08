package com.wu.achievers.BugTracking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wu.achievers.BugTracking.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    // Find user by email for authentication
    java.util.Optional<com.wu.achievers.BugTracking.entity.User> findByEmail(String email);

    @Query("SELECT A FROM User A INNER JOIN User B ON A.managerID = B.userID WHERE A.managerID = ?1")
    List<User> findByManagerID(Long managerId);

    @Query("SELECT u FROM User u WHERE u.managerID = :managerId AND u.userID = :userId")
    java.util.Optional<User> findUserByManager(@Param("managerId") Long managerId, @Param("userId") Long userId);
}
