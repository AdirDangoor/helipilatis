package com.helipilatis.helipilatis.databaseModels;

import com.helipilatis.helipilatis.databaseModels.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}