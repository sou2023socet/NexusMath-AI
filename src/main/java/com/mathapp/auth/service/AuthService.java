package com.mathapp.auth.service;

import com.mathapp.auth.api.dto.AuthDtos;
import com.mathapp.auth.api.dto.ResetPasswordRequest;
import com.mathapp.auth.logging.LogService;
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
    private final LogService logService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${app.frontend.url}")
    private String frontendUrl;
    
    public AuthService(UserRepository userRepository,
            JwtService jwtService,
            EmailService emailService,
            PasswordResetTokenRepository tokenRepository,
            LogService logService) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.logService = logService;
    }
    
	public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest req) {
        logService.operationStarted("REGISTER", req != null ? req.email : null);
        if (req == null) throw failed("REGISTER", "Missing request");
        if (req.email == null || req.email.isBlank()) throw failed("REGISTER", "Email required");
        if (req.username == null || req.username.isBlank()) throw failed("REGISTER", "Username required");
        if (req.password == null || req.password.length() < 6) throw failed("REGISTER", "Password min 6 chars");

        logService.operationStep("REGISTER", "CHECK_DUPLICATE", "email=" + req.email);
        Optional<User> existing = userRepository.findByEmail(req.email.trim().toLowerCase());
        if (existing.isPresent()) throw failed("REGISTER", "Email already registered");

        logService.operationStep("REGISTER", "SAVE_USER", "email=" + req.email + ", username=" + req.username);
        User u = new User();
        u.setEmail(req.email.trim().toLowerCase());
        u.setUsername(req.username.trim());
        u.setPasswordHash(passwordEncoder.encode(req.password));
        u.setRole("USER");

        userRepository.save(u);

        logService.operationStep("REGISTER", "GENERATE_TOKEN", "for username=" + u.getUsername());
        String token = jwtService.generateToken(u.getUsername(), u.getRole());
        logService.operationSuccess("REGISTER", "user registered: " + u.getEmail());
        return new AuthDtos.AuthResponse(token, u.getUsername(), u.getEmail());
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest req) {
        logService.operationStarted("LOGIN", req != null ? req.email : null);
        if (req == null) throw failed("LOGIN", "Missing request");
        if (req.email == null || req.email.isBlank()) throw failed("LOGIN", "Email required");
        if (req.password == null || req.password.isBlank()) throw failed("LOGIN", "Password required");

        logService.operationStep("LOGIN", "FIND_USER", "email=" + req.email);
        User u = userRepository.findByEmail(req.email.trim().toLowerCase())
                .orElseThrow(() -> failed("LOGIN", "Invalid credentials"));

        logService.operationStep("LOGIN", "VERIFY_PASSWORD", "email=" + req.email);
        boolean ok = passwordEncoder.matches(req.password, u.getPasswordHash());
        if (!ok) throw failed("LOGIN", "Invalid credentials");

        logService.operationStep("LOGIN", "GENERATE_TOKEN", "for username=" + u.getUsername());
        String token = jwtService.generateToken(u.getUsername(), u.getRole());
        logService.operationSuccess("LOGIN", "user logged in: " + u.getEmail());
        return new AuthDtos.AuthResponse(token, u.getUsername(), u.getEmail());
    }

    public void forgotPassword(AuthDtos.ForgotPasswordRequest req) {
        logService.operationStarted("FORGOT_PASSWORD", req != null ? req.email : null);

        if (req == null || req.email == null || req.email.isBlank()) {
            throw failed("FORGOT_PASSWORD", "Email required");
        }

        logService.operationStep("FORGOT_PASSWORD", "FIND_USER", "email=" + req.email);
        Optional<User> user = userRepository.findByEmail(req.email);

        // Do not reveal whether the email exists
        if (user.isEmpty()) {
            logService.operationWarning("FORGOT_PASSWORD", "No user found for email; suppressing response");
            return;
        }

        logService.operationStep("FORGOT_PASSWORD", "CLEAR_OLD_TOKEN", "email=" + req.email);
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

        logService.operationStep("FORGOT_PASSWORD", "SAVE_TOKEN", "for email=" + req.email);
        tokenRepository.save(resetToken);

        // Build reset URL
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        
        logService.operationStep("FORGOT_PASSWORD", "SEND_EMAIL", "to=" + user.get().getEmail());
        emailService.sendPasswordResetEmail(
                user.get().getEmail(),
                user.get().getUsername(),
                resetLink);

        logService.operationSuccess("FORGOT_PASSWORD", "reset email sent to " + user.get().getEmail());
    }

    public PasswordResetToken validateResetToken(String token) {

        logService.operationStarted("VALIDATE_RESET_TOKEN", null);
        if (token == null || token.isBlank()) {
            throw failed("VALIDATE_RESET_TOKEN", "Invalid reset link");
        }

        logService.operationStep("VALIDATE_RESET_TOKEN", "FIND_TOKEN", "token provided");
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> failed("VALIDATE_RESET_TOKEN", "Invalid reset link"));

        if (resetToken.isUsed()) {
            throw failed("VALIDATE_RESET_TOKEN", "This reset link has already been used.");
        }

        if (resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw failed("VALIDATE_RESET_TOKEN", "This reset link has expired.");
        }

        logService.operationSuccess("VALIDATE_RESET_TOKEN", "token valid for email=" + resetToken.getEmail());
        return resetToken;
    }

    public void resetPassword(ResetPasswordRequest request) {

        logService.operationStarted("RESET_PASSWORD", request != null ? request.getToken() : null);
        if (request == null) {
            throw failed("RESET_PASSWORD", "Request cannot be null");
        }

        if (request.getToken() == null || request.getToken().isBlank()) {
            throw failed("RESET_PASSWORD", "Token required");
        }

        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw failed("RESET_PASSWORD", "New password required");
        }

        if (request.getConfirmPassword() == null || request.getConfirmPassword().isBlank()) {
            throw failed("RESET_PASSWORD", "Confirm password required");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw failed("RESET_PASSWORD", "Passwords do not match");
        }

        logService.operationStep("RESET_PASSWORD", "VALIDATE_TOKEN", "token provided");
        PasswordResetToken resetToken = validateResetToken(request.getToken());

        logService.operationStep("RESET_PASSWORD", "FIND_USER", "email=" + resetToken.getEmail());
        User user = userRepository.findByEmail(resetToken.getEmail()).orElseThrow(() -> failed("RESET_PASSWORD", "User not found"));

        logService.operationStep("RESET_PASSWORD", "UPDATE_PASSWORD", "for user " + user.getEmail());
        String encryptedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPasswordHash(encryptedPassword);
        userRepository.save(user);
        resetToken.setUsed(true);

        tokenRepository.save(resetToken);

        logService.operationSuccess("RESET_PASSWORD", "password updated for " + user.getEmail());
    }

    private IllegalArgumentException failed(String operation, String reason) {
        logService.operationFailed(operation, reason);
        return new IllegalArgumentException(reason);
    }
    
}

