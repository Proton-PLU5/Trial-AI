package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.controllers.flashback.FlashbackController;
import nz.ac.auckland.se206.utils.SceneManager;
import nz.ac.auckland.se206.utils.SceneManager.Scenes;

public class DefendantFlashbackController extends FlashbackController {

  @FXML
  private ImageView backgroundImage;

  @FXML
  private Label timerLabel;

  @FXML
  private Button nextBtn;
  @FXML
  private Button memoryButton;

  @FXML
  private Label conversationRoleLabel;
  @FXML
  private Label conversationTextLabel;

  @Override
  @FXML
  protected void initialize() {
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
  protected void initializeText() {
    //Create teh text for the flashback scene
    addText(
        "Upon seeing the individual in court with us today, I immediately"
            + " contacted the local authorities after reading their database profile.");
    addText("Since all our customers have a membership with us,"
        + " I can access information on them from our database.");
    addText("My job is to surveil the customers when they first walk into our store.");
  }

  @Override
  @FXML
  protected void handleMemoryButton() {
    SceneManager.switchScene(Scenes.defendantMemory);
  }
}
