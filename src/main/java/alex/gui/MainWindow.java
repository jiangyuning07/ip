package alex.gui;

import alex.Alex;
import alex.CommandResult;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controls the main application window.
 */
public class MainWindow {
    private static final String WELCOME_MESSAGE =
            "Hey. Welcome to Alex's.\nWhat can I get started for you?";

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

        dialogContainer.getChildren().add(DialogBox.getAlexDialog(WELCOME_MESSAGE, alexImage));
        Platform.runLater(userInput::requestFocus);
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

        CommandResult result = alex.getResponse(input);
        DialogBox responseDialog = result.isError()
                ? DialogBox.getErrorDialog(result.text(), alexImage)
                : DialogBox.getAlexDialog(result.text(), alexImage);

        if (input.isEmpty()) {
            dialogContainer.getChildren().add(responseDialog);
        } else {
            dialogContainer.getChildren().addAll(
                    DialogBox.getUserDialog(input),
                    responseDialog);
        }

        userInput.clear();
        if (result.shouldExit()) {
            Platform.exit();
        } else {
            userInput.requestFocus();
        }
    }
}
