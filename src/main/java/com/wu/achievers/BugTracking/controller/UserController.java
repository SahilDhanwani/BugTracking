package com.wu.achievers.BugTracking.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.wu.achievers.BugTracking.entity.User;
import com.wu.achievers.BugTracking.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<User> getAllUsers(@RequestParam(required = false) Long managerId, @RequestHeader("Authorization") String token) {
        if (managerId == null) {
            // Adding a comment here for Git issues
            return userService.getAllUsers(token);
        }
        return userService.getUsersByManagerId(managerId, token);
    }

    @GetMapping("/users/{id}")
    public Optional<User> getUserById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        return userService.getUserById(id, token);
    }

    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        return userService.signup(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        return userService.login(user.getEmail(), user.getPassword());
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
