package mathapp.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainGUIApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlLocation = getClass().getResource("main_tabbed_view.fxml");
        Parent root = FXMLLoader.load(fxmlLocation);
        
        // Simulating a modern mobile/tablet responsive aspect ratio (450x700)
        Scene scene = new Scene(root, 450, 700);
        
//         Apply the CSS dynamically to ensure styling attaches correctly
        String cssPath = getClass().getResource("style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        primaryStage.setTitle("Math Solver Pro");
        primaryStage.setScene(scene);
        
        // We set minimum sizes so the auto-adjustment doesn't break if shrunk too small
        primaryStage.setMinWidth(450);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}