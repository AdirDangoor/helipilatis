package com.helipilatis.helipilatis.databaseModels;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "calendar_metadata")
public class CalendarMetadata {
    @Id
    private Long id;
    private LocalDate lastInitializationDate;

    // No-argument constructor for JPA
    public CalendarMetadata() {
    }

    // Parameterized constructor for convenience
    public CalendarMetadata(Long id, LocalDate lastInitializationDate) {
        this.id = id;
        this.lastInitializationDate = lastInitializationDate;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getLastInitializationDate() {
        return lastInitializationDate;
    }

    public void setLastInitializationDate(LocalDate lastInitializationDate) {
        this.lastInitializationDate = lastInitializationDate;
    }
}