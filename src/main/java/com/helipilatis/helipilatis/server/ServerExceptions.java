package com.helipilatis.helipilatis.server;

public class ServerExceptions {

    public static class RegistrationException extends Exception {
        public RegistrationException(String message) {
            super(message);
        }
    }

    public static class LoginException extends Exception {
        public LoginException(String message) {
            super(message);
        }
    }

    // Add other custom exceptions here as needed
}