package com.helipilatis.helipilatis.server.controllers;

import com.helipilatis.helipilatis.server.services.MailboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/mailbox")
public class MailboxController {

    private final MailboxService mailboxService;
    private final Logger logger = Logger.getLogger(MailboxController.class.getName());

    @Autowired
    public MailboxController(MailboxService mailboxService) {
        this.mailboxService = mailboxService;
    }

    @PostMapping("/instructor/send-message-to-all")
    public ResponseEntity<Void> addMessageToAllUsers(@RequestBody String message) {
        try {
            logger.info("Adding message to all users: " + message);
            mailboxService.addMessageToAllUsers(message);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            logger.warning("Error adding message to all users: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/inbox/{userId}")
    public ResponseEntity<String> getInboxMessages(@PathVariable Long userId) {
        try {
            logger.info("Fetching inbox messages for user: " + userId);
            String inboxMessages = mailboxService.getInboxMessages(userId);
            return ResponseEntity.status(HttpStatus.OK).body(inboxMessages);
        } catch (Exception e) {
            logger.warning("Error fetching inbox messages: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}