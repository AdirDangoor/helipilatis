package com.helipilatis.helipilatis.server.controllers;

import com.helipilatis.helipilatis.databaseModels.PilatisClass;
import com.helipilatis.helipilatis.server.services.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private static final Logger logger = Logger.getLogger("CalendarController");

    @Autowired
    private CalendarService calendarService;

    @GetMapping("/classes")
    public List<PilatisClass> getAllActiveClasses() {
        logger.info("[FUNCTION-CalendarController-getAllActiveClasses]");
        return calendarService.getAllActiveClasses();
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
            PilatisClass pilatisClass = calendarService.getClassById(classId);
            if (pilatisClass.isCanceled()) {
                return ResponseEntity.status(409).body("Class is canceled and cannot be booked");
            }
            if (pilatisClass.getSignedUsers().size() >= pilatisClass.getMaxParticipants()) {
                return ResponseEntity.status(409).body("Class is full and cannot be booked");
            }
            calendarService.signUpForClass(classId, userId);
            return ResponseEntity.ok("Successfully booked");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body("Error booking class: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error booking class");
        }
    }

    @PostMapping("/classes/{classId}/cancel")
    public ResponseEntity<String> cancelClass(@PathVariable Long classId, @RequestParam Long userId) {
        try {
            calendarService.cancelClassAsUser(classId, userId);
            return ResponseEntity.ok("Class cancelled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error cancelling class: " + e.getMessage());
        }
    }

    @PostMapping("/classes/{id}/message")
    public void sendMessageToClassClients(@PathVariable Long id, @RequestBody String message) {
        calendarService.sendMessageToClassClients(id, message);
    }

    @GetMapping("/user-classes/{userId}")
    public ResponseEntity<List<PilatisClass>> getUserClasses(@PathVariable Long userId) {
        logger.info("[FUNCTION-CalendarController-getUserClasses]");
        List<PilatisClass> userClasses = calendarService.getUserClasses(userId);
        return ResponseEntity.ok(userClasses);
    }

    @GetMapping("/instructor/classes")
    public List<PilatisClass> instructorGetAllClasses() {
        return calendarService.getAllClasses();
    }


    @PostMapping("/instructor/classes/{classId}/cancel")
    public ResponseEntity<String> cancelClass(@PathVariable Long classId) {
        boolean success = calendarService.cancelClassAsInstructor(classId);
        if (success) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(500).body("Error cancelling class");
        }
    }

    @PostMapping("/instructor/classes/{classId}/restore")
    public ResponseEntity<String> restoreClass(@PathVariable Long classId) {
        boolean success = calendarService.restoreClassAsInstructor(classId);
        if (success) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(500).body("Error restoring class");
        }
    }

}
