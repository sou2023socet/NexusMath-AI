package com.mathapp.auth.chat;

import org.springframework.http.HttpStatus;

public class ChatSessionException extends RuntimeException {
    private final HttpStatus status;

    public ChatSessionException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus status() {
        return status;
    }
}
