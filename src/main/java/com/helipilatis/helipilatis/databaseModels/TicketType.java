package com.helipilatis.helipilatis.databaseModels;

import jakarta.persistence.*;

@Entity
@Table(name = "tickets") // Specify the table name
public class TicketType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number_of_tickets") // Map to the 'number_of_tickets' column
    private int numberOfTickets;

    @Column(name = "price") // Map to the 'price' column
    private int price;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public void setNumberOfTickets(int numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    // Print the ticket type
    @Override
    public String toString() {
        return "TicketType{" +
                "id=" + id +
                ", numberOfTickets=" + numberOfTickets +
                ", price=" + price +
                '}';
    }
}