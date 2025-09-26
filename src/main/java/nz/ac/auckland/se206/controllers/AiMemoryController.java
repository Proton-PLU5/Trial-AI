package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.memory.MemoryController;
import nz.ac.auckland.se206.utils.SceneManager;

public class AiMemoryController extends MemoryController {

  private static final double CIRCLE_RADIUS = 75.0;
  public static boolean isFirstTimeInteract = true;
  public static boolean hasChattedWithAi;

  @FXML
  private Button roomBtn;
  @FXML
  private Button chatBtn;
  @FXML
  private Label timerLabel;
  @FXML
  private Pane chatPanel;
  @FXML
  private Pane rootPane;
  @FXML
  private ImageView mainImageView;
  @FXML
  private ImageView xrayImageView;
  @FXML
  private Rectangle overlayRectangle;

  private Circle clipCircle;
  private boolean isXrayMode = false;

  public AiMemoryController() {
    super("prompts/witnessAi.txt");
  }

  @Override
  @FXML
  protected void initialize() {
    App.timer.addConsumer(getTimerConsumer());
    super.initialize();
    setupXrayEffect();
    this.roleOfCharacter = "Checkout Bot";
  }

  private void setupXrayEffect() {
    // Create the clipping circle
    clipCircle = new Circle(CIRCLE_RADIUS);

    // Initially hide the xray image
    xrayImageView.setVisible(false);
    xrayImageView.setClip(clipCircle);

    // Add mouse event handlers to the root pane
    rootPane.setOnMouseMoved(this::handleMouseMove);
    rootPane.setOnMouseClicked(this::handleMouseClicked);
    rootPane.setOnMouseExited(this::handleMouseExited);
  }

  @FXML
  private void handleMouseMove(MouseEvent event) {
    // Only show effect if in xray mode
    if (isXrayMode) {
      updateClipPosition(event.getX(), event.getY());
    }
  }

  @FXML
  private void handleMouseClicked(MouseEvent event) {
    // Toggle between xray mode and normal mode
    isXrayMode = !isXrayMode;

    if (isXrayMode) {
      // Enter xray mode
      rootPane.setCursor(Cursor.NONE);
      xrayImageView.setVisible(true);
      updateClipPosition(event.getX(), event.getY());
    } else {
      // Exit xray mode
      rootPane.setCursor(Cursor.DEFAULT);
      xrayImageView.setVisible(false);
    }
  }

  @FXML
  private void handleMouseExited(MouseEvent event) {
    // Hide the xray effect when mouse leaves the pane and reset to normal mode
    isXrayMode = false;
    rootPane.setCursor(Cursor.DEFAULT);
    xrayImageView.setVisible(false);
  }

  // Add interaction event when concealed item is detected here
  @FXML
  private void interactableDone() {
    if (isXrayMode && isFirstTimeInteract) {
      // LLM sends message when interactable is done
      Task<Void> interactableDoneTask = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
          String output = sendGptRequest(loadPrompt("prompts/aiInteractableDone.txt"));

          // Update the chat area with the AI's response
          Platform.runLater(() -> {
            appendMessageToChat(roleOfCharacter, output);
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

      isFirstTimeInteract = false;
    }
  }

  private void updateClipPosition(double mouseX, double mouseY) {
    // Convert root pane coordinates to image coordinates
    double imageX = mouseX - xrayImageView.getLayoutX();
    double imageY = mouseY - xrayImageView.getLayoutY();

    // Update the clip circle position
    clipCircle.setCenterX(imageX);
    clipCircle.setCenterY(imageY);
  }

  @Override
  protected void markAsChatted() {
    hasChattedWithAi = true;
  }
}
