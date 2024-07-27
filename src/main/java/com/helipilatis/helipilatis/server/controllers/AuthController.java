package com.helipilatis.helipilatis.server.controllers;
import java.util.logging.Logger;

import com.helipilatis.helipilatis.server.services.AuthService;
import com.helipilatis.helipilatis.server.requests.LoginRequest;
import com.helipilatis.helipilatis.server.requests.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        // print for starting the function :
        logger.info("loginRequest : " + loginRequest);
        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {

        // print for starting the function :
        logger.info("registerRequest : " + registerRequest);

        boolean isRegistered = authService.register(registerRequest);
        if (isRegistered) {
            return ResponseEntity.ok("Register successful");
        } else {
            return ResponseEntity.status(400).body("Register failed");
        }
    }
}