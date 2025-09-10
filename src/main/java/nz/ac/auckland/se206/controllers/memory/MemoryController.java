package nz.ac.auckland.se206.controllers.memory;

import java.io.IOException;

import javafx.animation.PathTransition;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.se206.App;

public abstract class MemoryController {
  @FXML private Label titleLabel;
  @FXML private Label descriptionLabel;

  // Chat Elements
  @FXML private AnchorPane chatPane;
  @FXML private Button sendButton;
  @FXML private Button chatButton;
  @FXML private TextArea textArea;
  @FXML private TextField textField;

  // Title Pane
  @FXML private AnchorPane titleBlock;

  // Navigation
  @FXML private Button goBackButton;

  // Timer
  @FXML private Label timerLabel;

  // Chat visibility state
  private boolean isChatVisible = false;

  protected void initialize() {
    chatPane.setVisible(false);

    // Animate title block to disappear after 3 seconds
    titleBlock.setVisible(true);
    Transition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
    delay.setOnFinished(event -> hideTitleBlock());
    delay.play();
  }

  private void hideTitleBlock() {
    PathTransition transition = new PathTransition();
    transition.setNode(titleBlock);
    transition.setDuration(javafx.util.Duration.seconds(1));
    transition.setPath(new javafx.scene.shape.Line(0, -100, 0, 0));
    transition.setCycleCount(1);
    transition.setOnFinished(event -> titleBlock.setVisible(false));
  }

  /**
   * Handles the "Chat" button press event to toggle chat visibility.
   */
  protected void onChatButtonPressed() {
    isChatVisible = !isChatVisible;
    chatPane.setVisible(isChatVisible);
  }

  /**
   * Handles the "Go Back" button press event.
   */
  @FXML
  protected void onGoBackButtonPressed() {
    try {
      Parent root = App.loadFxml("room.fxml");
      App.primaryStage.setScene(new Scene(root));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
