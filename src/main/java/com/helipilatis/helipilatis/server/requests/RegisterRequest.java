package com.helipilatis.helipilatis.server.requests;

public class RegisterRequest {
    private String name;
    private String phone;

    // Constructors
    public RegisterRequest() {
    }

    public RegisterRequest(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // toString method
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}