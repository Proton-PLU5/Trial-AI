package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.controllers.flashback.FlashbackController;
import nz.ac.auckland.se206.utils.SceneManager;

public class AiFlashback extends FlashbackController {
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
  protected void setupText() {
    addText("Paragraph 3: dahdasidgaiudhauih");
    addText("Paragraph 2: hdiuwhdiuadihadi");
    addText("Paragraph 1: dhauhdiuhdiudh");
  }

  @FXML
  protected void handleMemoryButton() {
    SceneManager.switchScene(SceneManager.Scenes.aiMemory);
  }
}
