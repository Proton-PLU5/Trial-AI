package nz.ac.auckland.se206.controllers.flashback;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public abstract class FlashbackController {
  @FXML private ImageView backgroundImage;

  @FXML private Label titleLabel;
  @FXML private Label descriptionLabel;

  /// Conversation Elements
  // Conversation Pane (The parent object of all the conversation elements)
  @FXML private AnchorPane conversationPane;
  @FXML private Label conversationRoleLabel;
  @FXML private Label conversationTextLabel;
  @FXML private Button conversationNextButton;

  // Navigation
  @FXML private Button nextButton;
  @FXML private Button goBackButton;

  // Timer
  @FXML private Label timerLabel;

  /**
   * Method executed during the initialization of the flashback scene. Update UI elements with the
   * current flashback data here.
   */
  protected void initialize() {}
}
