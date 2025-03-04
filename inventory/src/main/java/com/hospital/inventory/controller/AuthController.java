package com.hospital.inventory.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = """
        http://localhost:5544
        """)
public class AuthController {

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        // For now, return a dummy token; later, integrate with Spring Security
        if ("admin".equals(request.getUsername()) && "password123".equals(request.getPassword())) {
            return "登录成功！Token: dummy-token-123";
        }
        return "登录失败！";
    }
}

class LoginRequest {
    private String username;
    private String password;

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}