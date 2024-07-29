package com.helipilatis.helipilatis.server.services;

import com.helipilatis.helipilatis.databaseModels.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

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
    private CalendarMetadataRepository calendarMetadataRepository;

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

            List<PilatisClass> existingEntries = calendarRepository.findClassesByDateRange(today, endDate);
            if (!existingEntries.isEmpty()) {
                return;
            }

            CalendarMetadata metadata = calendarMetadataRepository.findById(1L).orElse(new CalendarMetadata(1L, today.minusDays(1)));
            if (!metadata.getLastInitializationDate().isBefore(today)) {
                return;
            }

            Long instructorId = getDefaultInstructorId();

            List<PilatisClass> calendarEntries = new ArrayList<>();
            LocalTime startTime = LocalTime.of(9, 0);
            LocalTime endTime = LocalTime.of(17, 0);

            for (LocalDate date = today; date.isBefore(endDate); date = date.plusDays(1)) {
                DayOfWeek day = date.getDayOfWeek();
                if (day != DayOfWeek.FRIDAY && day != DayOfWeek.SATURDAY) {
                    for (int hour = startTime.getHour(); hour < endTime.getHour(); hour++) {
                        PilatisClass calendarEntry = new PilatisClass();
                        calendarEntry.setDate(date);
                        calendarEntry.setStartTime(LocalTime.of(hour, 0));
                        calendarEntry.setEndTime(LocalTime.of(hour + 1, 0));
                        calendarEntry.setInstructorId(instructorId);
                        calendarEntry.setMaxParticipants(10);
                        calendarEntries.add(calendarEntry);
                    }
                }
            }

            logger.info("Saving calendar entries: " + calendarEntries.size());

            calendarRepository.saveAll(calendarEntries);
            metadata.setLastInitializationDate(today);
            calendarMetadataRepository.save(metadata);
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

    @Autowired
    private UserRepository userRepository;


    public void signUpForClass(Long classId, Long userId) {
        PilatisClass pilatisClass = calendarRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        if (pilatisClass.getSignedUsers().size() >= pilatisClass.getMaxParticipants()) {
            throw new IllegalStateException("Class is full");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<User> signedUsers = pilatisClass.getSignedUsers();
        if (!signedUsers.contains(user)) {
            signedUsers.add(user);
            pilatisClass.setSignedUsers(signedUsers);
            calendarRepository.save(pilatisClass);
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