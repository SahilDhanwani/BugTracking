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
        if ("Admin".equals(role)) {
            return userRepo.findAll();
        } else if ("Manager".equals(role)) {
            Long managerId = jwtUtil.extractUserId(token);
            return userRepo.findByManagerID(managerId);
        }
        return userRepo.findById(jwtUtil.extractUserId(token)).map(List::of).orElse(List.of());
    }

    public Optional<User> getUserById(Long id, String token) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserId(token);

        if ("Admin".equals(role) || userId.equals(id)) {
            return userRepo.findById(id);
        }

        if ("Manager".equals(role)) {
            List<Long> arr = userRepo.getIdsByManagerId(userId);
            for (Long i : arr) {
                if (i.equals(id)) {
                    return userRepo.findById(id);
                }
            }
        }
        return null;
    }

    public User updateUser(User user, String token) {
        Long userId = jwtUtil.extractUserId(token);
        if (userId.equals(user.getUserID()) && userRepo.existsById(user.getUserID())) {
            user.setPassword(userRepo.findById(user.getUserID()).get().getPassword());
        }
        return userRepo.save(user);
    }

    public boolean checkUserByManagerID(Long managerId, Long userId) {
        User user = userRepo.checkByManagerIdAndUserId(managerId, userId);
        return user != null;
    }

    public void deleteUser(Long id) {
        // AddException
        userRepo.deleteById(id);
    }

    public List<User> getUsersByManagerId(Long managerId, String token) {
        // AddException: Agar ye function ne kuch return nahi kiya toh, Write ki no such manager exists
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

    public boolean checkUserByManagerIdAndUserId(Long managerId, Long userId) {
        User user = userRepo.checkByManagerIdAndUserId(managerId, userId);
        return user != null;
    }

    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public boolean checkUserById(long managerId) {
        return userRepo.existsById(managerId);
        
    }
}
