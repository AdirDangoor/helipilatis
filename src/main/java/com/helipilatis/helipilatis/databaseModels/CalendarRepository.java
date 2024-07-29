package com.helipilatis.helipilatis.databaseModels;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CalendarRepository extends JpaRepository<PilatisClass, Long> {

    @Query("SELECT c FROM PilatisClass c WHERE c.date BETWEEN :startDate AND :endDate")
    List<PilatisClass> findClassesByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT id FROM Instructor WHERE name = :name")
    Long findInstructorIdByName(@Param("name") String name);
}