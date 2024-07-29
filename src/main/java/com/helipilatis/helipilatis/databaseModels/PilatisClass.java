package com.helipilatis.helipilatis.databaseModels;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "calendar")
public class PilatisClass {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pilatis_class_seq")
    @SequenceGenerator(name = "pilatis_class_seq", sequenceName = "PILATIS_CLASS_SEQ", allocationSize = 1)
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int maxParticipants; // New attribute

    @Column(name = "instructor_id")
    private Long instructorId;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false, insertable = false, updatable = false)
    private Instructor instructor;

    @ManyToMany
    @JoinTable(
            name = "class_users",
            joinColumns = @JoinColumn(name = "class_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> signedUsers;

    // No-argument constructor for JPA
    public PilatisClass() {
    }

    // Parameterized constructor for convenience
    public PilatisClass(LocalDate date, LocalTime startTime, LocalTime endTime, Long instructorId, List<User> signedUsers, int maxParticipants) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.instructorId = instructorId;
        this.signedUsers = signedUsers;
        this.maxParticipants = maxParticipants;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public List<User> getSignedUsers() {
        return signedUsers;
    }

    public void setSignedUsers(List<User> signedUsers) {
        this.signedUsers = signedUsers;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
}