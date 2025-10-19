package nz.ac.auckland.se206.controllers.memory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest.Model;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.utils.Card;
import nz.ac.auckland.se206.utils.LoadingAnimationCard;
import nz.ac.auckland.se206.utils.SceneManager;
import nz.ac.auckland.se206.utils.TimableScene;
import nz.ac.auckland.se206.utils.Tuple;

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
  private GridPane conversationGridPane;
  @FXML
  protected ScrollPane conversationScrollPane;
  @FXML
  private TextArea textArea;

  // Navigation
  @FXML
  private Button goBackButton;

  // Timer
  @FXML
  private Label timerLabel;

  @FXML
  private Label timerLabelText;

  // Title
  @FXML
  protected AnchorPane titleBlock;
  @FXML
  protected Label titleLabel;
  @FXML
  protected Label descriptionLabel;

  // Notification
  @FXML
  protected StackPane notificationPane;

  @FXML
  private AnchorPane loadingPane;
  @FXML
  private ImageView trolleyImage;
  @FXML
  private ImageView orangeJuiceImage;
  @FXML
  private ImageView appleJuiceImage;
  @FXML
  private ImageView candyBarImage;

  // Chat visibility state
  private boolean isChatVisible = false;
  protected String roleOfCharacter = "";
  private String systemPrompt = "";

  private SequentialTransition hideLeftTransition;

  protected AudioClip notificationSound;
  protected String promptId = "";
  protected final String initalPrompt = "If there are no previous"
      + " messages you can talk about with the user,"
      + "then you should introduce yourself to the user with a short and concise message. "
      + "Otherwise, you should respond to the previous conversations.";

  private LoadingAnimationCard loadingAnimationCard;

  // Constructor
  public MemoryController(String promptId) {
    try {
      createChatCompletionResult();
      this.promptId = promptId;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  protected void initialize() {
    // Load initial messages
    loadInitialMessages(promptId);

    // Initially hide chat and notification panes
    notificationPane.setVisible(false);
    chatPane.setVisible(false);

    var resource = getClass().getResource("/sounds/notification.wav");
    notificationSound = new AudioClip(resource.toExternalForm());

    // Add some spacing between messages
    conversationGridPane.setVgap(10);

    // Send with enter key
    textArea.setOnKeyPressed(event -> {
      switch (event.getCode()) {
        case ENTER:
          if (event.isShiftDown()) {
            textArea.appendText("\n");
          } else {
            event.consume(); // Prevents adding a new line
            onSendButtonPressed();
          }
          break;
        default:
          break;
      }
    });
  }

  /** Handles the "Chat" button press event to toggle chat visibility. */
  @FXML
  protected void onChatButtonPressed() {
    isChatVisible = !isChatVisible;
    chatPane.setVisible(isChatVisible);

    // Hide the notification pane when chat is opened
    if (isChatVisible) {
      notificationPane.setVisible(false);
      chatButton.setText("Close Chat");
    } else {
      chatButton.setText("Open Chat");
    }
  }

  /** Handles the "Send" button press event to send a message. */
  @FXML
  protected void onSendButtonPressed() {
    String userInput = textArea.getText().strip();
    // Clear the text field
    textArea.clear();

    if (!userInput.isEmpty()) {
      markAsChatted();
      appendMessageToChat("User", userInput);

      Platform.runLater(() -> {
        PauseTransition pt = new PauseTransition(Duration.millis(50));
        pt.setOnFinished(e -> conversationScrollPane.setVvalue(1.0));
        pt.play();
      });

      // Create a new thread to handle the GPT request
      Task<Void> task = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
          String output = sendGptRequest(userInput);

          System.out.println("AI Response: " + output); // Debugging

          // Update the chat area with the AI's response
          Platform.runLater(() -> {
            loadingAnimationCard.finishLoadingAnimation(roleOfCharacter, output, conversationGridPane.getWidth() - 40);
            notificationSound.play();
            PauseTransition pt = new PauseTransition(Duration.millis(50));
            pt.setOnFinished(e -> conversationScrollPane.setVvalue(1.0));
            pt.play();
          });

          // Re-enable the text field and send button after processing
          textArea.setDisable(false);
          textArea.setPromptText("Enter your message.");
          sendButton.setDisable(false);

          return null;
        }
      };
      Thread gptRequestThread = new Thread(task);
      gptRequestThread.setDaemon(true);
      gptRequestThread.start();

      // Lock the text field and send button while processing
      textArea.setDisable(true);
      textArea.setPromptText("Waiting for response...");
      sendButton.setDisable(true);
      showLoadingAnimation();
    }
  }

  /**
   * Appends a message to the chat area.
   *
   * @param message The message to append.
   */
  protected void appendMessageToChat(String role, String message) {

    // Remove leading newline characters from the message
    if (message.startsWith("\n")) {
      message = message.replaceFirst("\n", "");
    }

    // Create a Card for the message
    Card messageCard = new Card(role, message, conversationGridPane.getWidth() - 40);
    messageCard.setUpCard();

    // Add the message to the next available row in the grid pane
    int nextRow = conversationGridPane.getRowCount();
    conversationGridPane.add(messageCard, 0, nextRow);
    GridPane.setHalignment(messageCard, HPos.LEFT);
    GridPane.setValignment(messageCard, VPos.TOP);
  }

  /** Handles the "Go Back" button press event. */
  @FXML
  protected void onGoBackButtonPressed() {
    SceneManager.switchScene(SceneManager.Scenes.room);
    SceneManager.setStyleSheet("/css/style.css");
  }

  /** Creates and configures the ChatCompletionRequest object. */
  public void createChatCompletionResult() {
    // This method initialises the chat completion request for the memory chat
    try {
      ApiProxyConfig config = ApiProxyConfig.readConfig();
      chatCompletionRequest = new ChatCompletionRequest(config)
          .setN(1)
          .setTemperature(0.7)
          .setTopP(0.9)
          .setModel(Model.GPT_4_1_MINI)
          .setMaxTokens(500);
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sends a notification to the user if they receive a new message while the chat
   * pane is closed.
   */
  protected void sendNotification() {
    // If the chat pane is not visible, show the notification pane
    if (!isChatVisible) {

      notificationSound.play();

      // Display the notification pane
      notificationPane.setVisible(true);

      // Create a "bounce" animation for the notification pane
      TranslateTransition moveUpTransition = new TranslateTransition(
          Duration.seconds(0.2), notificationPane);
      moveUpTransition.setFromY(-30);
      moveUpTransition.setToY(0);
      moveUpTransition.setInterpolator(Interpolator.EASE_IN);

      TranslateTransition moveDownTransition = new TranslateTransition(
          Duration.seconds(0.2), notificationPane);
      moveDownTransition.setFromY(0);
      moveDownTransition.setToY(-30);
      moveDownTransition.setInterpolator(Interpolator.EASE_OUT);

      SequentialTransition bounce = new SequentialTransition(moveDownTransition, moveUpTransition);
      bounce.setCycleCount(2);
      bounce.play();
    }
  }

  /**
   * Sends a GPT request with the user's input and returns the AI's response.
   *
   * @param userInput The user's input message.
   * @return The AI's response message.
   */
  protected String sendGptRequest(String userInput) {
    if (!userInput.isEmpty()) {
      // This method sends the user input to the GPT model and returns the response
      this.chatCompletionRequest.addMessage("user", userInput);
      App.chatHistoryMap.add(new Tuple<String, String>("User" + "\0" + roleOfCharacter, userInput));
    }

    try {
      ChatCompletionResult chatCompletionResult = this.chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      ChatMessage message = result.getChatMessage();

      this.chatCompletionRequest.addMessage(message);
      // Update chat history in App class

      App.chatHistoryMap.add(new Tuple<String, String>(roleOfCharacter, message.getContent()));
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
    Platform.runLater(() -> {
      // Load initial messages
      StringBuilder systemPromptBuilder = new StringBuilder();
      boolean hasBeenChattedWith = false;

      // Load the chat history, iterate through the map and append to the system
      // prompt.
      for (Tuple<String, String> entry : App.chatHistoryMap) {
        String role = entry.getKey();
        String message = entry.getValue();
        String displayRole = role.split("\0")[0];
        systemPromptBuilder.append(displayRole).append(":\n").append(message).append("\n");
        // Show all messages for this character (AI and User)
        if (role.equals(this.roleOfCharacter) || role.endsWith(this.roleOfCharacter)) {
          hasBeenChattedWith = true;
          appendMessageToChat(displayRole, message);
        }
      }

      Platform.runLater(() -> {
        PauseTransition pt = new PauseTransition(Duration.millis(50));
        pt.setOnFinished(e -> conversationScrollPane.setVvalue(1.0));
        pt.play();
      });

      if (!hasBeenChattedWith) {
        Platform.runLater(() -> {
          Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
              chatCompletionRequest.addMessage("system", initalPrompt);
              String output = sendGptRequest("");
              // Update the chat area with the AI's response
              Platform.runLater(() -> {
                appendMessageToChat(roleOfCharacter, output);
                sendNotification();
                notificationSound.play();
                PauseTransition pt = new PauseTransition(Duration.millis(50));
                pt.setOnFinished(e -> conversationScrollPane.setVvalue(1.0));
                pt.play();
              });

              return null;
            }
          };

          Thread gptRequestThread = new Thread(task);
          gptRequestThread.setDaemon(true);
          gptRequestThread.start();
        });
      }

      // Load the system prompt
      systemPromptBuilder.append(loadPrompt(promptId));
      this.systemPrompt = systemPromptBuilder.toString();

      // Append the system prompt to the chat completion request
      this.chatCompletionRequest.addMessage("system", this.systemPrompt);
    });
  }

  /**
   * Loads the prompt from the specified file.
   *
   * @param promptId the ID of the prompt to load
   * @return the loaded prompt as a string
   */
  protected String loadPrompt(String promptId) {
    // This method loads the prompt from a file and into the respective llms chat
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

  protected void createTitleDisappearAnimation() {
    // This method creates the animation for the title to disappear after a few
    // seconds
    TranslateTransition moveLeftTransition = new TranslateTransition(
        Duration.seconds(1), titleBlock);
    moveLeftTransition.setFromX(0);
    moveLeftTransition.setToX(-700);
    moveLeftTransition.setOnFinished(event -> titleBlock.setVisible(false));

    PauseTransition pause = new PauseTransition(Duration.seconds(10));

    titleBlock.setTranslateX(0);

    hideLeftTransition = new SequentialTransition(pause, moveLeftTransition);
    hideLeftTransition.play();
  }

  protected void stopTitleDisappearAnimation() {
    hideLeftTransition.stop();
  }

  @Override
  public Label getTimerLabel() {
    return timerLabel;
  }

  protected void markAsChatted() {
  }

  /**
   * Handles the completion of an interactable element.
   */
  @FXML
  protected boolean interactableDone(boolean isFirstTimeInteract, String locationOfContextString,
      String locationOfDoneString, String interactableContext) {
    if (isFirstTimeInteract) {
      minorInteractableDoneString(locationOfContextString, interactableContext);

      Task<Void> interactableDoneTask = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
          chatCompletionRequest.addMessage("system", loadPrompt("prompts/" + locationOfDoneString));
          String output = sendGptRequest("");

          // Update the chat area with the AI's response
          Platform.runLater(() -> {
            appendMessageToChat(roleOfCharacter, output);
          });

          Platform.runLater(() -> {
            PauseTransition pt = new PauseTransition(Duration.millis(50));
            pt.setOnFinished(e -> conversationScrollPane.setVvalue(1.0));
            pt.play();
          });

          // Send a notification to the user
          sendNotification();
          return null;
        }
      };

      // Use a thread to perform the task concurrently
      Thread additionalInfoThread = new Thread(interactableDoneTask);
      additionalInfoThread.setDaemon(true);
      additionalInfoThread.start();

      return false;
    }
    return isFirstTimeInteract;
  }

  /**
   * Handles the completion of an interactable element.
   */
  @FXML
  protected void minorInteractableDoneString(String locationOfContextString,
      String interactableContext) {
    // this method puts the string to llm when interactable is done
    try (InputStream is = getClass().getClassLoader()
        .getResourceAsStream("prompts/" + locationOfContextString)) {
      if (is == null) {
        throw new IOException("Resource not found: prompts/" + locationOfContextString);
      }
      interactableContext = new String(
          is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
    App.chatHistoryMap.add(new Tuple<String, String>("Context", interactableContext));
    System.out.println(App.getChatHistoryString());
  }

  private void showLoadingAnimation() {
    Platform.runLater(() -> {
      double cardWidth = conversationGridPane.getWidth() - 40;
      LoadingAnimationCard loadingCard = new LoadingAnimationCard("System", "Loading...", cardWidth);
      conversationGridPane.add(loadingCard, 0, conversationGridPane.getRowCount());
      GridPane.setHalignment(loadingCard, HPos.LEFT);
      GridPane.setValignment(loadingCard, VPos.TOP);

      this.loadingAnimationCard = loadingCard;
    });
  }
}
