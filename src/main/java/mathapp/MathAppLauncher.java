package mathapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Main FX Application Launcher for the Mathematics & Scientific Computing Workbench.
 */
public class MathAppLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlLocation = getClass().getResource("/mathapp/gui/main_view.fxml");
        if (fxmlLocation == null) {
            throw new IllegalStateException("Main view resource not found: /mathapp/gui/main_view.fxml");
        }

        Parent root = FXMLLoader.load(fxmlLocation);
        Scene scene = new Scene(root, 1100, 720);

        URL cssLocation = getClass().getResource("/mathapp/gui/style.css");
        if (cssLocation != null) {
            scene.getStylesheets().add(cssLocation.toExternalForm());
        }

        primaryStage.setTitle("NexusMath-Ai");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}