package com.helipilatis.helipilatis.server.controllers;
import java.util.logging.Logger;

import com.helipilatis.helipilatis.server.ServerExceptions;
import com.helipilatis.helipilatis.server.services.AuthService;
import com.helipilatis.helipilatis.server.requests.LoginRequest;
import com.helipilatis.helipilatis.server.requests.RegisterRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth") //request mapping
public class AuthController {

    @Autowired
    private AuthService authService;
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        logger.info("loginRequest : " + loginRequest);
        try {
            Long userId = authService.login(loginRequest);
            session.setAttribute("userId", userId); // Set userId in session
            logger.info("Login successful, userId: " + userId);
            return ResponseEntity.ok("Login successful, userId: " + userId);
        } catch (ServerExceptions.LoginException e) {
            logger.info("Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        logger.info("registerRequest : " + registerRequest);
        try {
            authService.register(registerRequest);
            logger.info("Register successful");
            return ResponseEntity.ok("Register successful");
        } catch (ServerExceptions.RegistrationException e) {
            logger.info("Register failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Register failed: " + e.getMessage());
        }
    }
}