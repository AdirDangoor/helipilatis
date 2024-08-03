package com.helipilatis.helipilatis.server.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.helipilatis.helipilatis.databaseModels.*;
import java.util.logging.Logger;

@Service
public class ShopService {

    private final UserRepository userRepository;
    private static final java.util.logging.Logger logger = Logger.getLogger("ShopService");

    @Autowired
    public ShopService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void purchaseTicket(Long userId, int ticket) {
        // Find the user by ID
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Update the user's ticket count
        user.setTickets(user.getTickets() + ticket);

        // Save the updated user back to the database
        userRepository.save(user);

        // Print a message indicating that the client bought tickets
        logger.info("Client with userId " + userId + " bought " + ticket + " ticket(s).");
    }
}