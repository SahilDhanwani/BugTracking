package com.wu.achievers.BugTracking.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wu.achievers.BugTracking.entity.User;
import com.wu.achievers.BugTracking.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> fetchAllUsers(@RequestParam(required = false) Long managerId, @RequestHeader("Authorization") String token) {
        if (managerId == null) {
            return ResponseEntity.ok(userService.fetchAllUsers(token));
        }
        return ResponseEntity.ok(userService.fetchUsersByManagerId(managerId, token));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> fetchUserById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userService.fetchUserById(id, token));
    }

    @PostMapping("/signup")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody User user, HttpServletResponse response) {
        try {
            String jwt = userService.authenticateUser(user.getEmail(), user.getPassword());
            Cookie cookie = new Cookie("JWT", jwt);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);

            response.addCookie(cookie);
            User loggedInUser = userService.getUserFromToken(jwt);
            
            return ResponseEntity.ok(Map.of(
                "token", jwt,
                "user", loggedInUser.getFirstname()
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(Map.of("error", "Invalid credentials"));
        }

    }


    @PutMapping("/users")
    public ResponseEntity<User> updateUserDetails(@RequestBody User user, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userService.updateUserDetails(user, token));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        userService.removeUser(id, token);
        return ResponseEntity.ok("User Deleted");
    }

    @GetMapping("/managers")
    public ResponseEntity<List<User>> fetchAllManagers(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userService.fetchAllManagers(token));
    }
}
