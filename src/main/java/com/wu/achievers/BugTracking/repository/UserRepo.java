package com.wu.achievers.BugTracking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wu.achievers.BugTracking.entity.User;
@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT A FROM User A WHERE A.managerID = ?1")
    List<User> findByManagerID(Long managerId);

    @Query("SELECT u FROM User u WHERE u.managerID = :managerId AND u.userID = :userId")
    User checkByManagerIdAndUserId(@Param("managerId") Long managerId, @Param("userId") Long userId);

    @Query("SELECT A.userID FROM User A WHERE A.managerID = ?1")
    List<Long> getIdsByManagerId(Long managerId);
}
