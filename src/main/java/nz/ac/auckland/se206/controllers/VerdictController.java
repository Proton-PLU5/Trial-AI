package nz.ac.auckland.se206.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest.Model;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.prompts.PromptEngineering;

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
  @FXML private TextArea rationaleJudgementTextArea;

  private ScheduledExecutorService finalTimerExecutor;
  private int finalSecondsRemaining = 5;
  private boolean choiceMade = false;
  private String optionChose = "";
  private String rationale = "";
  private String rationalePrompt = "";
  private ChatCompletionRequest chatCompletionRequest = null;
  private boolean rationaleSubmitted = false;

  @FXML
  private void initialize() {
    Media media = new Media(getClass().getResource("/sounds/decisionAudio.mp3").toExternalForm());
    MediaPlayer mediaPlayer = new MediaPlayer(media);
    mediaPlayer.play();

    updateFinalTimerDisplay();

    startFinalTimer();

    createChatCompletionResult();
  }

  
  /**
   * Creates and configures the ChatCompletionRequest object.
   */
  public void createChatCompletionResult() {
    try {
      ApiProxyConfig config = ApiProxyConfig.readConfig();
      chatCompletionRequest = new ChatCompletionRequest(
          config)
          .setN(1)
          .setTemperature(0.2)
          .setModel(Model.GPT_4_1_MINI)
          .setMaxTokens(500);
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
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
                if (rationaleSubmitted) {
                  return;
                }

                // If the time/seconds are still greater than 0 update it
                if (finalSecondsRemaining > 0) {
                  finalSecondsRemaining--;
                  updateFinalTimerDisplay();
                  // Otherwise say they got the answer wrong and they lost the game
                } else {
                  timeOutOption();
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
  private void handleYesClicked() {
    choiceMade = true;
    optionChose = "The player selected the 'Yes' option when asked if the AI's decision making process was reasonable, ethical and justified. Their rationale is the following: ";
    verdictCorrectLabel.setText("You made the correct decision.");
    handleVerdictMade();
  }

  @FXML
  private void handleNoClicked() {
    choiceMade = true;
    optionChose = "The player selected the 'No' option when asked if the AI's decision making process was reasonable, ethical and justified. Their rationale is the following: ";
    verdictCorrectLabel.setText("You made the wrong decision.");
    handleVerdictMade();
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
  private void timeOutOption() {
    if (!choiceMade) { // For when the user is on the choice button screen
      verdictTitleLabel1.setVisible(false);
      yesButton.setVisible(false);
      noButton.setVisible(false);
      verdictCorrectLabel.setText("You didn't make a decision in time.");
    }

    // Continue to rationale submission screen regardless of if the user made a verdict
    try {
      handleRationaleSubmitted();
    } catch (ApiProxyException e) {
        e.printStackTrace();
    }
  }

  @FXML
  private void handleRationaleSubmitted() throws ApiProxyException {
    rationaleSubmitted = true;
    verdictTitleLabel2.setVisible(false);
    submitButton.setVisible(false);
    rationaleTextArea.setVisible(false);
    verdictCorrectLabel.setVisible(true);
    rationaleCorrectLabel.setVisible(true);
    rationaleJudgementTextArea.setVisible(true);

    rationalePrompt += optionChose;
    // Read the rationale from the TextArea
    rationale = rationaleTextArea.getText().strip();

    if (rationale.isEmpty()) {
      rationaleCorrectLabel.setText("You didn't give a rationale."); 
    } else {
      // Add the rationale to the prompt
      rationalePrompt += rationale;
      System.out.println(rationalePrompt); // Debugging

      // Send the prompt and rationale to gpt
      String verdict = "verdict";
      Map<String, String> map = new HashMap<>();
      map.put("verdict", verdict);
      String promptFile = verdict + ".txt";
      String verdictPrompt = PromptEngineering.getPrompt(promptFile, map);
      ChatMessage msg = new ChatMessage("user", verdictPrompt + rationalePrompt);
      runGpt(msg);
    }
  }

  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {

    Thread gptThread =
        new Thread(
            () -> {
              try {
                chatCompletionRequest.addMessage(msg);
                ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
                Choice result = chatCompletionResult.getChoices().iterator().next();
                ChatMessage gptResponse = result.getChatMessage();
                chatCompletionRequest.addMessage(gptResponse);

                Platform.runLater(
                    () -> {
                      // Display GPT's response on screen
                      String content = gptResponse.getContent();
                      String [] arr = content.split("\\s+"); 
                      String rationaleJudgement="";

                      // Take first 4 words from GPT response
                      for(int i=0; i<4 ; i++){
                          rationaleJudgement = rationaleJudgement + " " + arr[i] ;         
                      }

                      // Remove first 4 words from GPT response
                      String rationaleSummary = content.replaceFirst("^(\\S+\\s+){4}", "");

                      rationaleCorrectLabel.setText(rationaleJudgement);
                      rationaleJudgementTextArea.setText(rationaleSummary);
                    });

              } catch (ApiProxyException e) {
                e.printStackTrace();
                Platform.runLater(() -> {});
              }
            });

    gptThread.setDaemon(true);
    gptThread.start();
    return null;
  }
}
