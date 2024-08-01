package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.client.requests.LoginResponse;
import com.helipilatis.helipilatis.client.requests.PilatisClass;
import com.helipilatis.helipilatis.server.requests.LoginRequest;
import com.helipilatis.helipilatis.server.requests.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ApiRequests {

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        String url = "http://localhost:8080/api/auth/login";
        try {
            return restTemplate.postForEntity(url, loginRequest, LoginResponse.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    public ResponseEntity<String> register(RegisterRequest registerRequest) {
        String url = "http://localhost:8080/api/auth/register";
        try {
            return restTemplate.postForEntity(url, registerRequest, String.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    public ResponseEntity<String> bookClass(Long classId, Long userId) {
        String url = "http://localhost:8080/api/calendar/classes/" + classId + "/signup?userId=" + userId;
        try {
            return restTemplate.postForEntity(url, null, String.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    public ResponseEntity<String> cancelClassForUser(Long classId, Long userId) {
        String url = "http://localhost:8080/api/calendar/classes/" + classId + "/cancel?userId=" + userId;
        try {
            return restTemplate.postForEntity(url, null, String.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    public ResponseEntity<List<PilatisClass>> getAllClasses() {
        String url = "http://localhost:8080/api/calendar/classes"; // Replace with your actual API endpoint
        try {
            ResponseEntity<PilatisClass[]> response = restTemplate.getForEntity(url, PilatisClass[].class);
            return ResponseEntity.status(HttpStatus.OK).body(Arrays.asList(response.getBody()));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    public ResponseEntity<String> cancelClassAsInstructor(Long classId) {
        String url = "http://localhost:8080/api/calendar/classes/" + classId + "/cancel";
        try {
            restTemplate.delete(url);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Class successfully cancelled");
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }
}