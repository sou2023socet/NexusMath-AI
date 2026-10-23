package com.mathapp.auth.chat;

public class ChatProviderException extends RuntimeException {
    public ChatProviderException() {
        super("The AI provider request failed.");
    }
}
