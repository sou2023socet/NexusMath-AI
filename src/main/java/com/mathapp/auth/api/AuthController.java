package com.mathapp.auth.api;

import com.mathapp.auth.api.dto.AuthDtos;
import com.mathapp.auth.api.dto.ResetPasswordRequest;
import com.mathapp.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8080")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthDtos.RegisterRequest req) {
        try {
            return ResponseEntity.ok(authService.register(req));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDtos.LoginRequest req) {
        try {
            return ResponseEntity.ok(authService.login(req));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(e.getMessage()));
        }
    }

     @PostMapping("/forgot-password")
     public ResponseEntity<?> forgotPassword(@RequestBody AuthDtos.ForgotPasswordRequest req) {
         try {
             authService.forgotPassword(req);
             return ResponseEntity.ok(new MsgBody("If the email exists, a reset link has been sent."));
         } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(e.getMessage()));
         }
     }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // Stateless JWT: client just discards token.
        return ResponseEntity.ok(new MsgBody("Logged out"));
    }

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
		try {
			authService.resetPassword(request);
			return ResponseEntity.ok(new MsgBody("Password changed successfully"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ErrorBody(e.getMessage()));
		}
	}

    public static class ErrorBody {
        public String error;

        public ErrorBody(String error) {
            this.error = error;
        }
    }

    public static class MsgBody {
        public String message;

        public MsgBody(String message) {
            this.message = message;
        }
    }
}

