package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Root", description = "Basic application endpoint")
public class HelloController {
    @GetMapping("/")
    @Operation(summary = "Health/greeting endpoint")
    public String hello() {
        return "Hello, Spring Boot!";
    }
}
