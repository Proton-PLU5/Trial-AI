package nz.ac.auckland.se206.controllers;

import java.io.IOException;

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

public class AiMemory extends MemoryController {

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

  private TimerService timerService;
  private Circle clipCircle;
  private static final double CIRCLE_RADIUS = 75.0;
  private boolean isXrayMode = false;
  private boolean isFirstTime = true;
  public static boolean hasChattedWithAi;

  public AiMemory() {
    super("prompts/witnessAi.txt");
  }

  @Override
  @FXML
  protected void initialize() {
    App.timer.addConsumer(getTimerConsumer());
    super.initialize();
    setupXrayEffect();
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
  private void interactableComplete() {
    if (isXrayMode && isFirstTime) {
      System.out.println("Identified conceled item");
      isFirstTime = false;
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

  private void handleGameOver() throws IOException {
    Stage stage = (Stage) chatBtn.getScene().getWindow();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/verdict.fxml"));
    Parent finalRoot = loader.load();
    stage.setScene(new Scene(finalRoot));
  }

  @FXML
  private void handleBackButton() {
    SceneManager.switchScene(SceneManager.Scenes.room);
  }

  @FXML
  private void handleOpenChatButtonClick(MouseEvent event) throws IOException {
    chatPanel.setVisible(true);
  }

  @Override
  protected void markAsChatted() {
    hasChattedWithAi = true;
  }
}
