package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import nz.ac.auckland.se206.utils.TimableScene;

public class startController implements TimableScene {
  @FXML Label timerLabel;
  @FXML Label title_label;
  @FXML private Button playBtn;

  // could add like volume dragger or sound button etc

  @FXML
  private void initialize() {}

  private void handleGameOver() {
    try {
      Stage stage = (Stage) playBtn.getScene().getWindow();
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/final.fxml"));
      Parent finalRoot = loader.load();
      stage.setScene(new Scene(finalRoot));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handlePlayButton() {
    try {
      Stage stage = (Stage) playBtn.getScene().getWindow();
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/room.fxml"));
      Parent finalRoot = loader.load();
      stage.setScene(new Scene(finalRoot));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Label getTimerLabel() {
    return timerLabel;
  }
}
