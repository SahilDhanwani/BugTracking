package com.wu.achievers.BugTracking.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class homecontroller {


    @GetMapping
    public String home() {
        return "Welcome to the Bug Tracking System";
    }

}
