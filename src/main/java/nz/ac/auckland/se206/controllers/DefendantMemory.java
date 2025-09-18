package nz.ac.auckland.se206.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import nz.ac.auckland.se206.controllers.memory.MemoryController;
import nz.ac.auckland.se206.utils.SceneManager;
import nz.ac.auckland.se206.utils.TimableScene;

public class DefendantMemory extends MemoryController implements TimableScene{

  @FXML private Rectangle rec1;
  @FXML private Rectangle rec2;
  @FXML private Rectangle rec3;
  @FXML private Rectangle rec4;

  public DefendantMemory() {
    super("/prompts/defendant.txt");
  }

  @Override
  @FXML
  protected void initialize() {
    super.initialize();
  }

  @Override
  public Label getTimerLabel() {
    return super.getTimerLabel();
  }
}
