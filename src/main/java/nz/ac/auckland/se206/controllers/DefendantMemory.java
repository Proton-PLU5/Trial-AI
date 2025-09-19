package nz.ac.auckland.se206.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import nz.ac.auckland.se206.controllers.memory.MemoryController;
import nz.ac.auckland.se206.utils.SceneManager;
import nz.ac.auckland.se206.utils.TimableScene;

public class DefendantMemory extends MemoryController implements TimableScene{

  @FXML private Rectangle rec1;
  @FXML private Rectangle rec2;
  @FXML private Rectangle rec3;
  @FXML private Rectangle rec4;
  @FXML private Label pinLabel;
  
  // Login Sequence
  @FXML private AnchorPane initialPane;
  @FXML private AnchorPane keypadPane;
  @FXML private AnchorPane loginPane;
  @FXML private AnchorPane cctvPane;


  private String pin = "_ _ _ _";
  private final String correctPin = "1 2 3 4";
  

  public DefendantMemory() {
    super("prompts/defendant.txt");
  }

  @Override
  @FXML
  protected void initialize() {
    loginPane.setVisible(true);
    initialPane.setVisible(true);
    keypadPane.setVisible(false);
    cctvPane.setVisible(false);
    super.initialize();
  }

  @FXML
  private void keypadButtonPressed(ActionEvent event) {
    Button button = (Button) event.getSource();
    String buttonText = button.getText();

    // Update pin if there are still underscores left
    if (buttonText.matches("[0-9]")) {
      pin = pin.replaceFirst("_", buttonText);
      pinLabel.setText(pin);
    }
  }

  /**
   * Handles the "Submit PIN" button press event to check the entered pin.
   * @param event The action event triggered by clicking the submit pin button
   */
  @FXML
  private void onSubmitPinPressed(ActionEvent event) {
    if (pin.equals(correctPin)) {
      // Correct pin entered, proceed to next pane
      loginPane.setVisible(false);
      cctvPane.setVisible(true);
    } else {
      // Incorrect pin, reset
      pin = "_ _ _ _";
      pinLabel.setText(pin);
    }
  }

  /**
   * Handles the "Clear" button press event to reset the pin.
   * @param event The action event triggered by clicking the clear button
   */
  @FXML
  private void onClearButtonPressed(ActionEvent event) {
    pin = "_ _ _ _";
    pinLabel.setText(pin);
  }

  @FXML
  private void loginButtonPressed(ActionEvent event) {
    initialPane.setVisible(false);
    keypadPane.setVisible(true);
  }

  @Override
  public Label getTimerLabel() {
    return super.getTimerLabel();
  }
}
