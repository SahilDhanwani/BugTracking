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

    public Optional<User> getUserById(String token, Long id) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserId(token);
        if (role.equals("Developer") || role.equals("Tester")) {
            if(id != userId) {
                //Yahan pe ek exception aana chahiye.
            }
            return userRepo.findById(userId); 
        }
        else if (role.equals("Manager")) {
            return userRepo.findUserByManager(userId, id);
        }
        return userRepo.findById(id);
    }

    public boolean checkUserByManagerID(Long managerId, Long userId) {
        User user = userRepo.findUserByManager(managerId, userId).orElseThrow(() -> new RuntimeException("User not found"));
        if(user != null) {
            return true;
        }
        return false;
    }

    public User createUser(User user) {
        return userRepo.save(user);
    }

    public User updateUser(String token, User user) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserId(token);
        User currentUser = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (userRepo.existsById(user.getUserID())) {
            if(role.equals("Developer") || role.equals("Tester")) {
                if (!currentUser.getRole().equals(user.getRole()) || !currentUser.getUserID().equals(user.getUserID()) || !currentUser.getManagerID().equals(user.getManagerID())) {
                    //Yahan pe ek exception
                }
            }

            //user.setPassword(userRepo.findById(user.getUserID()).get().getPassword());
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
