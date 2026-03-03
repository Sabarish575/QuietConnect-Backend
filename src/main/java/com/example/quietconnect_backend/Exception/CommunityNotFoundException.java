package com.example.quietconnect_backend.Exception;

public class CommunityNotFoundException extends RuntimeException {

    public CommunityNotFoundException(String message) {
        super(message);
    }

    public CommunityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
