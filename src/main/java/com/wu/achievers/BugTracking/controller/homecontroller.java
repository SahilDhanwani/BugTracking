package com.wu.achievers.BugTracking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class homecontroller {

    // Made a change here

    @GetMapping
    public String home() {
        return "Welcome to the Bs Tracking System";
    }
}
