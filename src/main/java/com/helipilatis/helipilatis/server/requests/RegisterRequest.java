package com.helipilatis.helipilatis.server.requests;

public class RegisterRequest {
    private String name;
    private String phone;
    private int age;
    private String gender;
    private int tickets;

    // Constructors
    public RegisterRequest() {
    }

    public RegisterRequest(String name, int age, String gender, int tickets) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.tickets = tickets;
    }

    public RegisterRequest(String phone, String name, int age, String gender) {
        this.phone = phone;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    // Getters and Setters
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

    // toString method
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", tickets=" + tickets +
                '}';
    }
}