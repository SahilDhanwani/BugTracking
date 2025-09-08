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

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepo.findById(id);
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

    public List<User> getUsersByManagerId(Long managerId) {
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
            return jwtUtil.generateToken(email, user.getRole());
        } catch (AuthenticationException e) {
            return "Invalid credentials";
        }
    }

    public Optional<User> findByEmail(String email) {
    return userRepo.findByEmail(email);
}


    public List<User> sahil() {
        List<User> arr = userRepo.findAll();
        for(User x : arr) {
            x.setPassword(passwordEncoder.encode(x.getPassword()));
            userRepo.save(x);
        }
        return userRepo.findAll();
    }
}
