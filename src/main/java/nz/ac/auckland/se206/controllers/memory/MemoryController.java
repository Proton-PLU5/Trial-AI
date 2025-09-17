package nz.ac.auckland.se206.controllers.memory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javafx.animation.PathTransition;
import javafx.animation.Transition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest.Model;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;

public abstract class MemoryController {

  protected ChatCompletionRequest chatCompletionRequest;

  @FXML private Label titleLabel;
  @FXML private Label descriptionLabel;

  // Chat Elements
  @FXML private AnchorPane chatPane;
  @FXML private Button sendButton;
  @FXML private Button chatButton;
  @FXML private TextArea textArea;
  @FXML private TextField textField;

  // Title Pane
  @FXML private AnchorPane titleBlock;

  // Navigation
  @FXML private Button goBackButton;

  // Timer
  @FXML private Label timerLabel;

  // Chat visibility state
  private boolean isChatVisible = false;
  private String roleOfCharacter = "";
  private String systemPrompt = "";

  // Constructor
  public MemoryController(String promptId) {
    createChatCompletionResult();
    loadInitialMessages(promptId);
  }

  protected void initialize() {
    chatPane.setVisible(false);

    // Animate title block to disappear after 3 seconds
    titleBlock.setVisible(true);
    Transition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
    delay.setOnFinished(event -> hideTitleBlock());
    delay.play();
  }

  /** Hides the title block with a slide-left animation. */
  private void hideTitleBlock() {
    PathTransition transition = new PathTransition();
    transition.setNode(titleBlock);
    transition.setDuration(javafx.util.Duration.seconds(1));
    transition.setPath(new javafx.scene.shape.Line(0, 0, -100, 0));
    transition.setCycleCount(1);
    transition.setOnFinished(event -> titleBlock.setVisible(false));
  }

  /** Handles the "Chat" button press event to toggle chat visibility. */
  protected void onChatButtonPressed() {
    isChatVisible = !isChatVisible;
    chatPane.setVisible(isChatVisible);
  }

  /** Handles the "Send" button press event to send a message. */
  protected void onSendButtonPressed() {
    String userInput = textField.getText();
    // Clear the text field
    textField.clear();

    if (!userInput.isEmpty()) {
      appendMessageToChat("User", userInput);

      // Create a new thread to handle the GPT request
      Task<Void> task =
          new Task<Void>() {
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
    try {
      Parent root = App.loadFxml("room.fxml");
      App.primaryStage.setScene(new Scene(root));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Creates and configures the ChatCompletionRequest object. */
  public void createChatCompletionResult() {
    try {
      ApiProxyConfig config = ApiProxyConfig.readConfig();
      chatCompletionRequest =
          new ChatCompletionRequest(config)
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
    this.chatCompletionRequest.addMessage("User", userInput);

    try {
      ChatCompletionResult chatCompletionResult = this.chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      ChatMessage message = result.getChatMessage();

      // Replace what role the AI generated:
      int index = message.getContent().indexOf(":");
      if (index != -1) {
        message.setContent(
            roleOfCharacter
                + ":\n"
                + message.getContent().substring(index + 1).replaceFirst("\n", ""));
      } else {
        message.setContent(roleOfCharacter + ":\n" + message.getContent().replaceFirst("\n", ""));
      }

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
      List<String> promptStrings =
          Files.readAllLines(Paths.get(promptUrl.toURI()), Charset.defaultCharset());
      return String.join("\n", promptStrings);
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
      throw new IllegalStateException(promptId + " not found");
    }
  }
}
