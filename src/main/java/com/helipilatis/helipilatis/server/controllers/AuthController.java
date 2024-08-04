package com.helipilatis.helipilatis.server.controllers;
import java.util.logging.Logger;

import com.helipilatis.helipilatis.server.ServerExceptions;
import com.helipilatis.helipilatis.server.requests.LoginResponse;
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
    private static final Logger logger = Logger.getLogger("AuthController");

    /**
     * Handles POST request for login
     * @param loginRequest the login request
     * @param session the session
     * @return ResponseEntity with status code and message
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        logger.info("loginRequest : " + loginRequest);
        try {
            LoginResponse loginResponse = authService.login(loginRequest);
            Long userId = loginResponse.getUserId();
            session.setAttribute("userId", userId); // Set userId in session
            logger.info("Login successful, userId: " + userId);
            return ResponseEntity.ok(loginResponse);
        } catch (ServerExceptions.LoginException e) {
            logger.info("Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    /**
     * Handles POST request for registration
     * @param registerRequest the register request
     * @return ResponseEntity with status code and message
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        logger.info("registerRequest : " + registerRequest);
        try {
            Long userId = authService.register(registerRequest);
            logger.info("Register successful");
            return ResponseEntity.ok(String.valueOf(userId));
        } catch (ServerExceptions.RegistrationException e) {
            logger.info("Register failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Register failed: " + e.getMessage());
        }
    }
}