package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.client.requests.LoginResponse;
import com.helipilatis.helipilatis.server.requests.LoginRequest;
import com.helipilatis.helipilatis.server.requests.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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


    public ResponseEntity<String> userBookClass(Long classId, Long userId) {
        String url = "http://localhost:8080/api/calendar/classes/" + classId + "/signup?userId=" + userId;
        try {
            return restTemplate.postForEntity(url, null, String.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    public ResponseEntity<String> userCancelClass(Long classId, Long userId) {
        String url = "http://localhost:8080/api/calendar/classes/" + classId + "/cancel?userId=" + userId;
        try {
            return restTemplate.postForEntity(url, null, String.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    public ResponseEntity<String> purchaseTicket(Long userId, Long ticketTypeId) {
        String url = "http://localhost:8080/api/shop/purchaseTicket?userId=" + userId + "&ticketTypeId=" + ticketTypeId;
        try {
            return restTemplate.postForEntity(url, null, String.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }


    public ResponseEntity<String> instructorCancelClass(Long classId) {
        String url = "http://localhost:8080/api/calendar/instructor/classes/" + classId + "/cancel";
        try {
            return restTemplate.postForEntity(url, null, String.class);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    public ResponseEntity<String> instructorRestoreClass(Long classId) {
        String url = "http://localhost:8080/api/calendar/instructor/classes/" + classId + "/restore";
        try {
            return restTemplate.postForEntity(url, null, String.class);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    public ResponseEntity<String> instructorUpdateParticipants(Long classId, int newParticipants) {
        String url = "http://localhost:8080/api/calendar/instructor/" + classId + "/participants";
        try {
            return restTemplate.postForEntity(url, newParticipants, String.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    public ResponseEntity<String> sendMessageToAllUsers(String message) {
        String url = "http://localhost:8080/api/mailbox/instructor/send-message-to-all";
        return restTemplate.postForEntity(url, message, String.class);
    }

}