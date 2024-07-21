package com.helipilatis.helipilatis.server.login_and_register;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {



    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        // print for starting the function :
        System.out.println("loginRequest : " + loginRequest);

        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {

        // print for starting the function :
        System.out.println("registerRequest : " + registerRequest);

        return ResponseEntity.ok("Register successful");
    }
}