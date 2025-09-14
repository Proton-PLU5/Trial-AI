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
import javafx.stage.Stage;

public class HumanMemory {

  @FXML private Button roomBtn;
  @FXML private Button chatBtn;
  @FXML private Label timerLabel;
  @FXML private Pane chatPanel;

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
    Stage stage = (Stage) chatBtn.getScene().getWindow();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/verdict.fxml"));
    Parent finalRoot = loader.load();
    stage.setScene(new Scene(finalRoot));
  }

  @FXML
  private void handleBackButton() {
    Stage stage = (Stage) roomBtn.getScene().getWindow();
    Parent roomRoot = SceneManager.getUiRoot(SceneManager.AppUi.room);
    stage.getScene().setRoot(roomRoot);
  }

  @FXML
  private void handleOpenChatButtonClick(MouseEvent event) throws IOException {
    if (chatPanel.getChildren().isEmpty()) {
      Parent chatContent = SceneManager.getChatView("witnessHuman");
      ChatController chatController = SceneManager.getChatController("witnessHuman");

      chatController.setChatPanelContainer(chatPanel);

      chatPanel.getChildren().add(chatContent);
    }

    chatPanel.setVisible(true);
  }
}
