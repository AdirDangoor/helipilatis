package com.helipilatis.helipilatis.databaseModels;

import com.helipilatis.helipilatis.databaseModels.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
}