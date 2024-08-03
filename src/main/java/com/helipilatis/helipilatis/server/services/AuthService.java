package com.helipilatis.helipilatis.server.services;

import com.helipilatis.helipilatis.server.ServerExceptions.LoginException;
import com.helipilatis.helipilatis.server.ServerExceptions.RegistrationException;
import com.helipilatis.helipilatis.databaseModels.Instructor;
import com.helipilatis.helipilatis.databaseModels.User;
import com.helipilatis.helipilatis.databaseModels.UserRepository;
import com.helipilatis.helipilatis.databaseModels.InstructorRepository;
import com.helipilatis.helipilatis.server.requests.LoginRequest;
import com.helipilatis.helipilatis.server.requests.LoginResponse;
import com.helipilatis.helipilatis.server.requests.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    public LoginResponse login(LoginRequest loginRequest) throws LoginException {
        String phone = loginRequest.getPhone();

        if (phoneExistsInInstructors(phone)) {
            Instructor instructor = instructorRepository.findByPhone(phone)
                    .orElseThrow(() -> new LoginException("Phone number does not exist"));
            return new LoginResponse(instructor.getId(), true, instructor.getName());
        }

        if (phoneExistsInUsers(phone)) {
            User user = userRepository.findByPhone(phone)
                    .orElseThrow(() -> new LoginException("Phone number does not exist"));
            return new LoginResponse(user.getId(), false, user.getName());
        }

        throw new LoginException("Phone number does not exist");
    }

    public Long register(RegisterRequest registerRequest) throws RegistrationException {
        String phone = registerRequest.getPhone();

        if (phoneExistsInUsers(phone) || phoneExistsInInstructors(phone)) {
            throw new RegistrationException("Phone number already exists");
        }

        User user = new User(phone, registerRequest.getName(), registerRequest.getAge(), registerRequest.getGender());
        userRepository.save(user);
        return user.getId();
    }

    private boolean phoneExistsInUsers(String phone) {
        Optional<User> user = userRepository.findByPhone(phone);
        return user.isPresent();
    }

    private boolean phoneExistsInInstructors(String phone) {
        Optional<Instructor> instructor = instructorRepository.findByPhone(phone);
        return instructor.isPresent();
    }
}