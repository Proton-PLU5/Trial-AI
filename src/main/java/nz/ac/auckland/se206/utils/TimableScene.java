package nz.ac.auckland.se206.utils;

import java.util.function.Consumer;

import javafx.scene.control.Label;

public interface TimableScene {
  
  Label getTimerLabel();

  default Consumer<String> getTimerConsumer() {
    return this::updateTimerLabel;
  }

  default void updateTimerLabel(String timeString) {
    Label timerLabel = getTimerLabel();
    if (timerLabel != null) {
      timerLabel.setText(timeString);
    }
  }
}
