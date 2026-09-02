package alex.gui;

import alex.Alex;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controls the main application window.
 */
public class MainWindow {
    private final Image userImage = new Image(
            getClass().getResourceAsStream("/images/DaUser.png"));

    private final Image alexImage = new Image(
            getClass().getResourceAsStream("/images/DaAlex.png"));

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private Alex alex;

    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((
                observable, oldHeight, newHeight) -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the Alex instance used to process commands.
     *
     * @param alex Alex instance.
     */
    public void setAlex(Alex alex) {
        this.alex = alex;
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();

        if (input.isEmpty()) {
            return;
        }

        String response = alex.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getAlexDialog(response, alexImage));

        userInput.clear();
    }
}
