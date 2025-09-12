package com.wu.achievers.BugTracking.repository;

import com.wu.achievers.BugTracking.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepoTest {

    @Autowired
    private UserRepo userRepo;

    private User createUser(String email, Long managerId) {
        User user = new User();
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail(email);
        user.setPassword("password");
        user.setRole("Admin");
        user.setManagerID(managerId);
        return user;
    }

    @Test
    void testFindById() {
        User user = createUser("findbyid@example.com", 1L);
        userRepo.save(user);
        Optional<User> found = userRepo.findById(user.getUserID());
        assertTrue(found.isPresent());
        assertEquals("findbyid@example.com", found.get().getEmail());
    }

    @Test
    void testSaveUser() {
        User user = createUser("saveuser@example.com", 2L);
        User saved = userRepo.save(user);
        assertNotNull(saved.getUserID());
        assertEquals("saveuser@example.com", saved.getEmail());
    }

    @Test
    void testFindByEmail() {
        User user = createUser("findbyemail@example.com", 3L);
        userRepo.save(user);
        Optional<User> found = userRepo.findByEmail("findbyemail@example.com");
        assertTrue(found.isPresent());
        assertEquals(3L, found.get().getManagerID());
    }

    @Test
    void testFindByManagerID() {
        User user1 = createUser("manager1@example.com", 10L);
        User user2 = createUser("manager2@example.com", 10L);
        userRepo.save(user1);
        userRepo.save(user2);
        List<User> users = userRepo.findByManagerID(10L);
        assertTrue(users.size() >= 2);
    }

    @Test
    void testCheckByManagerIdAndUserId() {
        User user = createUser("checkmanager@example.com", 20L);
        userRepo.save(user);
        User found = userRepo.checkByManagerIdAndUserId(20L, user.getUserID());
        assertNotNull(found);
        assertEquals("checkmanager@example.com", found.getEmail());
    }

    @Test
    void testGetIdsByManagerId() {
        User user = createUser("getids@example.com", 30L);
        userRepo.save(user);
        List<Long> ids = userRepo.getIdsByManagerId(30L);
        assertTrue(ids.contains(user.getUserID()));
    }
}
