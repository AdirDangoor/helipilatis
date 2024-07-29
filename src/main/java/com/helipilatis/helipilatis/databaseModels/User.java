package com.helipilatis.helipilatis.databaseModels;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1)
    private Long id;

    private String phone;
    private String username;
    private String email;
    private String password;
    private int age;
    private String gender;
    private int tickets; //mention the number of tickets client owns

    // Default constructor for JPA
    public User() {
    }

    public User(String username, String email, String password, int age, String gender, int tickets) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.age = age;
        this.gender = gender;
        this.tickets = tickets;
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String phone, String name, int age, String gender) {
        this.phone = phone;
        this.username = name;
        this.age = age;
        this.gender = gender;
    }

// Getters and setters


    public void setPhone(String phone){
            this.phone = phone;
    }
    public String getPhone(){
        return phone;
    }

    public String getName(){
        return username;
    }
    public void setName(String name){
        this.username = name;
    }
    public int getAge(){
        return age;
    }
    public void setAge(int age){
        this.age = age;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTickets() {
        return tickets;
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }
}
