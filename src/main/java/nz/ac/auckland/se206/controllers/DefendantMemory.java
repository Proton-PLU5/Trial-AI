package nz.ac.auckland.se206.controllers;

import java.io.IOException;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.memory.MemoryController;
import nz.ac.auckland.se206.utils.SceneManager;
import nz.ac.auckland.se206.utils.TimableScene;

public class DefendantMemory extends MemoryController {

  @FXML
  private Rectangle rec1;
  @FXML
  private Rectangle rec2;
  @FXML
  private Rectangle rec3;
  @FXML
  private Rectangle rec4;
  @FXML
  private Arc progressArc;
  @FXML
  private Label pinLabel;

  // Login Sequence
  @FXML
  private AnchorPane initialPane;
  @FXML
  private AnchorPane keypadPane;
  @FXML
  private AnchorPane loginPane;
  @FXML
  private AnchorPane cctvPane;

  // Customer Details
  @FXML
  private StackPane customerDetailsPane;
  @FXML
  private Label customerIDLabel;
  @FXML
  private Label customerStatusLabel;
  @FXML
  private Label customerAgeLabel;
  @FXML
  private Label customerCriminalRecordLabel;

  private String pin = "_ _ _ _";
  private final String correctPin = "1 2 3 4";
  private static AudioClip keyPadAudioClip;
  private static boolean loginSequenceCompleted = false;
  private AnimationTimer progressArcAnimationTimer;

  static {
    var resource = DefendantMemory.class.getResource("/sounds/keypad.mp3");
    System.out.println("[DEBUG] keypad.mp3 resource: " + resource);
    if (resource != null) {
      keyPadAudioClip = new AudioClip(resource.toExternalForm());
      keyPadAudioClip.setVolume(1.0); // Set volume to max
      Platform.runLater(() -> keyPadAudioClip.play());
    } else {
      System.out.println("[ERROR] Could not find keypad.mp3 resource!");
    }
  }

  public DefendantMemory() {
    super("prompts/defendant.txt");
  }

