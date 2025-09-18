package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest.Model;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.prompts.PromptEngineering;
import nz.ac.auckland.se206.utils.SceneManager;

/**
 * Controller class for the chat view. Handles user interactions and communication with the GPT
 * model via the API proxy.
 */
public class ChatController {

  private static final Map<String, String> ROLE_DISPLAY_NAMES = new HashMap<>();

  @FXML private TextArea txtaChat;
  @FXML private TextField txtInput;
  @FXML private Button btnSend;
  @FXML private Button backBtn;

  private ChatCompletionRequest chatCompletionRequest;
  private String profession;
  private Pane chatPanelContainer;

  static {
    ROLE_DISPLAY_NAMES.put("defendant", "Defendant");
    ROLE_DISPLAY_NAMES.put("witnessHuman", "Human Witness");
    ROLE_DISPLAY_NAMES.put("witnessAi", "AI Witness");
  }

  /**
   * Initializes the chat view.
   *
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  @FXML
  public void initialize() {}

  /**
   * Generates the system prompt based on the profession.
   *
   * @return the system prompt string
   */

  /**
   * Sets the profession for the chat context and initializes the ChatCompletionRequest.
   *
   * @param profession the profession to set
   */
  public void setProfession(String profession) {
    this.profession = profession;

    Thread setupThread =
        new Thread(
            () -> {
              try {
                // This might be slow - do in background
                ApiProxyConfig config = ApiProxyConfig.readConfig();

                // This is definitely slow
                Map<String, String> map = new HashMap<>();
                map.put("profession", profession);
                String promptFile = profession + ".txt";
                String systemPrompt = PromptEngineering.getPrompt(promptFile, map);

                // Back to main thread to set up chat and make API call
                Platform.runLater(
                    () -> {
                      chatCompletionRequest =
                          new ChatCompletionRequest(config)
                              .setN(1)
                              .setTemperature(0.7)
                              .setTopP(0.9)
                              .setModel(Model.GPT_4_1_MINI)
                              .setMaxTokens(100);

                      try {
                        runGpt(new ChatMessage("system", systemPrompt));
                      } catch (ApiProxyException e) {
                        e.printStackTrace();
                      }
                    });

              } catch (ApiProxyException e) {
                Platform.runLater(
                    () -> {
                      e.printStackTrace();
                    });
              }
            });

    setupThread.setDaemon(true);
    setupThread.start();
  }

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  private void appendChatMessage(ChatMessage msg) {

    String content = msg.getContent();
    String displayName;

    if ("user".equals(msg.getRole())) {
      displayName = "User";
    } else {
      displayName = ROLE_DISPLAY_NAMES.get(profession);
    }
    txtaChat.appendText(displayName + ": " + msg.getContent() + "\n\n");
  }

  public void receiveContextUpdate(String fromProfession, String message) {
    if (chatCompletionRequest != null) {
      Thread contextThread =
          new Thread(
              () -> {
                try {
                  String fromDisplayName = ROLE_DISPLAY_NAMES.get(fromProfession);
                  String contextMessage =
                      String.format(
                          "Context update: The %s just said: \"%s\"", fromDisplayName, message);

                  ChatMessage contextMsg = new ChatMessage("system", contextMessage);
                  chatCompletionRequest.addMessage(contextMsg);

                } catch (Exception e) {
                  e.printStackTrace();
                }
              });

      contextThread.setDaemon(true);
      contextThread.start();
    }
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {

    Thread gptThread =
        new Thread(
            () -> {
              try {
                chatCompletionRequest.addMessage(msg);
                ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
                Choice result = chatCompletionResult.getChoices().iterator().next();
                chatCompletionRequest.addMessage(result.getChatMessage());

                Platform.runLater(
                    () -> {
                      appendChatMessage(result.getChatMessage());
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

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {
    String message = txtInput.getText().trim();
    if (message.isEmpty()) {
      return;
    }
    txtInput.clear();
    ChatMessage msg = new ChatMessage("user", message);
    appendChatMessage(msg);
    runGpt(msg);
  }

  /**
   * Navigates back to the previous view.
   *
   * @param event the action event triggered by the go back button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    if (chatPanelContainer != null) {
      chatPanelContainer.setVisible(false);
    }
  }

  public void setChatPanelContainer(Pane container) {
    this.chatPanelContainer = container;
  }
}
