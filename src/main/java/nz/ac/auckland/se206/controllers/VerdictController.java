package nz.ac.auckland.se206.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.utils.TimableScene;
import nz.ac.auckland.se206.utils.Timer;

public class VerdictController implements TimableScene {

  @FXML
  private Button yesButton;
  @FXML
  private Button noButton;
  @FXML
  private Button submitButton;
  @FXML
  private Label verdictTitleLabel1;
  @FXML
  private Label verdictTitleLabel2;
  @FXML
  private Label timerLabel;
  @FXML
  private Label verdictCorrectLabel;
  @FXML
  private ImageView imageView;
  @FXML
  private TextArea rationaleTextArea;
  @FXML
  private TextArea rationaleJudgementTextArea;

  private boolean choiceMade = false;
  private boolean isChoiceMadeCorrect = false;
  private String optionChose = "";
  private String rationale = "";
  private String rationalePrompt = "";
  private ChatCompletionRequest chatCompletionRequest = null;
  private boolean rationaleSubmitted = false;
  public Timer verdictTimer = null;
  private StringBuilder gameOverText = new StringBuilder("");

  @FXML
  private void initialize() {
    Media media = new Media(getClass().getResource("/sounds/verdict.mp3").toExternalForm());
    MediaPlayer mediaPlayer = new MediaPlayer(media);
    mediaPlayer.play();

    // updateFinalTimerDisplay();

    // startFinalTimer();

    createChatCompletionResult();

    // Add ourselves to the timer service
    App.timer.stopTimer();
    verdictTimer = new Timer(2 * 60, new Consumer<Void>() {
      @Override
      public void accept(Void t) {
        timeOutOption();
      }
    });
    verdictTimer.addConsumer(getTimerConsumer());
    verdictTimer.setCountDown(true);
    verdictTimer.buildTimer();
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

  @FXML
  private void handleYesClicked() {
    choiceMade = true;
    isChoiceMadeCorrect = false;
    optionChose = "The player selected the 'Yes' option when asked if the AI's decision making process was reasonable, ethical and justified. Their rationale is the following: ";
    gameOverText.append("You made the wrong verdict.");
    handleVerdictMade();
  }

  @FXML
  private void handleNoClicked() {
    choiceMade = true;
    isChoiceMadeCorrect = true;
    optionChose = "The player selected the 'No' option when asked if the AI's decision making process was reasonable, ethical and justified. Their rationale is the following: ";
    gameOverText.append("You made the correct verdict");
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
      verdictCorrectLabel.setText("You didn't make a decision in time. Try again.");
    }

    // Continue to rationale submission screen regardless of if the user made a
    // verdict
    try {
      handleRationaleSubmitted();
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleRationaleSubmitted() throws ApiProxyException {
    verdictTimer.stopTimer();
    rationaleSubmitted = true;
    verdictTitleLabel2.setVisible(false);
    submitButton.setVisible(false);
    rationaleTextArea.setVisible(false);
    verdictCorrectLabel.setVisible(true);
    rationaleJudgementTextArea.setVisible(true);

    rationalePrompt += optionChose;
    // Read the rationale from the TextArea
    rationale = rationaleTextArea.getText().strip();

    if (rationale.isEmpty()) {
      gameOverText.setLength(0);
      gameOverText.append("You didn't give a rationale. Try again.");
      verdictCorrectLabel.setText(gameOverText.toString());
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

    Thread gptThread = new Thread(
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
                  String[] arr = content.split("\\s+");

                  if (isChoiceMadeCorrect) {
                    // Take first 5 words from GPT response and add it to gameOverText
                    for (int i = 0; i < 5; i++) {
                      gameOverText.append(" " + arr[i]);
                    }

                    // Remove first 5 words from GPT response
                    String rationaleSummary = content.replaceFirst("^(\\S+\\s+){5}", "");
                    rationaleJudgementTextArea.setText(rationaleSummary);
                    verdictCorrectLabel.setLayoutX(50);
                  } else {
                    rationaleJudgementTextArea.setText(content);
                  }

                  verdictCorrectLabel.setText(gameOverText.toString());
                });

          } catch (ApiProxyException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
            });
          }
        });

    gptThread.setDaemon(true);
    gptThread.start();
    return null;
  }

  @Override
  public Label getTimerLabel() {
    return timerLabel;
  }
}
