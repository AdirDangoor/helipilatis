package com.helipilatis.helipilatis.server.controllers;

import com.helipilatis.helipilatis.databaseModels.PilatisClass;
import com.helipilatis.helipilatis.server.ServerExceptions;
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

    /**
     * Handles GET request for all active classes
     * used by user only on user view file
     * @return List of all active classes
     */
    @GetMapping("/classes")
    public List<PilatisClass> getAllActiveClasses() {
        try {
            return calendarService.getAllActiveClasses();
        } catch (Exception e) {
            logger.severe("Error getting all active classes: " + e.getMessage());
            return List.of();
        }
    }


    /**
     * Handles POST request for signing up for a class
     * used by user only on user view file
     * @param classId the id of the class to sign up for
     * @param userId the id of the user signing up
     * @return ResponseEntity with status code and message
     */
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
        }catch (ServerExceptions.NotEnoughTicketsException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error booking class");
        }
    }

    /**
     * Handles POST request for cancelling a class
     * used by user only on user view file
     * @param classId the id of the class to cancel
     * @param userId the id of the user cancelling
     * @return ResponseEntity with status code and message
     */
    @PostMapping("/classes/{classId}/cancel")
    public ResponseEntity<String> cancelClass(@PathVariable Long classId, @RequestParam Long userId) {
        try {
            calendarService.cancelClassAsUser(classId, userId);
            return ResponseEntity.ok("Class cancelled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error cancelling class: " + e.getMessage());
        }
    }


    /**
     * Handles GET request for all classes of a user
     * used by user only on user view file
     * @param userId the id of the user to get classes for
     * @return ResponseEntity with status code and list of classes
     */
    @GetMapping("/user-classes/{userId}")
    public ResponseEntity<List<PilatisClass>> getUserClasses(@PathVariable Long userId) {
        try {
            List<PilatisClass> userClasses = calendarService.getUserClasses(userId);
            return ResponseEntity.ok(userClasses);
        } catch (Exception e) {
            logger.severe("Error getting user classes: " + e.getMessage());
            return ResponseEntity.status(500).body(List.of());
        }
    }

    /**
     * Handles GET request for all classes by instructor
     * it returns all classes including canceled ones
     * used by instructor only on instructor view file
     * @return ResponseEntity with status code and list of classes
     */
    @GetMapping("/instructor/classes")
    public List<PilatisClass> instructorGetAllClasses() {
        try {
            return calendarService.getAllClasses();
        } catch (Exception e) {
            logger.severe("Error getting all classes: " + e.getMessage());
            return List.of();
        }
    }


    /**
     * Handles POST request for cancelling a class
     * used by instructor only on instructor view file
     * @param classId the id of the class to cancel
     * @return ResponseEntity with status code and message
     */
    @PostMapping("/instructor/classes/{classId}/cancel")
    public ResponseEntity<String> cancelClass(@PathVariable Long classId) {
        try {
            calendarService.cancelClassAsInstructor(classId);
            return ResponseEntity.ok("Class cancelled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error cancelling class: " + e.getMessage());
        }
    }

    /**
     * Handles POST request for restoring a class
     * used by instructor only on instructor view file
     * @param classId the id of the class to restore
     * @return ResponseEntity with status code and message
     */
    @PostMapping("/instructor/classes/{classId}/restore")
    public ResponseEntity<String> restoreClass(@PathVariable Long classId) {
        try {
            calendarService.restoreClassAsInstructor(classId);
            return ResponseEntity.ok("Class restored successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error restoring class: " + e.getMessage());
        }
    }

    /**
     * Handles GET request for getting all users signed up for a class
     * used by instructor only on instructor view file
     * @param classId the id of the class to get users for
     * @return ResponseEntity with status code and list of user names
     */
    @GetMapping("/instructor/{classId}/users")
    public ResponseEntity<List<String>> getUserNamesForClass(@PathVariable Long classId) {
        try {
            List<String> userNames = calendarService.getUserNamesForClass(classId);
            return ResponseEntity.ok(userNames);
        } catch (Exception e) {
            logger.severe("Error getting user names for class: " + e.getMessage());
            return ResponseEntity.status(500).body(List.of());
        }
    }



}
