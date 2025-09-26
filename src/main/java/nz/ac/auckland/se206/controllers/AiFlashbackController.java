package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.controllers.flashback.FlashbackController;
import nz.ac.auckland.se206.utils.SceneManager;

public class AiFlashbackController extends FlashbackController {
  @FXML
  private ImageView backgroundImage;
  @FXML
  private Button nextBtn;
  @FXML
  private Label timerLabel;
  @FXML
  private Button memoryButton;

  @FXML
  private Label conversationRoleLabel;
  @FXML
  private Label conversationTextLabel;

  @Override
  @FXML
  protected void initialize() {
    super.initialize();
  }

  @Override
  protected void setupImages() {
    addImage("/images/aiFlash3.png");
    addImage("/images/aiFlash2.png");
    addImage("/images/aiFlash1.png");
  }

  @Override
  protected void initializeText() {
    addText(
        "System output: subject detected exhibiting irregular behavior."
            + "Subject advanced toward exit carrying one unscanned item.");
    addText("This operational pattern persisted throughout"
        + "the cycle until anomaly data was registered.");
    addText(
        "System output: initial operations proceeded under standard parameters."
            + "All customers scanned items and exited with zero unscanned detections.");
  }

  @Override
  @FXML
  protected void handleMemoryButton() {
    SceneManager.switchScene(SceneManager.Scenes.aiMemory);
  }
}
