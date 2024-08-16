package com.helipilatis.helipilatis.server.services;

import com.helipilatis.helipilatis.databaseModels.*;
import com.helipilatis.helipilatis.server.ServerExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CalendarService extends BaseService {

    private static final java.util.logging.Logger logger = Logger.getLogger("CalendarService");


    /**
     * Initializes the calendar on startup
     */
    @PostConstruct
    public void initializeCalendarOnStartup() {
        initializeCalendar();
    }

    /**
     * Initializes the calendar by adding classes for the next 4 weeks
     * This method is scheduled to run every day at midnight
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void initializeCalendarDaily() {
        initializeCalendar();
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void updateClassesHourly() {
        removePastClassesForToday();
    }

    /**
     * Initializes the calendar by adding classes for the next 4 weeks
     */
    public void initializeCalendar() {
        try {
            logger.info("Initializing calendar");
            LocalDate today = LocalDate.now();
            LocalDate endDate = today.plusWeeks(4);
            LocalDate yesterday = today.minusDays(1);
            // Remove all classes with dates that have passed
            List<PilatisClass> pastClasses = calendarRepository.findClassesByDateRangeWithSignedUsers(today.minusWeeks(1), yesterday);

            if (!pastClasses.isEmpty()) {
                calendarRepository.deleteAll(pastClasses);
                logger.info("Removed past classes: " + pastClasses.size());
            }

            LocalDate lastClassDate = calendarRepository.findLastClassDate().orElse(today.minusDays(1));
            if (!lastClassDate.isBefore(endDate)) {
                return;
            }

            Long instructorId = getDefaultInstructorId();

            List<PilatisClass> calendarEntries = new ArrayList<>();
            LocalTime startTime = LocalTime.of(9, 0);
            LocalTime endTime = LocalTime.of(17, 0);

            // Add new classes to fill the gap
            for (LocalDate date = lastClassDate.plusDays(1); date.isBefore(endDate); date = date.plusDays(1)) {
                DayOfWeek day = date.getDayOfWeek();
                if (day != DayOfWeek.FRIDAY && day != DayOfWeek.SATURDAY) {
                    for (int hour = startTime.getHour(); hour < endTime.getHour(); hour++) {
                        PilatisClass calendarEntry = new PilatisClass();
                        calendarEntry.setDate(date);
                        calendarEntry.setStartTime(LocalTime.of(hour, 0));
                        calendarEntry.setEndTime(LocalTime.of(hour + 1, 0));
                        calendarEntry.setInstructorId(instructorId);
                        calendarEntry.setMaxParticipants(10);
                        calendarEntry.setCanceled(false);
                        calendarEntries.add(calendarEntry);
                    }
                }
            }

            logger.info("Saving calendar entries: " + calendarEntries.size());
            calendarRepository.saveAll(calendarEntries);
        } catch (Exception e) {
            logger.severe("Failed to initialize calendar: " + e.getMessage());
        }
    }


    private void removePastClassesForToday() {
        try {
            logger.info("Removing past classes for today");
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();

            List<PilatisClass> pastClasses = calendarRepository.findClassesByDateAndTimeBefore(today, now);
            if (!pastClasses.isEmpty()) {
                calendarRepository.deleteAll(pastClasses);
                logger.info("Removed past classes for today: " + pastClasses.size());
            }
        } catch (Exception e) {
            logger.severe("Failed to remove past classes for today: " + e.getMessage());
        }
    }


    /**
     * Fetches the default instructor ID
     * @return the default instructor ID
     */
    private Long getDefaultInstructorId() {
        try {
            return calendarRepository.findInstructorIdByName("kaganov");
        } catch (Exception e) {
            logger.severe("Failed to fetch default instructor ID: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Fetches all classes
     * @return a list of all classes
     */
    public List<PilatisClass> getAllClasses() {
        try {
            return calendarRepository.findAll();
        } catch (Exception e) {
            logger.severe("Failed to fetch all classes: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Fetches all active classes
     * @return a list of all active classes
     */
    public List<PilatisClass> getAllActiveClasses() {
        try {
            return calendarRepository.findAll().stream()
                    .filter(pilatisClass -> !pilatisClass.isCanceled())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.severe("Failed to fetch all active classes: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Fetches all classes for a specific user
     * @return a list of all classes for the user
     */
    public List<PilatisClass> getUserClasses(Long userId) {
        try {
            return calendarRepository.findClassesByUserId(userId);
        } catch (Exception e) {
            logger.severe("Failed to fetch user classes: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Fetches class for a specific id
     * @param classId the id of the class
     * @return the class
     */
    public PilatisClass getClassById(Long classId) {
        try {
            Optional<PilatisClass> optionalClass = calendarRepository.findById(classId);
            if (optionalClass.isPresent()) {
                return optionalClass.get();
            } else {
                throw new IllegalArgumentException("Class not found with id: " + classId);
            }
        } catch (Exception e) {
            logger.severe("Failed to fetch class by id: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Signs up a user for a class
     * @param classId the id of the class
     * @param userId the id of the user
     */
    public void signUpForClass(Long classId, Long userId) throws ServerExceptions.NotEnoughTicketsException {
        try{
            PilatisClass pilatisClass = calendarRepository.findById(classId)
                    .orElseThrow(() -> new IllegalArgumentException("Class not found"));

            if (pilatisClass.getSignedUsers().size() >= pilatisClass.getMaxParticipants()) {
                throw new IllegalStateException("Class is full");
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (user.getTickets() <= 0) {
                throw new ServerExceptions.NotEnoughTicketsException("User does not have enough tickets");
            }

            List<User> signedUsers = pilatisClass.getSignedUsers();
            if (!signedUsers.contains(user)) {
                signedUsers.add(user);
                pilatisClass.setSignedUsers(signedUsers);
                user.setTickets(user.getTickets() - 1);
                userRepository.save(user);
                calendarRepository.save(pilatisClass);
            }
        } catch (Exception e) {
            logger.severe("Failed to sign up for class: " + e.getMessage());
            throw e;
        }
    }



    public void cancelClassAsUser(Long classId, Long userId) {
        try{
            PilatisClass pilatisClass = calendarRepository.findById(classId)
                    .orElseThrow(() -> new RuntimeException("Class not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!pilatisClass.getSignedUsers().contains(user)) {
                throw new RuntimeException("User not signed up for this class");
            }

            pilatisClass.getSignedUsers().remove(user);
            user.setTickets(user.getTickets() + 1);
            userRepository.save(user);
            calendarRepository.save(pilatisClass);
        } catch (Exception e) {
            logger.severe("Failed to cancel class as user: " + e.getMessage());
            throw e;
        }
    }


    public void cancelClassAsInstructor(Long classId) {
        try{
            PilatisClass pilatisClass = calendarRepository.findById(classId)
                    .orElseThrow(() -> new RuntimeException("Class not found"));

            List<User> signedUsers = pilatisClass.getSignedUsers();
            for (User user : signedUsers) {
                user.setTickets(user.getTickets() + 1);
                userRepository.save(user);
            }

            pilatisClass.getSignedUsers().clear();
            pilatisClass.setCanceled(true);
            calendarRepository.save(pilatisClass);
        } catch (Exception e) {
            logger.severe("Failed to cancel class as instructor: " + e.getMessage());
            throw e;
        }
    }

    public void restoreClassAsInstructor(Long classId) {
        try {
            PilatisClass pilatisClass = calendarRepository.findById(classId).orElseThrow();
            pilatisClass.setCanceled(false);
            calendarRepository.save(pilatisClass);
        } catch (Exception e) {
            logger.severe("Failed to restore class as instructor: " + e.getMessage());
            throw e;
        }
    }


    public List<String> getUserNamesForClass(Long classId) {
        try {
            PilatisClass pilatisClass = calendarRepository.findById(classId)
                    .orElseThrow(() -> new RuntimeException("Class not found"));
            return pilatisClass.getSignedUsers().stream().map(User::getName).collect(Collectors.toList());
        } catch (Exception e) {
            logger.severe("Failed to get user names for class: " + e.getMessage());
            throw e;
        }
    }

    // CalendarService.java
    public void updateParticipants(Long classId, int newParticipants) {
        try {
            PilatisClass pilatisClass = calendarRepository.findById(classId)
                    .orElseThrow(() -> new IllegalArgumentException("Class not found"));
            pilatisClass.setMaxParticipants(newParticipants);
            calendarRepository.save(pilatisClass);
        } catch (Exception e) {
            logger.severe("Failed to update participants: " + e.getMessage());
            throw e;
        }
    }

    public void sendMessageToClassClients(Long classId, String message) {
        PilatisClass pilatisClass = calendarRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        List<User> signedUsers = pilatisClass.getSignedUsers();
        for (User user : signedUsers) {
            // Implement the logic to send a message to the user
            // For example, you could use an email service or a notification service
            sendNotification(user, message);
        }
    }

    private void sendNotification(User user, String message) {
        // Implement the actual message sending logic here
        // This could be an email, SMS, push notification, etc.
    }

}