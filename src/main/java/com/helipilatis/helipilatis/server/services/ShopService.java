package com.helipilatis.helipilatis.server.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.helipilatis.helipilatis.databaseModels.*;
import java.util.logging.Logger;
import com.helipilatis.helipilatis.databaseModels.TicketType;

import java.util.List;
import java.util.logging.Logger;

@Service
public class ShopService extends BaseService {

    private static final Logger logger = Logger.getLogger("ShopService");

    @Autowired
    public ShopService(UserRepository userRepository, TicketTypeRepository ticketTypeRepository) {
        this.userRepository = userRepository;
        this.ticketTypeRepository = ticketTypeRepository;
    }

    public void purchaseTicket(Long userId, Long ticketTypeId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId).orElseThrow(() -> new RuntimeException("Ticket type not found"));
        user.setTickets(user.getTickets() + ticketType.getNumberOfTickets());
        userRepository.save(user);
        logger.info("Client with userId " + userId + " bought " + ticketType.getNumberOfTickets() + " ticket(s).");
    }

    public List<TicketType> getAllTicketTypes() {
        return ticketTypeRepository.findAll();
    }
}