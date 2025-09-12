package com.wu.achievers.BugTracking.service;

import com.wu.achievers.BugTracking.entity.User;
import com.wu.achievers.BugTracking.repository.UserRepo;
import com.wu.achievers.BugTracking.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private UserRepo userRepo;

    @Mock
    private JwtUtil jwtUtil;


    @InjectMocks
    private UserService userService;

    @Test
    void testGetUserById_Admin() {
        User user = new User(1L, "Test", "User", "admin@example.com", "pass", "Admin", null);
        Mockito.when(jwtUtil.extractRole(anyString())).thenReturn("Admin");
        Mockito.when(jwtUtil.extractUserId(anyString())).thenReturn(1L);
        Mockito.when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        Optional<User> found = userService.getUserById(1L, "token");
        assertTrue(found.isPresent());
        assertEquals("admin@example.com", found.get().getEmail());
    }

    @Test
    void testSignup_NewUser() {
    User user = new User(null, "Test", "User", "newuser@example.com", "pass", "Admin", null);
    Mockito.when(userRepo.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
    Mockito.when(userRepo.save(any(User.class))).thenReturn(user);
    Mockito.when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    User created = userService.signup(user);
    assertNotNull(created);
    assertEquals("newuser@example.com", created.getEmail());
    }

    @Test
    void testSignup_ExistingUser() {
        User user = new User(null, "Test", "User", "existing@example.com", "pass", "Admin", null);
        Mockito.when(userRepo.findByEmail("existing@example.com")).thenReturn(Optional.of(user));
        User created = userService.signup(user);
        assertNull(created);
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(1L);
        Mockito.verify(userRepo, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void testFindByEmail() {
        User user = new User(2L, "Test", "User", "findbyemail@example.com", "pass", "Admin", null);
        Mockito.when(userRepo.findByEmail("findbyemail@example.com")).thenReturn(Optional.of(user));
        Optional<User> found = userService.findByEmail("findbyemail@example.com");
        assertTrue(found.isPresent());
        assertEquals(2L, found.get().getUserID());
    }
}
