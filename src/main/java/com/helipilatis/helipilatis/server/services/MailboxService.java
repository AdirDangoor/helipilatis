package com.helipilatis.helipilatis.server.services;
import com.helipilatis.helipilatis.databaseModels.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Service
public class MailboxService extends BaseService {
    private static final Logger logger = Logger.getLogger("MailboxService");


    public void addMessageToAllUsers(String message) {
        List<User> users = userRepository.findAll();
        logger.info("users: " + users);
        for (User user : users) {
            logger.info("Adding message to user: " + user.getId());
            String inbox = user.getInbox();
            if (inbox == null) {
                inbox = "";
            }
            user.setInbox(inbox + message + "\n");
            userRepository.save(user);
        }
    }

    public String getInboxMessages(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            String inbox = user.getInbox();
            if (inbox == null) {
                return "";
            }
            String[] messages = inbox.split("\n");
            List<String> messageList = new ArrayList<>(Arrays.asList(messages));
            if (messageList.size() > 10) {
                messageList = messageList.subList(messageList.size() - 10, messageList.size());
            }
            return String.join("\n", messageList);
        } else {
            return "";
        }
    }
}