package com.mathapp.auth.api;

import com.mathapp.auth.api.dto.AuthDtos;
import com.mathapp.auth.api.dto.ResetPasswordRequest;
import com.mathapp.auth.logging.LogService;
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
    private final LogService logService;

    public AuthController(AuthService authService, LogService logService) {
        this.authService = authService;
        this.logService = logService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthDtos.RegisterRequest req) {
        String user = req != null ? req.email : null;
        logService.apiCall("POST", "/api/auth/register", user);
        try {
            return ResponseEntity.ok(authService.register(req));
        } catch (Exception e) {
            logService.operationFailed("REGISTER", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDtos.LoginRequest req) {
        String user = req != null ? req.email : null;
        logService.apiCall("POST", "/api/auth/login", user);
        try {
            return ResponseEntity.ok(authService.login(req));
        } catch (Exception e) {
            logService.operationFailed("LOGIN", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(e.getMessage()));
        }
    }

     @PostMapping("/forgot-password")
     public ResponseEntity<?> forgotPassword(@RequestBody AuthDtos.ForgotPasswordRequest req) {
         String user = req != null ? req.email : null;
         logService.apiCall("POST", "/api/auth/forgot-password", user);
         try {
             authService.forgotPassword(req);
             return ResponseEntity.ok(new MsgBody("If the email exists, a reset link has been sent."));
         } catch (Exception e) {
             logService.operationFailed("FORGOT_PASSWORD", e.getMessage(), e);
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(e.getMessage()));
         }
     }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // Stateless JWT: client just discards token.
        logService.apiCall("POST", "/api/auth/logout", null);
        logService.operationSuccess("LOGOUT", "session ended by client");
        return ResponseEntity.ok(new MsgBody("Logged out"));
    }

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
		logService.apiCall("POST", "/api/auth/reset-password", request != null ? request.getToken() : null);
		try {
			authService.resetPassword(request);
			return ResponseEntity.ok(new MsgBody("Password changed successfully"));
		} catch (Exception e) {
			logService.operationFailed("RESET_PASSWORD", e.getMessage(), e);
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

