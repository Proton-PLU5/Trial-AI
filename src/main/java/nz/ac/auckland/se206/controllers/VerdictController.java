package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.utils.SceneManager;
import nz.ac.auckland.se206.utils.TimableScene;
import nz.ac.auckland.se206.utils.Timer;
import nz.ac.auckland.se206.utils.Tuple;

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
  @FXML
  private Button restartButton;

  private boolean choiceMade = false;
  private boolean isChoiceMadeCorrect = false;
  private String optionChose = "";
  private String rationale = "";
  private String rationalePrompt = "";
  private ChatCompletionRequest chatCompletionRequest = null;
  private boolean rationaleSubmitted = false;
  private Timer verdictTimer = null;
  private StringBuilder gameOverText = new StringBuilder("");
  private String systemPrompt = "";

  @FXML
  protected void initialize() {
    Media media = new Media(getClass().getResource("/sounds/verdict.mp3").toExternalForm());
    MediaPlayer mediaPlayer = new MediaPlayer(media);
    mediaPlayer.play();

    verdictCorrectLabel.setText("Your verdict was...");
    verdictCorrectLabel.setLayoutX(498);
    verdictCorrectLabel.setVisible(false);

    try {
      createChatCompletionResult();
      loadInitialMessages("prompts/verdict.txt");
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Add ourselves to the timer service
    App.timer.stopTimer();
    verdictTimer = new Timer(10, new Consumer<Void>() {
      @Override
      public void accept(Void t) {
        timeOutOption();
      }
    });
    verdictTimer.addConsumer(getTimerConsumer());
    verdictTimer.setCountDown(true);
    verdictTimer.buildTimer();

    // Disable submit button until rationale isn't empty
    submitButton.setDisable(true);
    rationaleTextArea.textProperty().addListener((obs, oldText, newText) -> {
      submitButton.setDisable(newText.strip().isEmpty());
    });
  }

  /**
   * Creates and configures the ChatCompletionRequest object.
   */
  public void createChatCompletionResult() {
    // This method creates the chat completion request for the verdict rationale
    try {
      ApiProxyConfig config = ApiProxyConfig.readConfig();
      chatCompletionRequest = new ChatCompletionRequest(
          config)
          .setN(1)
          .setTemperature(0.2)
          .setModel(Model.GPT_4_1_MINI)
          .setMaxTokens(2000);
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void onYesClicked() {
    // Make sure something happens when press yes
    choiceMade = true;
    isChoiceMadeCorrect = false;
    optionChose = "The player selected the 'Yes' option when asked if the AI's decision"
        + " making process was reasonable, ethical and justified."
        + " Their rationale is the following: ";
    gameOverText.append("You made the wrong verdict.");
    handleVerdictMade();
  }

  @FXML
  private void onNoClicked() {
    // Make sure what happens when no is clicked
    choiceMade = true;
    isChoiceMadeCorrect = true;
    optionChose = "The player selected the 'No' option when asked if the AI's decision making"
        + " process was reasonable, ethical and justified. Their rationale is the following: ";
    gameOverText.append("You made the correct verdict");
    handleVerdictMade();
  }

  @FXML
  private void handleVerdictMade() {
    // This method makes the objects inthe scene switch when the user makes a
    // verdict
    verdictTitleLabel1.setVisible(false);
    yesButton.setVisible(false);
    noButton.setVisible(false);
    verdictTitleLabel2.setVisible(true);
    submitButton.setVisible(true);
    rationaleTextArea.setVisible(true);
  }

  @FXML
  private void onRestartButtonPressed(ActionEvent event) {
    // This method handles the restart button
    App.createTimer();
    App.chatHistoryMap = new ArrayList<Tuple<String, String>>();

    HumanMemoryController.hasChattedWithHuman = false;
    HumanMemoryController.itemCollected = new HashMap<>();
    HumanMemoryController.itemToLabel = new HashMap<>();
    HumanMemoryController.isFirstTimeInteract = true;
    HumanMemoryController.completedOne = false;
    HumanMemoryController.completedTwo = false;
    HumanMemoryController.completedThree = false;

    DefendantMemoryController.hasChattedWithDefendant = false;
    DefendantMemoryController.loginSequenceCompleted = false;
    DefendantMemoryController.isFirstTimeInteract = true;
    DefendantMemoryController.scannedOne = false;
    DefendantMemoryController.scannedTwo = false;
    DefendantMemoryController.scannedThree = false;
    DefendantMemoryController.scannedFour = false;

    AiMemoryController.isFirstTimeInteract = true;
    AiMemoryController.hasChattedWithAi = false;

    RoomController.characterInteracted = new HashMap<>();
    RoomController.isFirstTimeInit = true;

    SceneManager.switchScene(SceneManager.Scenes.start);
    SceneManager.setStyleSheet("/css/style.css");
  }

  @FXML
  private void timeOutOption() {
    if (!choiceMade) { // For when the user is on the choice button screen
      verdictTitleLabel1.setVisible(false);
      yesButton.setVisible(false);
      noButton.setVisible(false);
    }

    // Continue to rationale submission screen regardless of if the user made a
    // verdict
    try {
      onRationaleSubmitted();
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void onRationaleSubmitted() throws ApiProxyException {
    if (!rationaleSubmitted) {
      rationaleSubmitted = true;
      verdictTimer.stopTimer();
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
        if (!choiceMade) {
          gameOverText.append("You didn't make a decision in time. Try again.");
        } else {
          gameOverText.append("You didn't give a rationale. Try again.");
        }
        verdictCorrectLabel.setText(gameOverText.toString());
        setVerdictCorrectLabelLayout();
        restartButton.setVisible(true);
      } else {
        // Add the rationale to the prompt
        rationalePrompt += rationale;
        System.out.println(rationalePrompt); // Debugging

        // Send the prompt and rationale to gpt
        ChatMessage msg = new ChatMessage("user", rationalePrompt);
        runGpt(msg);
      }
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
                    verdictCorrectLabel.setText(gameOverText.toString());
                    setVerdictCorrectLabelLayout(arr);
                  } else {
                    rationaleJudgementTextArea.setText(content);
                    verdictCorrectLabel.setText(gameOverText.toString());
                    setVerdictCorrectLabelLayout();
                  }

                  restartButton.setVisible(true);
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

  protected void loadInitialMessages(String promptId) {
    // Load the system prompt
    this.systemPrompt = loadPrompt(promptId);

    // Load initial messages
    this.systemPrompt += App.getChatHistoryString();
    this.systemPrompt += "The user's response will be provided below:\n";

    // Append the system prompt to the chat completion request
    this.chatCompletionRequest.addMessage("system", systemPrompt);
    System.out.println(systemPrompt);
  }

  protected String loadPrompt(String promptId) {
    // Loading the prompt into the chats and stuff
    try {
      URL promptUrl = this.getClass().getClassLoader().getResource(promptId);
      List<String> promptStrings = Files.readAllLines(
          Paths.get(promptUrl.toURI()), Charset.defaultCharset());
      return String.join("\n", promptStrings);
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
      throw new IllegalStateException(promptId + " not found");
    }
  }

  private void setVerdictCorrectLabelLayout() {
    // See if the verdict is correct and then change the scene
    if (rationale.isEmpty()) {
      if (!choiceMade) {
        verdictCorrectLabel.setLayoutX(201);
      } else {
        verdictCorrectLabel.setLayoutX(278);
      }
      verdictCorrectLabel.setText(gameOverText.toString());
    } else {
      if (!isChoiceMadeCorrect) {
        verdictCorrectLabel.setLayoutX(410);
      }
    }
  }

  private void setVerdictCorrectLabelLayout(String[] arr) {
    if (arr[9].equals("correct")) {
      verdictCorrectLabel.setLayoutX(47);
    } else {
      verdictCorrectLabel.setLayoutX(25);
    }
  }
}
