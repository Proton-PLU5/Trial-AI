package nz.ac.auckland.se206.controllers;

import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class VerdictController {

  @FXML private Button yesButton;
  @FXML private Button noButton;
  @FXML private Button submitButton;
  @FXML private Label verdictTitleLabel1;
  @FXML private Label verdictTitleLabel2;
  @FXML private Label correctLabel;
  @FXML private Label incorrectLabel;
  @FXML private Label timeoutLabel;
  @FXML private Label timerLabel;
  @FXML private Label verdictCorrectLabel;
  @FXML private Label rationaleCorrectLabel;
  @FXML private ImageView imageView;
  @FXML private TextArea rationaleTextArea;
  @FXML private TextField rationaleJudgementTextField;

  private ScheduledExecutorService finalTimerExecutor;
  private int finalSecondsRemaining = 60;
  private boolean choiceMade;
  private String rationale = "";

  @FXML
  private void initialize() {
    Media media = new Media(getClass().getResource("/sounds/decisionAudio.mp3").toExternalForm());
    MediaPlayer mediaPlayer = new MediaPlayer(media);
    mediaPlayer.play();

    updateFinalTimerDisplay();

    startFinalTimer();
  }

  private void startFinalTimer() {
    // Start and use the timer as another thread and then when the time ends it says the user lost
    finalTimerExecutor =
        Executors.newSingleThreadScheduledExecutor(
            r -> {
              Thread t = new Thread(r);
              t.setDaemon(true);
              return t;
            });
    finalTimerExecutor.scheduleAtFixedRate(
        () -> {
          Platform.runLater(
              () -> {
                if (choiceMade) {
                  return;
                }

                // If the time/seconds are still greater than 0 update it
                if (finalSecondsRemaining > 0) {
                  finalSecondsRemaining--;
                  updateFinalTimerDisplay();
                  // Otherwise say they got the answer wrong and they lost the game
                } else {
                  handleNoClicked();
                }
              });
        },
        1,
        1,
        TimeUnit.SECONDS);
  }

  private void updateFinalTimerDisplay() {
    if (timerLabel != null) {
      timerLabel.setText("00:" + String.format("%02d", finalSecondsRemaining));

      // Change color based on time remaining
      if (finalSecondsRemaining <= 3) {
        timerLabel.setStyle("-fx-text-fill: red; -fx-font-size: 24px; -fx-font-weight: bold;");
      } else if (finalSecondsRemaining <= 5) {
        timerLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 20px; -fx-font-weight: bold;");
      } else {
        timerLabel.setStyle("-fx-text-fill: green; -fx-font-size: 18px; -fx-font-weight: bold;");
      }
    }
  }

  @FXML
  private void handleVerdictMade() {
    verdictTitleLabel1.setVisible(false);
    yesButton.setVisible(false);
    noButton.setVisible(false);
    verdictTitleLabel2.setVisible(true);
    submitButton.setVisible(true);
    rationaleTextArea.setVisible(true);
  }

  @FXML
  private void handleNoClicked() {
    rationale += "The player selected the 'No' option when asked if the AI's decision making process was reasonable, ethical and justified. Their rationale is the following: ";
    handleVerdictMade();
  }

  @FXML
  private void timeOutOption() {
    try {
      String path = "/images/wrong.png";
      InputStream stream = getClass().getResourceAsStream(path);
      if (stream == null) {
        throw new IllegalArgumentException("Image not found: " + path);
      }
      choiceMade = true;
      Image image = new Image(stream);
      imageView.setImage(image);
      imageView.setVisible(true);
      timeoutLabel.setVisible(true);
      verdictTitleLabel1.setVisible(false);
      yesButton.setVisible(false);
      noButton.setVisible(false);
      timerLabel.setVisible(false);
    } catch (Exception e) {
      System.err.println("Failed to load image: " + e.getMessage());
    }
  }

  @FXML
  private void handleYesClicked() {
    rationale += "The player selected the 'Yes' option when asked if the AI's decision making process was reasonable, ethical and justified. Their rationale is the following: ";
    handleVerdictMade();
  }

  @FXML
  private void handleRationaleSubmitted() {
    verdictTitleLabel2.setVisible(false);
    submitButton.setVisible(false);
    rationaleTextArea.setVisible(false);
    verdictCorrectLabel.setVisible(true);
    rationaleCorrectLabel.setVisible(true);
    rationaleJudgementTextField.setVisible(true);

    // Read the rationale from the TextArea
    rationale += rationaleTextArea.getText();
    System.out.println(rationale); // Debugging
  }
}
