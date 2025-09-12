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

    @Query("SELECT A FROM User A WHERE A.managerId = ?1")
    List<User> findByManagerID(Long managerId);

    @Query("SELECT u FROM User u WHERE u.managerId = :managerId AND u.userId = :userId")
    Optional<User> checkByManagerIdAndUserId(@Param("managerId") Long managerId, @Param("userId") Long userId);

    // This function is to be depreciated in v2.0
    @Query("SELECT A.userId FROM User A WHERE A.managerId = ?1")
    List<Long> getIdsByManagerId(Long managerId);
}
