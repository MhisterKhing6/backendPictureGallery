package com.example.picturegaller.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeControler {
    @GetMapping("/") 
    String home() {
        return "I am home";
    }
    
}
