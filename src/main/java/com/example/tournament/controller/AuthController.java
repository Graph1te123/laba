package com.example.tournament.controller;

import com.example.tournament.dto.LoginRequest;
import com.example.tournament.dto.RegisterRequest;
import com.example.tournament.model.User;
import com.example.tournament.repository.UserRepository;
import com.example.tournament.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Email already registered");
            return ResponseEntity.badRequest().body(response);
        }

        if (!isPasswordStrong(request.getPassword())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Password must be at least 8 characters");
            return ResponseEntity.badRequest().body(response);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("email", user.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> tokenPair = tokenService.createTokenPair(user);

            return ResponseEntity.ok(tokenPair);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Missing or invalid authorization header");
                return ResponseEntity.status(401).body(response);
            }

            String refreshToken = authHeader.substring(7);
            Map<String, Object> tokenPair = tokenService.refreshTokenPair(refreshToken);

            return ResponseEntity.ok(tokenPair);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Missing authorization header");
                return ResponseEntity.status(400).body(response);
            }

            String accessToken = authHeader.substring(7);
            tokenService.revokeSession(accessToken);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Logged out successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Logout failed");
            return ResponseEntity.status(400).body(response);
        }
    }

    private boolean isPasswordStrong(String password) {
        return password.length() >= 8;
    }
}
