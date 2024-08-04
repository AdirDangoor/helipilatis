package com.helipilatis.helipilatis.databaseModels;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1)
    private Long id;

    private String phone;
    private String name;
    private int age;
    private String gender;
    private int tickets; //mention the number of tickets client owns

    @Column(name = "inbox")
    private String inbox;

    // Default constructor for JPA
    public User() {
    }

    public User(String phone, String name, int age, String gender, int tickets) {
        this.phone = phone;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.tickets = tickets;
    }

    public User(String phone, String name, int age, String gender) {
        this.phone = phone;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getTickets() {
        return tickets;
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }

    public String getInbox() {
        return inbox;
    }

    public void setInbox(String inbox) {
        this.inbox = inbox;
    }
}