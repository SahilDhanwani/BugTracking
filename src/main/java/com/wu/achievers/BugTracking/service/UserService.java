package com.wu.achievers.BugTracking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wu.achievers.BugTracking.entity.User;
import com.wu.achievers.BugTracking.repository.UserRepo;
import com.wu.achievers.BugTracking.util.JwtUtil;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public List<User> getAllUsers(String token) {
        String role = jwtUtil.extractRole(token);
        System.out.println("User Role: " + role); // Debugging line
        if ("Admin".equals(role)) {
            return userRepo.findAll();
        } else if ("Manager".equals(role)) {
            Long managerId = jwtUtil.extractUserId(token);
            System.out.println("Manager ID: " + managerId); // Debugging line
            return userRepo.findByManagerID(managerId);
        }
        return userRepo.findById(jwtUtil.extractUserId(token)).map(List::of).orElse(List.of());
    }

    public Optional<User> getUserById(User user, Long id) {
        if (user.getRole().equals("Developer") || user.getRole().equals("Tester")) {
            if(id != user.getUserID()) {
                //Yahan pe ek exception aana chahiye.
            }
            return userRepo.findById(user.getUserID()); 
        }
        else if (user.getRole().equals("Manager")) {
            return userRepo.findUserByManager(user.getUserID(), id);
        }
        return userRepo.findById(id);
    }

    public User getUserByMID(Long managerId, Long userId) {
        return userRepo.findUserByManager(managerId, userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User createUser(User user) {
        return userRepo.save(user);
    }

    public User updateUser(User user) {
        if (userRepo.existsById(user.getUserID())) {
            user.setPassword(userRepo.findById(user.getUserID()).get().getPassword());
            return userRepo.save(user);
        }
        return null;
    }

    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

    public List<User> getUsersByManagerId(Long managerId, String token) {
        return userRepo.findByManagerID(managerId);
    }

    public User signup(User user) {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            return null;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return user;
    }

    public String login(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            User user = userRepo.findByEmail(email).orElseThrow();
            return jwtUtil.generateToken(email, user.getRole(), user.getUserID());
        } catch (AuthenticationException e) {
            return "Invalid credentials";
        }
    }
}
