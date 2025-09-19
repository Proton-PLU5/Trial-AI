package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.utils.SceneManager;
import nz.ac.auckland.se206.utils.TimableScene;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController implements TimableScene {

  private static boolean isFirstTimeInit = true;

  @FXML private Rectangle witnessAi;
  @FXML private Rectangle witnessHuman;
  @FXML private Rectangle defendant;
  @FXML private Button btnGuess;
  @FXML private Label timerLabel;
  @FXML private Button nextButton;
  @FXML private Label conversationRoleLabel;
  @FXML private Label chaconversationTextLabel;
  @FXML private AnchorPane conversationPanel;
  private String currentChatCharacter = null;
  private Map<String, Boolean> characterInteracted = new HashMap<>();
  private TimerService timerService;
  private boolean finalSceneLoaded = false;

  /**
   * Initializes the room view. If it's the first time initialization, it will provide instructions
   * via text-to-speech.
   */
  @FXML
  public void initialize() {
    App.timer.addConsumer(this.getTimerConsumer());

    if (isFirstTimeInit) {
      // Media media = new Media(getClass().getResource("/sounds/startAudio.mp3").toExternalForm());
      // MediaPlayer mediaPlayer = new MediaPlayer(media);
      // mediaPlayer.play();
    }
  }

  private void handleGameOver() throws IOException {
    if (!finalSceneLoaded) {
      finalSceneLoaded = true;
      Stage stage = (Stage) btnGuess.getScene().getWindow();
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/verdict.fxml"));
      Parent finalRoot = loader.load();
      timerService.startTimer();
      stage.setScene(new Scene(finalRoot));
    }
  }

  private void updateTimerStyle(int secondsRemaining) {
    if (timerLabel == null) {
      return;
    }

    timerLabel.getStyleClass().removeAll("timer-normal", "timer-warning", "timer-critical");

    if (secondsRemaining <= 10) {

      timerLabel.getStyleClass().addAll("timer-label", "timer-critical");
    } else if (secondsRemaining <= 30) {

      timerLabel.getStyleClass().addAll("timer-label", "timer-warning");
    } else {

      timerLabel.getStyleClass().addAll("timer-label", "timer-normal");
    }
  }

  /**
   * Handles the key pressed event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " pressed");
  }

  /**
   * Handles the key released event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyReleased(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " released");
  }

  /**
   * Handles mouse clicks on rectangles representing people in the room.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleRectangleClick(MouseEvent event) throws IOException {
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    String characterId = clickedRectangle.getId();

    boolean hasInteracted = characterInteracted.getOrDefault(characterId, false);

    if (!hasInteracted) {
      characterInteracted.put(characterId, true);
      SceneManager.switchScene(getFlashbackScene(characterId));
    } else {
      SceneManager.switchScene(getMemoryScene(characterId));
    }
  }
  
  @FXML
  void handleNextButton() {
    nextButton.setVisible(false);
    conversationPanel.setVisible(false);
    conversationRoleLabel.setVisible(false);
    chaconversationTextLabel.setVisible(false);
  }


  private SceneManager.Scenes getMemoryScene(String characterId) {
    switch (characterId) {
      case "witnessAi":
        return SceneManager.Scenes.aiMemory;
      case "witnessHuman":
        return SceneManager.Scenes.humanMemory;
      default:
        return SceneManager.Scenes.defendantMemory;
    }
  }

  private SceneManager.Scenes getFlashbackScene(String characterId) {
    switch (characterId) {
      case "witnessAi":
        return SceneManager.Scenes.witnessAi;
      case "witnessHuman":
        return SceneManager.Scenes.witnessHuman;
      default:
        return SceneManager.Scenes.defendant;
    }
  }

  /**
   * Handles the guess button click event.
   *
   * @param event the action event triggered by clicking the guess button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleGuessClick(MouseEvent event) throws IOException {
    handleGameOver();
  }

  @Override
  public Label getTimerLabel() {
    return timerLabel;
  }
}
