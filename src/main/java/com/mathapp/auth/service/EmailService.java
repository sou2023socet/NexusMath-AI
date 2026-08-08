package com.mathapp.auth.service;

public interface EmailService {

    void sendPasswordResetEmail(
            String to,
            String userName,
            String resetLink
    );

}