  @Override
  @FXML
  protected void initialize() {
    super.initialize();

    loginPane.setVisible(true);
    initialPane.setVisible(true);
    keypadPane.setVisible(false);
    cctvPane.setVisible(false);

    if (loginSequenceCompleted) {
      loginPane.setVisible(false);
      cctvPane.setVisible(true);
      titleBlock.setVisible(true);
      descriptionLabel.setText("Click on the different characters to view their details.");
      titleLabel.setText("View Character Details");
    } else {
      titleBlock.setVisible(true);
      descriptionLabel.setText("Please login to access the CCTV footage.");
      titleLabel.setText("Login Required");
    }

    Platform.runLater(() -> {
      progressArc.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          progressArc.setLayoutX(event.getSceneX());
          progressArc.setLayoutY(event.getSceneY());
        }
      });
    });

    App.timer.addConsumer(getTimerConsumer());
    createTitleDisappearAnimation();

    this.roleOfCharacter = "Defendant";
  }

  @FXML
  private void keypadButtonPressed(ActionEvent event) {
    Button button = (Button) event.getSource();
    String buttonText = button.getText();

    // Update pin if there are still underscores left
    if (buttonText.matches("[0-9]")) {
      pin = pin.replaceFirst("_", buttonText);
      pinLabel.setText(pin);
      // Set the pitch of the audio clip based on the button pressed
      // For numbers 1-9, set pitch from 1.0 to 1.8
      double pitch = 1.0 + (Integer.parseInt(buttonText)) * 0.1;
      // keyPadAudioClip.setRate(pitch);
      keyPadAudioClip.play();
    }
  }

  /**
   * Handles the "Submit PIN" button press event to check the entered pin.
   * 
   * @param event The action event triggered by clicking the submit pin button
   */
  @FXML
  private void onSubmitPinPressed(ActionEvent event) {
    if (pin.equals(correctPin)) {
      // Mark login sequence as completed
      loginSequenceCompleted = true;
      // Stop the previous animation
      stopTitleDisappearAnimation();

      // Correct pin entered, proceed to next pane
      loginPane.setVisible(false);
      cctvPane.setVisible(true);
      titleBlock.setVisible(true);
      descriptionLabel.setText("Click on the different characters to view their details.");
      titleLabel.setText("View Character Details");
      createTitleDisappearAnimation();
    } else {
      // Incorrect pin, reset
      pin = "_ _ _ _";
      pinLabel.setText(pin);
    }
  }

  /**
   * Handles the "Clear" button press event to reset the pin.
   * 
   * @param event The action event triggered by clicking the clear button
   */
  @FXML
  private void onClearButtonPressed(ActionEvent event) {
    pin = "_ _ _ _";
    pinLabel.setText(pin);
  }

  /**
   * Handles the "Login" button press event to show the keypad pane.
   * 
   * @param event The action event triggered by clicking the login button
   */
  @FXML
  private void loginButtonPressed(ActionEvent event) {
    initialPane.setVisible(false);
    keypadPane.setVisible(true);
  }

  /**
   * Handles mouse press on a character in the CCTV pane.
   * Creates the progress bar for the "scanning" feature.
   * 
   * @param event The mouse event triggered by pressing a character
   */
  @FXML
  private void onTargetMousePressed(MouseEvent event) {
    progressArc.setVisible(true);
    progressArc.setLength(0);

    progressArcAnimationTimer = new AnimationTimer() {
      @Override
      public void handle(long now) {
        // Update the length of the arc to create a progress effect
        progressArc.setLength(progressArc.getLength() + 3);

        // Display person info after progress completes a full circle.
        if (progressArc.getLength() >= 370) {
          progressArc.setLength(0);
          progressArcAnimationTimer.stop();
          progressArc.setVisible(false);

          // Show customer details pane
          customerDetailsPane.setVisible(true);
          customerCriminalRecordLabel.setStyle(""); // Reset style
          Node sourceNode = (Node) event.getSource();
          customerDetailsPane.setLayoutX(sourceNode.getLayoutX());
          customerDetailsPane.setLayoutY(sourceNode.getLayoutY());

          // Check if the source of the event is one of the rectangles
          // Check by getting the ID of the source
          if (sourceNode.getId().equals(rec1.getId())) {
            customerIDLabel.setText("Customer 1");
            customerStatusLabel.setText("New Shopper");
            customerAgeLabel.setText("Age: 25");
            customerCriminalRecordLabel.setText("Shoplifting");
            // Highlight criminal record
            customerCriminalRecordLabel.setStyle("-fx-border-color: red; -fx-border-width: 2px;");

            // Send additional info to the AI
            Task<Void> sendAdditionalInfoTask = new Task<Void>() {
              @Override
              protected Void call() throws Exception {
                String output = sendGPTRequest(loadPrompt("prompts/defendant_additional.txt"));
                appendMessageToChat(roleOfCharacter, output);
                return null;
              }
            };

            // Use a thread to perform the task concurrently
            Thread additionalInfoThread = new Thread(sendAdditionalInfoTask);
            additionalInfoThread.setDaemon(true);
            additionalInfoThread.start();

          } else if (sourceNode.getId().equals(rec2.getId())) {
            customerIDLabel.setText("Customer 2");
            customerStatusLabel.setText("Returning Shopper");
            customerAgeLabel.setText("Age: 40");
            customerCriminalRecordLabel.setText("No Record");
          } else if (sourceNode.getId().equals(rec3.getId())) {
            customerIDLabel.setText("Customer 3");
            customerStatusLabel.setText("New Shopper");
            customerAgeLabel.setText("Age: 30");
            customerCriminalRecordLabel.setText("No Record");
          } else if (sourceNode.getId().equals(rec4.getId())) {
            customerIDLabel.setText("Customer 4");
            customerStatusLabel.setText("Loyal Shopper");
            customerAgeLabel.setText("Age: 35");
            customerCriminalRecordLabel.setText("No Record");
          }
        }
      };
    };
    progressArcAnimationTimer.start();
  }

  /**
   * Handles mouse release on a character in the CCTV pane.
   * Stops the progress bar for the "scanning" feature.
   * 
   * @param event The mouse event triggered by releasing a character
   */
  @FXML
  private void onTargetMouseReleased(MouseEvent event) {
    // Stop the progress arc animation and reset the arc
    progressArcAnimationTimer.stop();
    progressArc.setVisible(false);
    progressArc.setLength(0);
  }

  @FXML
  private void onCustomerDetailsButtonPressed(ActionEvent event) {
    customerDetailsPane.setVisible(false);
  }

}