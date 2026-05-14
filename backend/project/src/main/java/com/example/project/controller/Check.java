package com.example.project.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Check
{
    @GetMapping("/user")
    public String user()
    {
        return "User endpoint";
    }
    @GetMapping("/test")
    public String test()
    {
        return "test";
    }

    @GetMapping("/admin")
    public String admin()
    {
        return "Admin endpoint";
    }
}
