package com.wu.achievers.BugTracking.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public List<User> fetchAllUsers(String jwtToken) {
        List<User> userList;
        String userRole = jwtUtil.extractRole(jwtToken);
        if (userRole == null) {
            userList = userRepository.findById(jwtUtil.extractUserId(jwtToken)).map(List::of).orElse(List.of());
        } else {
            switch (userRole) {
                case "Admin" ->
                    userList = userRepository.findAll();
                case "Manager" -> {
                    Long managerId = jwtUtil.extractUserId(jwtToken);
                    userList = userRepository.findByManagerID(managerId);
                }
                default ->
                    userList = userRepository.findById(jwtUtil.extractUserId(jwtToken)).map(List::of).orElse(List.of());
            }
        }
        if (userList.isEmpty()) {
            throw new NotFoundException("No users exist");
        }
        return userList;
    }

    public User fetchUserById(Long userId, String jwtToken) {
        String userRole = jwtUtil.extractRole(jwtToken);
        Long tokenUserId = jwtUtil.extractUserId(jwtToken);
        User foundUser = null;
        if ("Admin".equals(userRole) || tokenUserId.equals(userId)) {
            foundUser = userRepository.findById(userId).orElse(null);
        } else if ("Manager".equals(userRole)) {
            foundUser = userRepository.checkByManagerIdAndUserId(tokenUserId, userId).orElse(null);
            if (foundUser == null) {
                throw new BadRequestException("User with ID " + userId + " does not exist or is not part of your team");
            }
        }
        if (foundUser == null) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }
        return foundUser;
    }

    public User updateUserDetails(User userToUpdate, String jwtToken) {
        String userRole = jwtUtil.extractRole(jwtToken);
        Long tokenUserId = jwtUtil.extractUserId(jwtToken);
        User dbUser = userRepository.findById(userToUpdate.getUserId()).orElse(null);
        if (dbUser == null) {
            throw new NotFoundException("No such user exists in the database");
        }
        if ("Admin".equals(userRole)) {
            dbUser.setManagerId(userToUpdate.getManagerId());
        } else if (tokenUserId.equals(userToUpdate.getUserId())) {
            dbUser.setFirstname(userToUpdate.getFirstname());
            dbUser.setLastname(userToUpdate.getLastname());
            dbUser.setPassword(passwordEncoder.encode(userToUpdate.getPassword()));
        }
        return userRepository.save(dbUser);
    }

    public void removeUser(Long userId, String jwtToken) {
        String userRole = jwtUtil.extractRole(jwtToken);
        if ("Admin".equals(userRole) && userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new BadRequestException("Either you are not authorized to do so or the user does not exist");
        }
    }

    public List<User> fetchUsersByManagerId(Long managerId, String jwtToken) {
        List<User> usersByManager = userRepository.findByManagerID(managerId);
        if (usersByManager.isEmpty()) {
            throw new BadRequestException("Incorrect manager ID or no such users exist");
        }
        return usersByManager;
    }

    public User registerUser(User newUser) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new BadRequestException("User email already exists");
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepository.save(newUser);
        return newUser;
    }

    public ResponseEntity<?> authenticateUser(String email, String password, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            User user = userRepository.findByEmail(email).orElseThrow();
            String jwt = jwtUtil.generateToken(email, user.getRole(), user.getUserId());
            Cookie cookie = new Cookie("JWT", jwt);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);

            response.addCookie(cookie);

            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "user", user.getFirstname()
            ));

        } catch (AuthenticationException e) {
            throw new BadRequestException("Invalid credentials");
        }

    }

    // Function not used by controller directly
    public boolean existsUserByManagerId(Long managerId, Long userId) {
        Optional<User> user = userRepository.checkByManagerIdAndUserId(managerId, userId);
        return user.isPresent();
    }

    public boolean checkUserByManagerIdAndUserId(Long managerId, Long userId) {
        Optional<User> user = userRepository.checkByManagerIdAndUserId(managerId, userId);
        return user.isPresent();
    }

    public Optional<User> fetchUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsUserById(long userId) {
        return userRepository.existsById(userId);
    }
    
    public List<User> fetchAllManagers(String token) {
        return userRepository.findByRole("Manager");
    }
}
