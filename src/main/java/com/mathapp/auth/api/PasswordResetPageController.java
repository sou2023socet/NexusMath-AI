package com.mathapp.auth.api;

import com.mathapp.auth.api.dto.ResetPasswordRequest;
import com.mathapp.auth.logging.LogService;
import com.mathapp.auth.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PasswordResetPageController {

    private final AuthService authService;
    private final LogService logService;

    public PasswordResetPageController(AuthService authService, LogService logService) {
        this.authService = authService;
        this.logService = logService;
    }

    @GetMapping("/reset-password")
    public String showResetPage(@RequestParam("token") String token, Model model) {
        logService.screenOpened("RESET_PASSWORD_PAGE", null);
        logService.apiCall("GET", "/reset-password?token=***", null);
        try {
            // Validate token before showing the reset form
            authService.validateResetToken(token);
            model.addAttribute("token", token);
            logService.operationStep("SHOW_RESET_PAGE", "TOKEN_VALID", "token accepted");
            return "reset-password";
        } catch (Exception e) {
            logService.operationFailed("SHOW_RESET_PAGE", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "reset-error";
        }
    }

    @PostMapping("/reset-password")
    public String submitResetPassword(@ModelAttribute ResetPasswordRequest request, Model model) {
        logService.screenOpened("RESET_PASSWORD_SUBMIT", null);
        logService.apiCall("POST", "/reset-password", null);
        try {
            authService.resetPassword(request);
            logService.operationSuccess("SUBMIT_RESET_PASSWORD", "password reset successful");
            logService.screenClosed("RESET_PASSWORD_PAGE", null);
            return "reset-success";
        } catch (Exception e) {
            logService.operationFailed("SUBMIT_RESET_PASSWORD", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            // Keep the token so the form can be reloaded if needed
            model.addAttribute("token", request.getToken());
            return "reset-error";
        }
    }
}
