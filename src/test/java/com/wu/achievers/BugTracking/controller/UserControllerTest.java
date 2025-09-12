package com.wu.achievers.BugTracking.controller;

import com.wu.achievers.BugTracking.entity.User;
import com.wu.achievers.BugTracking.exceptionHandling.GlobalExceptionHandler;
import com.wu.achievers.BugTracking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetAllUsers_NoManagerId() throws Exception {
        User user = new User(1L, "Test", "User", "user@example.com", "pass", "Admin", null);
        when(userService.fetchAllUsers(anyString())).thenReturn(List.of(user));
        mockMvc.perform(get("/api/users").header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("user@example.com"));
    }

    @Test
    void testGetAllUsers_WithManagerId() throws Exception {
        User user = new User(2L, "Test", "User", "manager@example.com", "pass", "Manager", 1L);
        when(userService.fetchUsersByManagerId(eq(1L), anyString())).thenReturn(List.of(user));
        mockMvc.perform(get("/api/users").param("managerId", "1").header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("manager@example.com"));
    }

    @Test
    void testGetUserById_Found() throws Exception {
        User user = new User(1L, "Test", "User", "user@example.com", "pass", "Admin", null);
        when(userService.fetchUserById(eq(1L), anyString())).thenReturn(user);
        mockMvc.perform(get("/api/users/1").header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void testSignup_Success() throws Exception {
        User created = new User(2L, "Test", "User", "newuser@example.com", "pass", "Admin", null);
        when(userService.fetchUserByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(userService.registerUser(any(User.class))).thenReturn(created);
        String json = "{\"firstname\":\"Test\",\"lastname\":\"User\",\"email\":\"newuser@example.com\",\"password\":\"pass\",\"role\":\"Admin\"}";
        mockMvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("newuser@example.com"));
    }

    @Test
    void testSignup_EmailExists() throws Exception {
        User user = new User(null, "Test", "User", "existing@example.com", "pass", "Admin", null);
    when(userService.fetchUserByEmail("existing@example.com")).thenReturn(Optional.of(user));
        String json = "{\"firstname\":\"Test\",\"lastname\":\"User\",\"email\":\"existing@example.com\",\"password\":\"pass\",\"role\":\"Admin\"}";
        mockMvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_Success() throws Exception {
    when(userService.authenticateUser(eq("login@example.com"), eq("pass"))).thenReturn("jwt-token");
        String json = "{\"email\":\"login@example.com\",\"password\":\"pass\"}";
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Login successful jwt-token")));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        //User user = new User(null, "Test", "User", "loginfail@example.com", "wrongpass", "Admin", null);
    when(userService.authenticateUser(eq("loginfail@example.com"), eq("wrongpass"))).thenReturn("Invalid credentials");
        String json = "{\"email\":\"loginfail@example.com\",\"password\":\"wrongpass\"}";
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid credentials")));
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = new User(1L, "Test", "User", "user@example.com", "pass", "Admin", null);
    when(userService.updateUserDetails(any(User.class), anyString())).thenReturn(user);
        String json = "{\"userID\":1,\"firstname\":\"Test\",\"lastname\":\"User\",\"email\":\"user@example.com\",\"password\":\"pass\",\"role\":\"Admin\"}";
        mockMvc.perform(put("/api/users")
                .header("Authorization", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void testDeleteUser() throws Exception {
    doNothing().when(userService).removeUser(eq(1L), anyString());
    mockMvc.perform(delete("/api/users/1").header("Authorization", "token"))
        .andExpect(status().isOk());
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk());
    }
}
