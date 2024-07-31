package com.helipilatis.helipilatis.server.controllers;

import com.helipilatis.helipilatis.client.UserView;
import com.helipilatis.helipilatis.databaseModels.PilatisClass;
import com.helipilatis.helipilatis.server.services.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.helipilatis.helipilatis.config.LoggingUtil;
import java.util.logging.Logger;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private static final Logger logger = Logger.getLogger(UserView.class.getName());

    @Autowired
    private CalendarService calendarService;

    @GetMapping("/classes")
    public List<PilatisClass> getAllClasses() {
        return calendarService.getAllClasses();
    }

    @PostMapping("/classes")
    public PilatisClass createClass(@RequestBody PilatisClass pilatisClass) {
        return calendarService.saveClass(pilatisClass);
    }

    @PutMapping("/classes/{id}")
    public PilatisClass updateClass(@PathVariable Long id, @RequestBody PilatisClass pilatisClass) {
        return calendarService.updateClass(id, pilatisClass);
    }

    @DeleteMapping("/classes/{id}")
    public void deleteClass(@PathVariable Long id) {
        calendarService.deleteClass(id);
    }


    @PostMapping("/classes/{classId}/signup")
    public ResponseEntity<String> signUpForClass(@PathVariable Long classId, @RequestParam Long userId) {
        logger.info("Signing up for class " + classId + " with user " + userId);
        try {
            calendarService.signUpForClass(classId, userId);
            return ResponseEntity.ok("Successfully booked");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body("Class is full");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error booking class");
        }
    }

    @PostMapping("/classes/{classId}/cancel")
    public ResponseEntity<String> cancelClass(@PathVariable Long classId, @RequestParam Long userId) {
        try {
            calendarService.cancelClass(classId, userId);
            return ResponseEntity.ok("Class cancelled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error cancelling class: " + e.getMessage());
        }
    }

    @PostMapping("/classes/{id}/message")
    public void sendMessageToClassClients(@PathVariable Long id, @RequestBody String message) {
        calendarService.sendMessageToClassClients(id, message);
    }
}

//sign to class from client method
//front need to send api request - api/
//calendar send request to sign to classController

//instructor create a pilates class inside the database calendar


//instructor delete pilates class

//
