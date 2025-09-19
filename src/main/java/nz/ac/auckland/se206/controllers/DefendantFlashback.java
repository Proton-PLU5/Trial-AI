package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import nz.ac.auckland.se206.controllers.flashback.FlashbackController;
import nz.ac.auckland.se206.utils.SceneManager;
import nz.ac.auckland.se206.utils.SceneManager.Scenes;

public class DefendantFlashback extends FlashbackController {

  @FXML private ImageView backgroundImage;

  @FXML private Label timerLabel;

  @FXML private Button nextBtn;
  @FXML private Button memoryButton;

  @FXML private Label conversationRoleLabel;
  @FXML private Label conversationTextLabel;

  private List<String> imagePaths;
  private List<String> conversationText;

  private int currentDrawingIndex = 0;
  
  @FXML
  protected void initialize() {
    conversationRoleLabel.setText("Defendant");
    conversationTextLabel.setVisible(true);
    super.initialize();
  }

  @Override
  protected void setupImages() {
    addImage("/images/defendantFlash3.png");
    addImage("/images/defendantFlash2.png");
    addImage("/images/defendantFlash1.png");
  }

  @Override
  protected void setupText() {
    addText("Paragraph 3: dahdasidgaiudhauih");
    addText("Paragraph 2: hdiuwhdiuadihadi");
    addText("Paragraph 1: dhauhdiuhdiudh");
  }

  @FXML
  protected void handleMemoryButton() {
    SceneManager.switchScene(Scenes.defendantMemory);
  }

  
}
