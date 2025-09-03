package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class WitnessHumanFlashback {
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

    if (secondsRemaining <= 10) {
      // Critical time - red
      timerLabel.setStyle("-fx-text-fill: red; -fx-font-size: 24px; -fx-font-weight: bold;");
    } else if (secondsRemaining <= 30) {
      // Warning time - orange
      timerLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 20px; -fx-font-weight: bold;");
    } else {
      // Normal time - green
      timerLabel.setStyle("-fx-text-fill: green; -fx-font-size: 18px; -fx-font-weight: bold;");
    }
  }

  @FXML
  private void handleMemoryButton() {
    Stage stage = (Stage) memoryBtn.getScene().getWindow();
    Parent roomRoot = SceneManager.getUiRoot(SceneManager.AppUi.humanMemory);
    stage.getScene().setRoot(roomRoot);
  }
}
