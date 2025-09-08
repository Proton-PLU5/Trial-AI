package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class DefendantFlashback {

  @FXML private ImageView drawingImageView;
  @FXML private Button backBtn;
  @FXML private Button nextBtn;
  @FXML private Button prevBtn;
  @FXML private Button chatBtn;
  @FXML private Label timerLabel;
  @FXML private Pane chatPanel;
  @FXML private Button memoryBtn;

  private List<String> imagePaths;
  private int currentDrawingIndex = 0;
  private TimerService timerService;

  @FXML
  private void initialize() {
    chatPanel.setVisible(false);

    timerService = TimerService.getInstance();

    // Bind timer display to label
    if (timerLabel != null) {
      timerLabel.textProperty().bind(timerService.timeDisplayProperty());

      // Update timer style based on remaining time
      timerService
          .secondsRemainingProperty()
          .addListener(
              (obs, oldVal, newVal) -> {
                updateTimerStyle(newVal.intValue());
              });

      // Handle game over when time runs out
      timerService
          .timeUpProperty()
          .addListener(
              (obs, wasTimeUp, isTimeUp) -> {
                if (isTimeUp) {
                  try {
                    handleGameOver();
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                }
              });
    }

    setupImagePaths();
    showImageDrawing();
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

  private void handleGameOver() throws IOException {
    Stage stage = (Stage) backBtn.getScene().getWindow();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/final.fxml"));
    Parent finalRoot = loader.load();
    stage.setScene(new Scene(finalRoot));
  }

  private void setupImagePaths() {
    imagePaths = new ArrayList<>();
    imagePaths.add("/images/defendantFlash1.png");
    imagePaths.add("/images/defendantFlash2.png");
    imagePaths.add("/images/defendantFlash3.png");
  }

  @FXML
  private void nextDrawing() {
    currentDrawingIndex = (currentDrawingIndex + 1) % imagePaths.size();
    showImageDrawing();
  }

  @FXML
  private void previousDrawing() {
    currentDrawingIndex = (currentDrawingIndex - 1 + imagePaths.size()) % imagePaths.size();
    showImageDrawing();
  }

  private void showImageDrawing() {
    try {
      String path = imagePaths.get(currentDrawingIndex);
      InputStream stream = getClass().getResourceAsStream(path);
      if (stream == null) {
        throw new IllegalArgumentException("Image not found: " + path);
      }
      Image image = new Image(stream);
      drawingImageView.setImage(image);
    } catch (Exception e) {
      System.err.println("Failed to load image: " + e.getMessage());
    }
  }

  @FXML
  private void handleMemoryButton() {
    Stage stage = (Stage) nextBtn.getScene().getWindow();
    Parent roomRoot = SceneManager.getUiRoot(SceneManager.AppUi.defendantMemory);
    stage.getScene().setRoot(roomRoot);
  }
}
