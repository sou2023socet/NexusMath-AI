package com.mathapp.auth.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Centralized logging service for MathApp Auth Backend.
 * Provides convenience wrappers for screen events, business operations,
 * API calls, and standard log levels, with built-in redaction of
 * sensitive values (passwords, tokens).
 */
@Service
public class LogService {

    private static final Logger log =
            LoggerFactory.getLogger(LogService.class);

    private static final String[] SENSITIVE_KEYS = {
            "password", "token", "secret", "authorization"
    };

    // ==========================================================
    //  Standard levels
    // ==========================================================

    public void info(String message) {
        log.info(message);
    }

    public void info(String message, Object... args) {
        log.info(message, args);
    }

    public void debug(String message) {
        log.debug(message);
    }

    public void debug(String message, Object... args) {
        log.debug(message, args);
    }

    public void warn(String message) {
        log.warn(message);
    }

    public void warn(String message, Object... args) {
        log.warn(message, args);
    }

    public void error(String message) {
        log.error(message);
    }

    public void error(String message, Exception ex) {
        log.error(message, ex);
    }

    // ==========================================================
    //  Screen events (open / close)
    // ==========================================================

    public void screenOpened(String screenName, String user) {
        log.info("""

                ============== SCREEN ==================
                Event    : OPENED
                Screen   : {}
                User     : {}
                ========================================
                """, screenName, redact(user));
    }

    public void screenClosed(String screenName, String user) {
        log.info("""

                ============== SCREEN ==================
                Event    : CLOSED
                Screen   : {}
                User     : {}
                ========================================
                """, screenName, redact(user));
    }

    // ==========================================================
    //  Business operations
    // ==========================================================

    public void operationStarted(String operation, String user) {
        log.info("""

                ------- OPERATION START ----------------
                Operation : {}
                User      : {}
                ----------------------------------------
                """, operation, redact(user));
    }

    public void operationSuccess(String operation, String detail) {
        log.info("""

                ------- OPERATION SUCCESS --------------
                Operation : {}
                Detail    : {}
                ----------------------------------------
                """, operation, redact(detail));
    }

    public void operationFailed(String operation, String reason) {
        log.error("""

                ------- OPERATION FAILED ---------------
                Operation : {}
                Reason    : {}
                ----------------------------------------
                """, operation, redact(reason));
    }

    public void operationFailed(String operation, String reason, Exception ex) {
        log.error("""

                ------- OPERATION FAILED ---------------
                Operation : {}
                Reason    : {}
                ----------------------------------------
                """, operation, redact(reason), ex);
    }

    public void operationStep(String operation, String step, String detail) {
        log.debug("""

                ------- OPERATION STEP -----------------
                Operation : {}
                Step      : {}
                Detail    : {}
                ----------------------------------------
                """, operation, step, redact(detail));
    }

    public void operationWarning(String operation, String warning) {
        log.warn("""

                ------- OPERATION WARNING --------------
                Operation : {}
                Warning   : {}
                ----------------------------------------
                """, operation, redact(warning));
    }

    // ==========================================================
    //  API calls
    // ==========================================================

    public void apiCall(String method, String uri, String user) {
        log.info("""

                ============== API CALL ================
                Method : {}
                URI    : {}
                User   : {}
                ========================================
                """, method, uri, redact(user));
    }

    public void api(String method, String uri, int status, long time) {
        log.info("""

                ============== API LOG =================
                Method : {}
                URI    : {}
                Status : {}
                Time   : {} ms
                ========================================
                """, method, uri, status, time);
    }

    public void apiError(String method, String uri, int status, String body) {
        log.error("""

                ============== API ERROR ===============
                Method : {}
                URI    : {}
                Status : {}
                Body   : {}
                ========================================
                """, method, uri, status, redact(body));
    }

    // ==========================================================
    //  Database
    // ==========================================================

    public void databaseSuccess(String url, String database, String user) {
        log.info("""

                =============== DATABASE ===============
                Status   : CONNECTED
                URL      : {}
                Database : {}
                User     : {}
                ========================================
                """, url, database, user);
    }

    public void databaseFailed(Exception ex) {
        log.error("""

                =============== DATABASE ===============
                Status : FAILED
                Message: {}
                ========================================
                """, ex.getMessage(), ex);
    }

    // ==========================================================
    //  Redaction helper
    // ==========================================================

    /**
     * Redact sensitive values from a detail string before logging.
     * Masks any value that appears after a sensitive key such as
     * password, token, secret, or authorization.
     */
    private String redact(String detail) {
        if (detail == null || detail.isBlank()) {
            return detail;
        }
        String result = detail;
        String lower = detail.toLowerCase();
        for (String key : SENSITIVE_KEYS) {
            int idx = lower.indexOf(key);
            while (idx != -1) {
                // Skip the key itself and find the value separator
                int valueStart = idx + key.length();
                while (valueStart < result.length()
                        && (result.charAt(valueStart) == ' ' || result.charAt(valueStart) == '=')) {
                    valueStart++;
                }
                int valueEnd = valueStart;
                while (valueEnd < result.length()
                        && result.charAt(valueEnd) != ' ' && result.charAt(valueEnd) != ',' && result.charAt(valueEnd) != '\n') {
                    valueEnd++;
                }
                String masked = result.substring(0, valueStart) + "*****" + result.substring(valueEnd);
                result = masked;
                lower = result.toLowerCase();
                idx = lower.indexOf(key, valueStart);
            }
        }
        return result;
    }
}
