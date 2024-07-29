package com.helipilatis.helipilatis.server.requests;

public class LoginRequest {
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                ", phone='" + phone + '\'' +
                '}';
    }
}