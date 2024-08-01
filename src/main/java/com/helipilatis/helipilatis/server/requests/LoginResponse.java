package com.helipilatis.helipilatis.server.requests;

public class LoginResponse {
    private Long userId;
    private boolean isInstructor;

    public LoginResponse(Long userId, boolean isInstructor) {
        this.userId = userId;
        this.isInstructor = isInstructor;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isInstructor() {
        return isInstructor;
    }
}