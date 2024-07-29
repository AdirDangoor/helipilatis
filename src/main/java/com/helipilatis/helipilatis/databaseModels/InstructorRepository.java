package com.helipilatis.helipilatis.databaseModels;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    Optional<Instructor> findByPhone(String phone);
}