package nz.ac.auckland.se206.controllers.flashback;

import java.io.InputStream;
import java.util.Stack;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.utils.TimableScene;

public abstract class FlashbackController implements TimableScene {
  @FXML protected ImageView backgroundImage;

  @FXML protected Label titleLabel;
  @FXML protected Label descriptionLabel;

  /// Conversation Elements
  // Conversation Pane (The parent object of all the conversation elements)
  @FXML protected AnchorPane conversationPane;
  @FXML protected Label conversationRoleLabel;
  @FXML protected Label conversationTextLabel;
  @FXML protected Button conversationNextButton;

  // Navigation
  @FXML protected Button nextButton;
  @FXML protected Button memoryButton;

  // Timer
  @FXML protected Label timerLabel;

  // Image Drawings Elements
  protected Stack<String> imageStack;

  // Text elements
  protected Stack<String> textStack;

  /**
   * Method executed during the initialization of the flashback scene. Update UI elements with the
   * current flashback data here.
   */
  protected void initialize() {
    imageStack = new Stack<>();
    textStack = new Stack<>();
    memoryButton.setVisible(false);
    setupImages();
    setupText();
    showCurrentImage();
    showCurrentText();

    // Add ourselves to the timer service
    App.timer.addConsumer(getTimerConsumer());
  }

  /** Abstract method to be implemented by child classes to setup their specific images */
  protected abstract void setupImages();

  protected abstract void setupText();

  /** Abstract method to handle navigation to memory scene */
  protected abstract void handleMemoryButton();

  /**
   * Add image path to the image stack
   *
   * @param imagePath The path of the image to be added
   */
  protected void addImage(String imagePath) {
    imageStack.push(imagePath);
  }

  protected void onBackButtonPressed() {
    // Default implementation does nothing
  }

  /**
   * Add text to the text stack
   *
   * @param text The text to be added
   */
  protected void addText(String text) {
    textStack.push(text);
  }

  protected void setRole(String role) {
    conversationRoleLabel.setText(role);
  }

  /** Navigate to next image */
  @FXML
  protected void nextDrawing() {
    //This method makes it so that the flashback image goes to the next one in a slide show sort of thing
    if (imageStack.size() == 1) {
      showCurrentImage();
      showCurrentText();
      nextButton.setVisible(false);
      memoryButton.setVisible(true);
    } else if (!imageStack.isEmpty()) {
      showCurrentImage();
      showCurrentText();
    }
  }

  /** Show the currnt image at the top of the stack */
  @FXML
  protected void showCurrentImage() {
    String path = imageStack.pop();
    InputStream stream = getClass().getResourceAsStream(path);
    Image image = new Image(stream);
    backgroundImage.setImage(image);
  }

  @FXML
  protected void showCurrentText() {
    String text = textStack.pop();
    conversationTextLabel.setText(text);
  }

  @Override
  public Label getTimerLabel() {
    return timerLabel;
  }
}
