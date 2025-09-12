package com.wu.achievers.BugTracking.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.wu.achievers.BugTracking.exceptionHandling.BadRequestException;
import com.wu.achievers.BugTracking.exceptionHandling.NotFoundException;
import com.wu.achievers.BugTracking.repository.UserRepo;
import com.wu.achievers.BugTracking.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
 @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

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
    public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response) {
        try {
            String jwt = userService.login(user.getEmail(), user.getPassword());
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

// @GetMapping("/me")
// public ResponseEntity<User> getCurrentUser(Authentication authentication) {
//     String email = authentication.getName(); // from JWT subject
//     System.out.println("Authenticated user email: " + email);
//     User user = userRepo.findByEmail(email)
//             .orElseThrow(() -> new RuntimeException("User not found"));
//     return ResponseEntity.ok(user);
// }


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
