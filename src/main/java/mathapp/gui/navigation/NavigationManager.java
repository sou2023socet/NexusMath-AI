package mathapp.gui.navigation;

import javafx.animation.FadeTransition;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import mathapp.logging.MathLogger;

public class NavigationManager {

    private VBox currentScreen;

    public NavigationManager(VBox initialScreen) {
        this.currentScreen = initialScreen;
        if (currentScreen != null) {
            currentScreen.setVisible(true);
        }
    }

    public void switchScreen(VBox newScreen) {
        if (newScreen == null || currentScreen == newScreen) return;

        String fromName = getScreenId(currentScreen);
        String toName = getScreenId(newScreen);

        MathLogger.operationStarted("NAVIGATE", fromName + " -> " + toName);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), currentScreen);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            MathLogger.screenClosed(fromName);
            currentScreen.setVisible(false);

            newScreen.setOpacity(0.0);
            newScreen.setVisible(true);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), newScreen);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            currentScreen = newScreen;
            MathLogger.screenOpened(toName);
        });
        fadeOut.play();
    }

    private String getScreenId(VBox screen) {
        return (screen != null && screen.getId() != null) ? screen.getId() : "UNKNOWN";
    }

    public VBox getCurrentScreen() {
        return currentScreen;
    }
}