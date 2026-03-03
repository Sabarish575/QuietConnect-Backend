package com.example.quietconnect_backend.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quietconnect_backend.jwt.JwtUtil;
import com.example.quietconnect_backend.user.User;
import com.example.quietconnect_backend.user.UserService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

// In AuthController
@GetMapping("/me")
public ResponseEntity<?> me(@CookieValue(name = "token", required = false) String token) {
    if (token == null) return ResponseEntity.status(401).build();

    String email = jwtUtil.extractEmail(token);
    User user = userService.find(email);

    // Return only what the frontend needs
    Map<String, Object> response = new HashMap<>();
    response.put("email", user.getEmail());
    response.put("username", user.getUsername());
    return ResponseEntity.ok(response);
}
}
