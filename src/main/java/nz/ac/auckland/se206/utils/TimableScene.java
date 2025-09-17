package nz.ac.auckland.se206.utils;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public interface TimableScene {
  
  Label getTimerLabel();

  default void updateTimerLabel(String timeString) {
    Label timerLabel = getTimerLabel();
    if (timerLabel != null) {
      timerLabel.setText(timeString);
    }
  }
}
