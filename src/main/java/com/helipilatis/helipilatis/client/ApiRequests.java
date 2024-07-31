package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.server.requests.LoginRequest;
import com.helipilatis.helipilatis.server.requests.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiRequests {

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<String> login(LoginRequest loginRequest) {
        String url = "http://localhost:8080/api/auth/login";
        try {
            return restTemplate.postForEntity(url, loginRequest, String.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
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

    public ResponseEntity<String> cancelClass(Long classId, Long userId) {
        String url = "http://localhost:8080/api/calendar/classes/" + classId + "/cancel?userId=" + userId;
        try {
            return restTemplate.postForEntity(url, null, String.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }
}