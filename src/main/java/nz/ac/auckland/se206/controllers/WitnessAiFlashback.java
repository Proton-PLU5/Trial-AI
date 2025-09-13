package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class WitnessAiFlashback {
  @FXML private Button memoryBtn;
  @FXML private Label timerLabel;
  private TimerService timerService;

  @FXML
  private void initialize() {

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
  }

  private void handleGameOver() throws IOException {
    Stage stage = (Stage) memoryBtn.getScene().getWindow();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/final.fxml"));
    Parent finalRoot = loader.load();
    stage.setScene(new Scene(finalRoot));
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

  @FXML
  private void handleMemoryButton() {
    Stage stage = (Stage) memoryBtn.getScene().getWindow();
    Parent roomRoot = SceneManager.getUiRoot(SceneManager.AppUi.aiMemory);
    stage.getScene().setRoot(roomRoot);
  }
}
