package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import nz.ac.auckland.se206.utils.SceneManager;
import nz.ac.auckland.se206.controllers.flashback.FlashbackController;

public class HumanFlashback extends FlashbackController {
  @FXML private ImageView backgroundImage;
  @FXML private Button nextBtn;
  @FXML private Label timerLabel;
  @FXML private Button memoryButton;

  @FXML private Label conversationRoleLabel;
  @FXML private Label conversationTextLabel;

  private List<String> imagePaths;
  private List<String> conversationText;

  private int currentDrawingIndex = 0;

  @FXML
  protected void initialize() {
    super.initialize();
  }

  @Override
  protected void setupImages() {
    addImage("/images/humanFlash3.png");
    addImage("/images/humanFlash2.png");
    addImage("/images/humanFlash1.png");
  }

  @Override
  protected void setupText() {
    addText("But when I was about to leave the store, the police suddenly showed up to arrest me, and I had no idea why.");
    addText("After I finished getting everything I needed, I went to purchase it all at the self-checkout.");
    addText("I was just shopping at the supermarket normally, with nothing out of the usual.");
  }

  @FXML
  protected void handleMemoryButton() {
    SceneManager.switchScene(SceneManager.Scenes.humanMemory);
  }
}
