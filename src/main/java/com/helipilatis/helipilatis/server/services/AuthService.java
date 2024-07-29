package com.helipilatis.helipilatis.server.services;
import com.helipilatis.helipilatis.databaseModels.User;
import com.helipilatis.helipilatis.databaseModels.UserRepository;
import com.helipilatis.helipilatis.server.requests.LoginRequest;
import com.helipilatis.helipilatis.server.requests.RegisterRequest;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    // This method should check the credentials against those stored in the database or another authentication system
    public boolean authenticate(LoginRequest loginRequest) {
        // Placeholder logic for authentication
        // Replace with actual authentication logic
        return true;
    }


    public boolean register(RegisterRequest registerRequest) {
        User user = new User(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());
        //User user = new User(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword(), "active", registerRequest.getAge(), registerRequest.getGender(), 0);
        userRepository.save(user);
        return true;
    }
}