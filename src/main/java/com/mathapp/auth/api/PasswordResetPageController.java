package com.mathapp.auth.api;

import com.mathapp.auth.api.dto.ResetPasswordRequest;
import com.mathapp.auth.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PasswordResetPageController {

    private final AuthService authService;

    public PasswordResetPageController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/reset-password")
    public String showResetPage(@RequestParam("token") String token, Model model) {
        try {
            // Validate token before showing the reset form
            authService.validateResetToken(token);
            model.addAttribute("token", token);
            return "reset-password";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "reset-error";
        }
    }

    @PostMapping("/reset-password")
    public String submitResetPassword(@ModelAttribute ResetPasswordRequest request, Model model) {
        try {
            authService.resetPassword(request);
            return "reset-success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            // Keep the token so the form can be reloaded if needed
            model.addAttribute("token", request.getToken());
            return "reset-error";
        }
    }
}