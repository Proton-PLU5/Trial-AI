package nz.ac.auckland.se206.controllers.memory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.management.RuntimeErrorException;

import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest.Model;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.RoomController;
import nz.ac.auckland.se206.utils.SceneManager;
import nz.ac.auckland.se206.utils.TimableScene;

public abstract class MemoryController implements TimableScene {

  protected ChatCompletionRequest chatCompletionRequest;

  // Chat Elements
  @FXML
  private AnchorPane chatPane;
  @FXML
  private Button sendButton;
  @FXML
  private Button chatButton;
  @FXML
  private TextArea textArea;
  @FXML
  private TextField textField;

  // Navigation
  @FXML
  private Button goBackButton;

  // Timer
  @FXML
  private Label timerLabel;

  // Title
  @FXML
  protected AnchorPane titleBlock;
  @FXML
  protected Label titleLabel;
  @FXML
  protected Label descriptionLabel;

  // Chat visibility state
  private boolean isChatVisible = false;
  protected String roleOfCharacter = "";
  private String systemPrompt = "";

  private SequentialTransition hideLeftTransition;

  // Constructor
  public MemoryController(String promptId) {
    try {
      createChatCompletionResult();
      loadInitialMessages(promptId);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  protected void initialize() {
    chatPane.setVisible(false);
  }

  /** Handles the "Chat" button press event to toggle chat visibility. */
  @FXML
  protected void onChatButtonPressed() {
    isChatVisible = !isChatVisible;
    chatPane.setVisible(isChatVisible);
  }

  /** Handles the "Send" button press event to send a message. */
  @FXML
  protected void onSendButtonPressed() {
    String userInput = textField.getText();
    // Clear the text field
    textField.clear();

    if (!userInput.isEmpty()) {
      markAsChatted();
      appendMessageToChat("User", userInput);

      // Create a new thread to handle the GPT request
      Task<Void> task = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
          String output = sendGPTRequest(userInput);

          // Update the chat area with the AI's response
          appendMessageToChat(roleOfCharacter, output);

          // Update chat history in App class
          App.chatHistory.append(roleOfCharacter + ":\n" + output + "\n");

          // Re-enable the text field and send button after processing
          textField.setDisable(false);
          textField.setPromptText("Enter your message.");
          sendButton.setDisable(false);

          return null;
        }
      };
      Thread gptRequestThread = new Thread(task);
      gptRequestThread.setDaemon(true);
      gptRequestThread.start();

      // Lock the text field and send button while processing
      textField.setDisable(true);
      textField.setPromptText("Waiting for response...");
      sendButton.setDisable(true);
    }
  }

  /**
   * Appends a message to the chat area.
   *
   * @param message The message to append.
   */
  protected void appendMessageToChat(String role, String message) {
    textArea.appendText(role + ":\n" + message + "\n");
    App.chatHistory.append(role + ":\n" + message + "\n");
  }

  /** Handles the "Go Back" button press event. */
  @FXML
  protected void onGoBackButtonPressed() {
    SceneManager.switchScene(SceneManager.Scenes.room);
    SceneManager.setStyleSheet("/css/style.css");
  }

  /** Creates and configures the ChatCompletionRequest object. */
  public void createChatCompletionResult() {
    try {
      ApiProxyConfig config = ApiProxyConfig.readConfig();
      chatCompletionRequest = new ChatCompletionRequest(config)
          .setN(1)
          .setTemperature(0.2)
          .setModel(Model.GPT_4_1_MINI)
          .setMaxTokens(500);
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sends a GPT request with the user's input and returns the AI's response.
   *
   * @param userInput The user's input message.
   * @return The AI's response message.
   */
  protected String sendGPTRequest(String userInput) {
    this.chatCompletionRequest.addMessage("user", userInput);

    try {
      ChatCompletionResult chatCompletionResult = this.chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      ChatMessage message = result.getChatMessage();

      // Replace what role the AI generated:
      message.setContent("\n" + roleOfCharacter + ":\n" + message.getContent());

      this.chatCompletionRequest.addMessage(message);
      return message.getContent();
    } catch (ApiProxyException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to get response from GPT");
    }
  }

  /**
   * Loads the initial messages including the system prompt.
   *
   * @param promptId The ID of the prompt to load.
   */
  protected void loadInitialMessages(String promptId) {
    // Load initial messages
    this.systemPrompt = App.chatHistory.toString();

    // Load the system prompt
    this.systemPrompt += loadPrompt(promptId);

    // Append the system prompt to the chat completion request
    this.chatCompletionRequest.addMessage("system", systemPrompt);
  }

  /**
   * Loads the prompt from the specified file.
   *
   * @param promptId the ID of the prompt to load
   * @return the loaded prompt as a string
   */
  protected String loadPrompt(String promptId) {
    try {
      URL promptUrl = this.getClass().getClassLoader().getResource(promptId);
      List<String> promptStrings = Files.readAllLines(Paths.get(promptUrl.toURI()), Charset.defaultCharset());
      return String.join("\n", promptStrings);
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
      throw new IllegalStateException(promptId + " not found");
    }
  }

  protected void createTitleDisappearAnimation() {
    TranslateTransition moveLeftTransition = new TranslateTransition(Duration.seconds(1), titleBlock);
    moveLeftTransition = new TranslateTransition(Duration.seconds(1), titleBlock);
    moveLeftTransition.setFromX(0);
    moveLeftTransition.setToX(-700);
    moveLeftTransition.setOnFinished(event -> titleBlock.setVisible(false));

    PauseTransition pause = new PauseTransition(Duration.seconds(4));

    titleBlock.setTranslateX(0);

    hideLeftTransition = new SequentialTransition(pause, moveLeftTransition);
    hideLeftTransition.play();
  }

  protected void stopTitleDisappearAnimation() {
    hideLeftTransition.stop();
  }

  public Label getTimerLabel() {
    return timerLabel;
  }

  protected void markAsChatted() {
    return;
  }
}
