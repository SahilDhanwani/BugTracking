package com.wu.achievers.BugTracking.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.AuthenticationException;

import com.wu.achievers.BugTracking.entity.User;
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
        System.out.println("Authorization Header SD: " + token); // Debugging line
        if (managerId == null) {
            return userService.getAllUsers(token);
        }
        return userService.getUsersByManagerId(managerId, token);
    }

    @GetMapping("/users/{id}")
    public Optional<User> getUserById(Authentication authentication, @PathVariable Long id) {
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);

        return userService.getUserById(currentUser, id);
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
    
    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        return userService.signup(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user, HttpServletResponse response) {
        try {
            String jwt = userService.login(user.getEmail(), user.getPassword());
            Cookie cookie = new Cookie("JWT", jwt);
            cookie.setHttpOnly(true);  // Make sure the cookie is not accessible from JavaScript
            cookie.setSecure(true);    // Make sure the cookie is sent only over HTTPS (optional, but recommended)
            cookie.setPath("/");       // Set the path for the cookie
            cookie.setMaxAge(3600);

            response.addCookie(cookie);

            return "Login successful " + jwt;
        }
        catch (AuthenticationException e) {
            return "Invalid Credentials";
        }
        
    }
    
    @PutMapping("/users")
    public User updateUser(Authentication authentication, @RequestBody User user) {
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);

        if(currentUser.getRole().equals("Developer") || currentUser.getRole().equals("Tester")) {
            if (!currentUser.getRole().equals(user.getRole()) || !currentUser.getUserID().equals(user.getUserID()) || !currentUser.getManagerID().equals(user.getManagerID())) {
                //Yahan pe ek exception
            }
            return userService.updateUser(user);
        }

        return userService.updateUser(user);
    }

    @DeleteMapping("/users/{id}")
    public Void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return null;
    }
}
