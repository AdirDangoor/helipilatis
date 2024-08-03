package com.helipilatis.helipilatis.server.requests;

public class LoginResponse {
    private Long userId;
    private boolean isInstructor;
    private String name;

    public LoginResponse(Long userId, boolean isInstructor, String name) {
        this.userId = userId;
        this.isInstructor = isInstructor;
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isInstructor() {
        return isInstructor;
    }

    public String getName() {
        return name;
    }
}