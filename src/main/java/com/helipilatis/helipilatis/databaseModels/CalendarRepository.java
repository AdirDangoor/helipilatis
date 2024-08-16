package com.helipilatis.helipilatis.databaseModels;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends JpaRepository<PilatisClass, Long> {

    @Query("SELECT c FROM PilatisClass c WHERE c.date BETWEEN :startDate AND :endDate")
    List<PilatisClass> findClassesByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT id FROM Instructor WHERE name = :name")
    Long findInstructorIdByName(@Param("name") String name);

    @Query("SELECT c FROM PilatisClass c JOIN c.signedUsers u WHERE u.id = :userId")
    List<PilatisClass> findClassesByUserId(@Param("userId") Long userId);

    @Query("SELECT MAX(c.date) FROM PilatisClass c")
    Optional<LocalDate> findLastClassDate();

    @Query("SELECT c FROM PilatisClass c WHERE c.date < CURRENT_DATE")
    List<PilatisClass> findClassesBeforeToday();

    @Query("SELECT c FROM PilatisClass c LEFT JOIN FETCH c.signedUsers WHERE c.date BETWEEN :startDate AND :endDate")
    List<PilatisClass> findClassesByDateRangeWithSignedUsers(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c FROM PilatisClass c WHERE c.date = :date AND c.startTime < :time")
    List<PilatisClass> findClassesByDateAndTimeBefore(LocalDate date, LocalTime time);

}