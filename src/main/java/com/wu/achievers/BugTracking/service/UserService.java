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
import com.wu.achievers.BugTracking.exceptionHandling.BadRequestException;
import com.wu.achievers.BugTracking.exceptionHandling.NotFoundException;
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
        List<User> users;
        String role = jwtUtil.extractRole(token);
        if ("Admin".equals(role)) {
            users = userRepo.findAll();
            
        } else if ("Manager".equals(role)) {
            Long managerId = jwtUtil.extractUserId(token);
            users = userRepo.findByManagerID(managerId);
        }
        else {
            users = userRepo.findById(jwtUtil.extractUserId(token)).map(List::of).orElse(List.of());
        }
        
        if(users.isEmpty())  throw new NotFoundException("No Users exist");
        return users;
    }

    public User getUserById(Long id, String token) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserId(token);
        User user = null;
        if ("Admin".equals(role) || userId.equals(id)) {
            user = userRepo.findById(id).orElse(null);
            if(user == null)
            throw new NotFoundException("User with ID " + id + " not found");
        }

        else if ("Manager".equals(role)) {
            user = userRepo.checkByManagerIdAndUserId(userId, id).orElse(null);
            if(user == null)
            throw new BadRequestException("User with ID " + id + " does not exist or is not the part of your team");
        }
        return user;
    }

    public User updateUser(User user, String token) {

        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserId(token);

        User newUser = userRepo.findById(user.getUserID()).orElse(null);

        if(newUser == null) throw new NotFoundException("No Such User Exists in the DB");

        if("Admin".equals(role)) {
            newUser.setManagerID(user.getManagerID());
        }
        else if (userId.equals(user.getUserID())) {
            newUser.setFirstname(user.getFirstname());
            newUser.setLastname(user.getLastname());
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepo.save(newUser);
    }

    public void deleteUser(Long id, String token) {

        String role = jwtUtil.extractRole(token);
        if("Admin".equals(role) && userRepo.existsById(id)) 
            userRepo.deleteById(id);
        else 
            throw new BadRequestException("Either you are not authorized to do so or the user does not exists");
    }

    public List<User> getUsersByManagerId(Long managerId, String token) {
        List<User> arr = userRepo.findByManagerID(managerId);
        if(arr.isEmpty()) throw new BadRequestException("Incorrect manager ID or no such users exists");
        return arr;
    }

    public User signup(User user) {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new BadRequestException("User Email already exists");
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
            throw new BadRequestException("Invalid Credentials");
        }
    }

    // Function not used by controller directly
    public boolean checkUserByManagerID(Long managerId, Long userId) {
        Optional<User> user = userRepo.checkByManagerIdAndUserId(managerId, userId);
        return user != null;
    }

    public boolean checkUserByManagerIdAndUserId(Long managerId, Long userId) {
        Optional<User> user = userRepo.checkByManagerIdAndUserId(managerId, userId);
        return user != null;
    }

    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
