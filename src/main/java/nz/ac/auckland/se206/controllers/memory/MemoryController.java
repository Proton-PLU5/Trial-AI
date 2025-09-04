package nz.ac.auckland.se206.controllers.memory;

import java.io.IOException;

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

  // Navigation
  @FXML private Button goBackButton;

  // Timer
  @FXML private Label timerLabel;

  // Chat visibility state
  private boolean isChatVisible = false;

  protected void initialize() {
    chatPane.setVisible(false);
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
