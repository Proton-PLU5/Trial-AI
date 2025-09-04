package nz.ac.auckland.se206.controllers.memory;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

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
}
