package com.helipilatis.helipilatis.client.requests;

public class LoginResponse {
    private Long userId;
    private boolean isInstructor;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isInstructor() {
        return isInstructor;
    }

    public void setInstructor(boolean instructor) {
        isInstructor = instructor;
    }
}