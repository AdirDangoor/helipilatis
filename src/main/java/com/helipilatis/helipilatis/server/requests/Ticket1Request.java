package com.helipilatis.helipilatis.server.requests;

import org.springframework.stereotype.Component;

@Component
public class Ticket1Request {
    private int ticketNum;

    // Getters and setters
    public int getTicketNum() {
        return ticketNum;
    }

    public void setTicketNum(int ticketNum) {
        this.ticketNum = ticketNum;
    }
}