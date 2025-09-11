package com.wu.achievers.BugTracking.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.AuthenticationException;

import com.wu.achievers.BugTracking.entity.User;
import com.wu.achievers.BugTracking.exceptionHandling.BadRequestException;
import com.wu.achievers.BugTracking.exceptionHandling.NotFoundException;
import com.wu.achievers.BugTracking.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<User> getAllUsers(@RequestParam(required = false) Long managerId, @RequestHeader("Authorization") String token) {
        if (managerId == null) {
            return userService.getAllUsers(token);
        }
        return userService.getUsersByManagerId(managerId, token);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id, @RequestHeader("Authorization") String token) {

        Optional<User> user = userService.getUserById(id, token);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            throw new NotFoundException("User with ID " + id + " not found");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody User user) {
        Optional<User> existingUser = userService.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new BadRequestException("Email " + user.getEmail() + " is already in use");
        }

        User createdUser = userService.signup(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user, HttpServletResponse response) {
        try {
            String jwt = userService.login(user.getEmail(), user.getPassword());
            Cookie cookie = new Cookie("JWT", jwt);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);

            response.addCookie(cookie);

            return "Login successful " + jwt;
        } catch (AuthenticationException e) {
            return "Invalid Credentials";
        }

    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user, @RequestHeader("Authorization") String token) {
        return userService.updateUser(user, token);
    }

    @DeleteMapping("/users/{id}")
    public Void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return null;
    }
}
