package mathapp.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MathLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger("mathapp.math");

    private MathLogger() {
    }

    public static void operationStarted(String operation, String detail) {
        LOGGER.debug("{} started", operation);
    }

    public static void operationSuccess(String operation, String detail) {
        LOGGER.debug("{} completed", operation);
    }

    public static void operationStep(String operation, String step, String detail) {
        LOGGER.trace("{}: {}", operation, step);
    }

    public static void operationWarning(String operation, String warning) {
        LOGGER.warn("{} completed with a warning", operation);
    }

    public static void operationFailed(String operation, String detail) {
        LOGGER.warn("{} failed", operation);
    }

    public static void operationFailed(String operation, String detail, Throwable error) {
        LOGGER.warn("{} failed ({})", operation, error.getClass().getSimpleName());
    }
}
