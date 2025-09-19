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

public class WitnessHumanFlashback extends FlashbackController {
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
    imagePaths = new ArrayList<>();
    imagePaths.add("/images/defendantFlash3.png");
    imagePaths.add("/images/humanFlash2.png");
    imagePaths.add("/images/humanFlash1.png");
    addImagesToStack(imagePaths);
  }

  @Override
  protected void setupText() {
    conversationText = new ArrayList<>();
    conversationText.add("Paragraph 3: dahdasidgaiudhauih");
    conversationText.add("Paragraph 2: hdiuwhdiuadihadi");
    conversationText.add("Paragraph 1: dhauhdiuhdiudh");
    addTextToStack("Human Witness", conversationText);
  }

  @FXML
  protected void handleMemoryButton() {
    Stage stage = (Stage) memoryButton.getScene().getWindow();
    Parent roomRoot = SceneManager.getUiRoot(SceneManager.AppUi.defendantMemory);
    stage.getScene().setRoot(roomRoot);
  }
}
