package com.helipilatis.helipilatis.server.services;

import com.helipilatis.helipilatis.databaseModels.User;
import com.helipilatis.helipilatis.databaseModels.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService {

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves the number of tickets for a user.
     * @param userId the ID of the user
     * @return the number of tickets
     */
    public int getUserTicketCount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getTickets();
    }
}