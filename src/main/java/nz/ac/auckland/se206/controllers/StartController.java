package nz.ac.auckland.se206.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.utils.TimableScene;

public class StartController implements TimableScene {

  @FXML
  private Label timerLabel;
  @FXML
  private Label titleLabel;
  @FXML
  private Button playBtn;

  // could add like volume dragger or sound button etc

  @FXML
  private void initialize() {
  }

  @FXML
  private void pressPlayButton() {
    // This method handles the play button click and switches to the room scene
    try {
      // Timer Setup
      App.createTimer();
      App.timer.setCountDown(true);
      App.timer.buildTimer();

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
