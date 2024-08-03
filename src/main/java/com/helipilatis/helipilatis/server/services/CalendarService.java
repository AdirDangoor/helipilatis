package com.helipilatis.helipilatis.server.services;

import com.helipilatis.helipilatis.databaseModels.*;
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
public class CalendarService {

    private static final java.util.logging.Logger logger = Logger.getLogger(CalendarService.class.getName());

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void initializeCalendarOnStartup() {
        initializeCalendar();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void initializeCalendarDaily() {
        initializeCalendar();
    }

    public void initializeCalendar() {
        try {
            logger.info("Initializing calendar");
            LocalDate today = LocalDate.now();
            LocalDate endDate = today.plusWeeks(4);
            LocalDate yesterday = today.minusDays(1);
            logger.info("Today: " + today + ", end date: " + endDate + ", yesterday: " + yesterday);
            // Remove all classes with dates that have passed
            List<PilatisClass> pastClasses = calendarRepository.findClassesByDateRangeWithSignedUsers(today.minusWeeks(1), yesterday);
            logger.info("Past classes: " + pastClasses);

            if (!pastClasses.isEmpty()) {
                calendarRepository.deleteAll(pastClasses);
                logger.info("Removed past classes: " + pastClasses.size());
            }

            LocalDate llastClassDate = calendarRepository.findLastClassDate().orElse(today.minusDays(1));
            logger.info("Last class date: " + llastClassDate);
            // Fetch the last class date
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
            e.printStackTrace();
        }
    }

    private Long getDefaultInstructorId() {
        return calendarRepository.findInstructorIdByName("kaganov");
    }

    public List<PilatisClass> getAllClasses() {
        return calendarRepository.findAll();
    }

    public List<PilatisClass> getAllActiveClasses() {
        logger.info("Getting all active classes");
        return calendarRepository.findAll().stream()
                .filter(pilatisClass -> !pilatisClass.isCanceled())
                .collect(Collectors.toList());
    }

    public PilatisClass saveClass(PilatisClass pilatisClass) {
        return calendarRepository.save(pilatisClass);
    }

    public PilatisClass updateClass(Long id, PilatisClass pilatisClass) {
        pilatisClass.setId(id);
        return calendarRepository.save(pilatisClass);
    }

    public void deleteClass(Long id) {
        calendarRepository.deleteById(id);
    }

    public List<PilatisClass> getUserClasses(Long userId) {
        return calendarRepository.findClassesByUserId(userId);
    }


    public PilatisClass getClassById(Long classId) {
        Optional<PilatisClass> optionalClass = calendarRepository.findById(classId);
        if (optionalClass.isPresent()) {
            return optionalClass.get();
        } else {
            throw new IllegalArgumentException("Class not found with id: " + classId);
        }
    }

    public void signUpForClass(Long classId, Long userId) {
        PilatisClass pilatisClass = calendarRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        if (pilatisClass.getSignedUsers().size() >= pilatisClass.getMaxParticipants()) {
            throw new IllegalStateException("Class is full");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getTickets() <= 0) {
            throw new IllegalStateException("User does not have enough tickets");
        }

        List<User> signedUsers = pilatisClass.getSignedUsers();
        if (!signedUsers.contains(user)) {
            signedUsers.add(user);
            pilatisClass.setSignedUsers(signedUsers);
            user.setTickets(user.getTickets() - 1);
            userRepository.save(user);
            calendarRepository.save(pilatisClass);
        }
    }



    public void cancelClassAsUser(Long classId, Long userId) {
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
    }


    public boolean cancelClassAsInstructor(Long classId) {
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
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean restoreClassAsInstructor(Long classId) {
        try {
            PilatisClass pilatisClass = calendarRepository.findById(classId).orElseThrow();
            pilatisClass.setCanceled(false);
            calendarRepository.save(pilatisClass);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public List<String> getUserNamesForClass(Long classId) {
        PilatisClass pilatisClass = calendarRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        return pilatisClass.getSignedUsers().stream().map(User::getName).collect(Collectors.toList());
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