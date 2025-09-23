package nz.ac.auckland.se206.controllers;

import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.se206.utils.SceneManager;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.memory.MemoryController;

public class HumanMemory extends MemoryController {

  @FXML private Button roomBtn;
  @FXML private Button chatBtn;
  @FXML private Label timerLabel;
  @FXML private Pane chatPanel;

  // Title
  @FXML private AnchorPane titleBlock;
  @FXML private Label titleLabel;
  @FXML private Label descriptionLabel;

  // Shopping List
  @FXML private Label shoppingListItem1Label;
  @FXML private Label shoppingListItem2Label;
  @FXML private Label shoppingListItem3Label;
  @FXML private Label shoppingListItem4Label;

  public HumanMemory() {
    super("prompts/witnessHuman.txt");
  }

  @Override
  @FXML
  protected void initialize() {
    App.timer.addConsumer(getTimerConsumer());
    createTitleDisappearAnimation();
    super.initialize();
  }

  private void handleGameOver() throws IOException {
    Stage stage = (Stage) chatBtn.getScene().getWindow();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/verdict.fxml"));
    Parent finalRoot = loader.load();
    stage.setScene(new Scene(finalRoot));
  }

  @FXML
  private void handleBackButton() {
    SceneManager.switchScene(SceneManager.Scenes.room);
  }

  @FXML
  private void handleOpenChatButtonClick(MouseEvent event) throws IOException {
    chatPanel.setVisible(true);
  }
}
