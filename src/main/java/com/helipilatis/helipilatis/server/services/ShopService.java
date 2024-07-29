package com.helipilatis.helipilatis.server.services;

import com.helipilatis.helipilatis.databaseModels.Ticket;
import com.helipilatis.helipilatis.server.requests.Ticket1Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopService {

    @Autowired
    public void purchaseTicket(Ticket1Request ticket1Request) {
        // Get the ticket count from the request
        int ticketNum = ticket1Request.getTicketNum();

        // Print a message indicating that the client bought 1 ticket
        System.out.println("Client bought " + 1 + " ticket(s).");

        // Additional logic could go here, such as checking availability, deducting from inventory, or saving the purchase to a database
    }
}