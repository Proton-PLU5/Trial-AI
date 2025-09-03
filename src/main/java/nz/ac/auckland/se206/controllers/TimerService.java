package nz.ac.auckland.se206.controllers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TimerService {
  private static TimerService instance;

  public static synchronized TimerService getInstance() {
    if (instance == null) {
      instance = new TimerService();
    }
    return instance;
  }

  private ScheduledExecutorService executor;
  private IntegerProperty secondsRemaining = new SimpleIntegerProperty(999);
  private BooleanProperty running = new SimpleBooleanProperty(false);
  private BooleanProperty timeUp = new SimpleBooleanProperty(false);
  private StringProperty timeDisplay = new SimpleStringProperty("02:00");

  private TimerService() {
    executor =
        Executors.newSingleThreadScheduledExecutor(
            r -> {
              Thread t = new Thread(r);
              t.setDaemon(true);
              return t;
            });

    // Update display when seconds change
    secondsRemaining.addListener(
        (obs, oldVal, newVal) -> {
          updateTimeDisplay(newVal.intValue());
        });

    // Auto-start the timer when instance is created
    startTimer();
  }

  public void startTimer() {
    if (running.get()) {
      return;
    }

    running.set(true);
    executor.scheduleAtFixedRate(
        () -> {
          Platform.runLater(
              () -> {
                int current = secondsRemaining.get();
                if (current > 0) {
                  secondsRemaining.set(current - 1);
                } else {
                  stopTimer();
                  timeUp.set(true);
                  System.out.println("TIME'S UP! Game Over!");
                }
              });
        },
        0,
        1,
        TimeUnit.SECONDS);
  }

  public void stopTimer() {
    running.set(false);
    if (executor != null && !executor.isShutdown()) {
      executor.shutdown();
    }
  }

  private void updateTimeDisplay(int seconds) {
    int minutes = seconds / 60;
    int secs = seconds % 60;
    timeDisplay.set(String.format("%02d:%02d", minutes, secs));
  }

  public IntegerProperty secondsRemainingProperty() {
    return secondsRemaining;
  }

  public StringProperty timeDisplayProperty() {
    return timeDisplay;
  }

  public BooleanProperty timeUpProperty() {
    return timeUp;
  }

  public boolean isTimeUp() {
    return timeUp.get();
  }

  public int getRemainingSeconds() {
    return secondsRemaining.get();
  }
}
