package mathapp.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Lightweight centralized logger for the MathApp JavaFX frontend.
 * Writes to both the console and a log file (mathapp.log).
 *
 * Provides convenience methods for screen events (open/close), business
 * operations, API calls, and standard log levels with timestamps.
 */
public final class MathLogger {

    private static final DateTimeFormatter TS =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static final String LOG_FILE = "logs/mathapp.log";

    private static final String[] SENSITIVE_KEYS = {
            "password", "token", "secret", "authorization"
    };

    private MathLogger() {
    }

    // ==========================================================
    //  Standard levels
    // ==========================================================

    public static void info(String category, String message) {
        write("INFO", category, message);
    }

    public static void debug(String category, String message) {
        write("DEBUG", category, message);
    }

    public static void warn(String category, String message) {
        write("WARN", category, message);
    }

    public static void error(String category, String message) {
        write("ERROR", category, message);
    }

    public static void error(String category, String message, Throwable ex) {
        write("ERROR", category, message + " | " + (ex != null ? ex.getMessage() : ""));
        if (ex != null) {
            writeThrowable(category, ex);
        }
    }

    // ==========================================================
    //  Screen events (open / close)
    // ==========================================================

    public static void screenOpened(String screenName) {
        info("SCREEN", "OPENED | " + screenName);
    }

    public static void screenClosed(String screenName) {
        info("SCREEN", "CLOSED | " + screenName);
    }

    // ==========================================================
    //  Business operations
    // ==========================================================

    public static void operationStarted(String operation, String detail) {
        info("OP", "START  | " + operation + " | " + safe(detail));
    }

    public static void operationSuccess(String operation, String detail) {
        info("OP", "SUCCESS| " + operation + " | " + safe(detail));
    }

    public static void operationFailed(String operation, String detail) {
        error("OP", "FAILED | " + operation + " | " + safe(detail));
    }

    public static void operationFailed(String operation, String detail, Throwable ex) {
        error("OP", "FAILED | " + operation + " | " + safe(detail), ex);
    }

    public static void operationStep(String operation, String step, String detail) {
        debug("OP", "STEP   | " + operation + " | " + step + " | " + safe(detail));
    }

    public static void operationWarning(String operation, String warning) {
        warn("OP", "WARN   | " + operation + " | " + safe(warning));
    }

    // ==========================================================
    //  API calls
    // ==========================================================

    public static void apiCall(String method, String uri) {
        info("API", method + " " + uri);
    }

    public static void apiSuccess(String method, String uri, int status) {
        info("API", method + " " + uri + " -> " + status);
    }

    public static void apiError(String method, String uri, int status, String body) {
        error("API", method + " " + uri + " -> " + status + " | " + safe(body));
    }

    // ==========================================================
    //  Internal
    // ==========================================================

    private static void write(String level, String category, String message) {
        String line = TS.format(LocalDateTime.now())
                + " [" + level + "] [" + category + "] " + message;
        System.out.println(line);
        writeToFile(line);
    }

    private static void writeThrowable(String category, Throwable ex) {
        String line = TS.format(LocalDateTime.now())
                + " [ERROR] [" + category + "] ";
        try (PrintWriter console = new PrintWriter(System.out)) {
            console.println(line);
            concatStack(console, ex);
            console.flush();
        }
        appendThrowableToFile(line, ex);
    }

    private static void concatStack(PrintWriter out, Throwable ex) {
        out.println(ex.toString());
        for (StackTraceElement el : ex.getStackTrace()) {
            out.println("    at " + el);
        }
    }

    private static void appendThrowableToFile(String header, Throwable ex) {
        try {
            Files.createDirectories(Paths.get("logs"));
            try (FileWriter fw = new FileWriter(LOG_FILE, true);
                 PrintWriter pw = new PrintWriter(fw)) {
                pw.println(header);
                concatStack(pw, ex);
            }
        } catch (IOException ignored) {
            // Logging failure should not break the app
        }
    }

    private static void writeToFile(String line) {
        try {
            Files.createDirectories(Paths.get("logs"));
            try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
                fw.write(line + System.lineSeparator());
            }
        } catch (IOException ignored) {
            // Logging failure should not break the app
        }
    }

    /**
     * Redact sensitive values from a detail string before logging.
     */
    private static String safe(String detail) {
        if (detail == null || detail.isBlank()) {
            return detail;
        }
        String result = detail;
        String lower = detail.toLowerCase();
        for (String key : SENSITIVE_KEYS) {
            int idx = lower.indexOf(key);
            while (idx != -1) {
                int valueStart = idx + key.length();
                while (valueStart < result.length()
                        && (result.charAt(valueStart) == ' ' || result.charAt(valueStart) == '=')) {
                    valueStart++;
                }
                int valueEnd = valueStart;
                while (valueEnd < result.length()
                        && result.charAt(valueEnd) != ' ' && result.charAt(valueEnd) != ','
                        && result.charAt(valueEnd) != '\n') {
                    valueEnd++;
                }
                result = result.substring(0, valueStart) + "*****" + result.substring(valueEnd);
                lower = result.toLowerCase();
                idx = lower.indexOf(key, valueStart);
            }
        }
        return result;
    }
}
