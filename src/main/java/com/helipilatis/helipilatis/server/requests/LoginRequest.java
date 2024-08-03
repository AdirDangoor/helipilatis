package com.helipilatis.helipilatis.server.requests;

public class LoginRequest {
    private String phone;
    private String name;

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}