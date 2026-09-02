package alex.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Runs the JavaFX interface for Alex.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Label greeting = new Label("Hello! I'm Alex.");
        Scene scene = new Scene(greeting, 400, 600);

        stage.setTitle("Alex");
        stage.setScene(scene);
        stage.show();
    }
}
