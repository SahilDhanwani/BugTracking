package com.wu.achievers.BugTracking.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "Users")
@SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @Column(name = "UserID")
    private Long userID;

    @Column(name = "FirstName", length = 50, nullable = false)
    private String firstname;

    @Column(name = "LastName", length = 50, nullable = false)
    private String lastname;

    @Column(name = "Email", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "Password", length = 255, nullable = false)
    private String password;

    @Column(name = "Role", length = 50, nullable = false)
    private String role;

    @Column(name = "ManagerID")
    private Long managerID;

    // Default constructor
    public User() {
    }

    // Parameterized constructor
    public User(Long userID, String firstname, String lastname, String email, String password, String role, Long managerID) {
         
        this.userID = userID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.managerID = managerID;
    }

    // Getters and Setters
    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getManagerID() {
        return managerID;
    }

    public void setManagerID(Long managerID) {
        this.managerID = managerID;
    }
}
