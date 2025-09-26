package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.utils.SceneManager;
import nz.ac.auckland.se206.utils.TimableScene;

/**
 * Controller class for the room view. Handles user interactions within the room
 * where the user can
 * chat with customers and guess their profession.
 */
public class RoomController implements TimableScene {

  public static boolean isFirstTimeInit = true;

  @FXML
  private Rectangle witnessAi;
  @FXML
  private Rectangle witnessHuman;
  @FXML
  private Rectangle defendant;
  @FXML
  private Button btnGuess;
  @FXML
  private Label timerLabel;
  @FXML
  private Button nextButton;
  @FXML
  private Label conversationRoleLabel;
  @FXML
  private Label chaconversationTextLabel;
  @FXML
  private AnchorPane conversationPane;

  public static Map<String, Boolean> characterInteracted = new HashMap<>();

  private boolean finalSceneLoaded = false;

  private MediaPlayer startAudioMediaPlayer;

  /**
   * Initializes the room view. If it's the first time initialization, it will
   * provide instructions
   * via text-to-speech.
   */
  @FXML
  public void initialize() {
    initializeRectangleAnimations();

    App.timer.addConsumer(this.getTimerConsumer());

    // Check if player has chatted with all three participants
    // enableGuessButton();

    if (isFirstTimeInit) {
      startAudioMediaPlayer = new MediaPlayer(
          new Media(getClass().getResource("/sounds/voiceover.mp3").toExternalForm()));
      startAudioMediaPlayer.play();

      isFirstTimeInit = false;
    } else {
      conversationPane.setVisible(false);
    }
  }

  private void handleGameOver() throws IOException {
    if (!finalSceneLoaded) {
      // Stop the audio if it's still playing
      if (startAudioMediaPlayer != null) {
        startAudioMediaPlayer.stop();
      }
      finalSceneLoaded = true;
      SceneManager.switchScene(SceneManager.Scenes.verdict);
      SceneManager.setStyleSheet("/css/verdict.css");
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

    // Stop the audio if it's still playing
    if (startAudioMediaPlayer != null) {
      startAudioMediaPlayer.stop();
    }

    if (!hasInteracted) {
      characterInteracted.put(characterId, true);
      SceneManager.switchScene(getFlashbackScene(characterId));
      SceneManager.setStyleSheet("/css/style.css");
    } else {
      SceneManager.switchScene(getMemoryScene(characterId));
    }
  }

  @FXML
  void handleNextButton() {
    conversationPane.setVisible(false);
  }

  private SceneManager.Scenes getMemoryScene(String characterId) {
    // This method gets the memory scene for each of the characters
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
    // This method gets the flashback scene for each of the characters
    switch (characterId) {
      case "witnessAi":
        return SceneManager.Scenes.aiFlashback;
      case "witnessHuman":
        return SceneManager.Scenes.humanFlashback;
      default:
        return SceneManager.Scenes.defendantFlashback;
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

  // Checks if all participants have been chatted with
  public void enableGuessButton() {
    if (AiMemoryController.hasChattedWithAi && HumanMemoryController.hasChattedWithHuman
        && DefendantMemoryController.hasChattedWithDefendant) {
      btnGuess.setDisable(false);
    } else {
      btnGuess.setDisable(true);
    }
  }

  private void initializeRectangleAnimations() {
    App.createScaleAnimation(witnessAi, 2.0, 1.1);
    App.createScaleAnimation(witnessHuman, 2.0, 1.1);
    App.createScaleAnimation(defendant, 2.0, 1.1);
  }
}
