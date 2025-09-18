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

public class WitnessAiFlashback extends FlashbackController {
  @FXML private ImageView backgroundImage;
  @FXML private Button nextBtn;
  @FXML private Label timerLabel;
  @FXML private Button memoryButton;

  private List<String> imagePaths;
  private int currentDrawingIndex = 0;

  @FXML
  protected void initialize() {
    super.initialize();
  }

  @Override
  protected void setupImages() {
    imagePaths = new ArrayList<>();
    imagePaths.add("/images/aiFlash1.png");
    imagePaths.add("/images/aiFlash2.png");
    imagePaths.add("/images/aiFlash3.png");
    addImagesToStack(imagePaths);
  }

  @FXML
  protected void handleMemoryButton() {
    Stage stage = (Stage) nextBtn.getScene().getWindow();
    Parent roomRoot = SceneManager.getUiRoot(SceneManager.AppUi.defendantMemory);
    stage.getScene().setRoot(roomRoot);
  }
}
