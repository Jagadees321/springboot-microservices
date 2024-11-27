package com.example.questionservices.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quiz")
public class TestController {

    @GetMapping("/test/{id}")
    public String test() {
        return "Hello World!!";
    }
}
