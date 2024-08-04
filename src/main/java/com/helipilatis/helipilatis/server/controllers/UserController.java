package com.helipilatis.helipilatis.server.controllers;

import com.helipilatis.helipilatis.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final Logger logger = Logger.getLogger(UserController.class.getName());

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles GET requests to retrieve the number of tickets for a user.
     * @param userId the ID of the user
     * @return a ResponseEntity containing the number of tickets and an HTTP status code
     */
    @GetMapping("/tickets/{userId}")
    public ResponseEntity<Integer> getUserTicketCount(@PathVariable Long userId) {
        try {
            int ticketCount = userService.getUserTicketCount(userId);
            return ResponseEntity.ok(ticketCount);
        } catch (Exception e) {
            logger.warning("Error getting ticket count for user ID: " + userId + ". " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

}