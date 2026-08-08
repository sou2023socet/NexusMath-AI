package com.mathapp.auth.service;

import com.mathapp.auth.api.dto.AuthDtos;
import com.mathapp.auth.api.dto.ResetPasswordRequest;
import com.mathapp.auth.model.PasswordResetToken;
import com.mathapp.auth.model.User;
import com.mathapp.auth.repo.PasswordResetTokenRepository;
import com.mathapp.auth.repo.UserRepository;
import com.mathapp.auth.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDateTime;
import java.util.UUID;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PasswordResetTokenRepository tokenRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${app.frontend.url}")
    private String frontendUrl;
    
    public AuthService(UserRepository userRepository,
            JwtService jwtService,
            EmailService emailService,
            PasswordResetTokenRepository tokenRepository) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
    }
    
	public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest req) {
        if (req == null) throw new IllegalArgumentException("Missing request");
        if (req.email == null || req.email.isBlank()) throw new IllegalArgumentException("Email required");
        if (req.username == null || req.username.isBlank()) throw new IllegalArgumentException("Username required");
        if (req.password == null || req.password.length() < 6) throw new IllegalArgumentException("Password min 6 chars");

        Optional<User> existing = userRepository.findByEmail(req.email.trim().toLowerCase());
        if (existing.isPresent()) throw new IllegalArgumentException("Email already registered");

        User u = new User();
        u.setEmail(req.email.trim().toLowerCase());
        u.setUsername(req.username.trim());
        u.setPasswordHash(passwordEncoder.encode(req.password));
        u.setRole("USER");

        userRepository.save(u);

        String token = jwtService.generateToken(u.getUsername(), u.getRole());
        return new AuthDtos.AuthResponse(token, u.getUsername(), u.getEmail());
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest req) {
        if (req == null) throw new IllegalArgumentException("Missing request");
        if (req.email == null || req.email.isBlank()) throw new IllegalArgumentException("Email required");
        if (req.password == null || req.password.isBlank()) throw new IllegalArgumentException("Password required");

        User u = userRepository.findByEmail(req.email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        boolean ok = passwordEncoder.matches(req.password, u.getPasswordHash());
        if (!ok) throw new IllegalArgumentException("Invalid credentials");

        String token = jwtService.generateToken(u.getUsername(), u.getRole());
        return new AuthDtos.AuthResponse(token, u.getUsername(), u.getEmail());
    }

    public void forgotPassword(AuthDtos.ForgotPasswordRequest req) {

        if (req == null || req.email == null || req.email.isBlank()) {
            throw new IllegalArgumentException("Email required");
        }

        Optional<User> user = userRepository.findByEmail(req.email);

        // Do not reveal whether the email exists
        if (user.isEmpty()) {
            return;
        }

        // Remove any existing reset token for this email
        tokenRepository.findByEmail(user.get().getEmail()).ifPresent(tokenRepository::delete);

        // Generate a new token
        String token = UUID.randomUUID().toString();

        // Save the token
        PasswordResetToken resetToken = new PasswordResetToken();

        resetToken.setEmail(user.get().getEmail());
        resetToken.setToken(token);
        resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(30));
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        // Build reset URL
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        
        emailService.sendPasswordResetEmail(
                user.get().getEmail(),
                user.get().getUsername(),
                resetLink);
    }

    public PasswordResetToken validateResetToken(String token) {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Invalid reset link");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset link"));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("This reset link has already been used.");
        }

        if (resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("This reset link has expired.");
        }

        return resetToken;
    }

    public void resetPassword(ResetPasswordRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.getToken() == null || request.getToken().isBlank()) {
            throw new IllegalArgumentException("Token required");
        }

        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("New password required");
        }

        if (request.getConfirmPassword() == null || request.getConfirmPassword().isBlank()) {
            throw new IllegalArgumentException("Confirm password required");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        PasswordResetToken resetToken = validateResetToken(request.getToken());

        User user = userRepository.findByEmail(resetToken.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));

        String encryptedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPasswordHash(encryptedPassword);
        userRepository.save(user);
        resetToken.setUsed(true);

        tokenRepository.save(resetToken);

    }
    
}

