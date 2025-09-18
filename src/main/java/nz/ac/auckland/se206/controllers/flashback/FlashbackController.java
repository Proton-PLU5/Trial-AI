package nz.ac.auckland.se206.controllers.flashback;

import java.io.InputStream;
import java.util.List;
import java.util.Stack;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public abstract class FlashbackController {
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
  }

  /** Abstract method to be implemented by child classes to setup their specific images */
  protected abstract void setupImages();

  protected abstract void setupText();

  /** Abstract method to handle navigation to memory scene */
  protected abstract void handleMemoryButton();

  /** Add images to the stack from a list */
  protected void addImagesToStack(List<String> imagePaths) {
    imageStack.clear();
    for (String imagePath : imagePaths) {
      imageStack.push(imagePath);
    }
  }

  protected void addTextToStack(String Role, List<String> conversationText) {
    textStack.clear();
    conversationRoleLabel.setText(Role);
    for (String conversation : conversationText) {
      textStack.push(conversation);
    }
  }

  /** Navigate to next image */
  @FXML
  protected void nextDrawing() {
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
    if (!imageStack.isEmpty()) {
      String path = imageStack.pop();
      InputStream stream = getClass().getResourceAsStream(path);
      if (stream == null) {
        throw new IllegalArgumentException("Image not found: " + path);
      }
      Image image = new Image(stream);
      backgroundImage.setImage(image);
    }
  }

  @FXML
  protected void showCurrentText() {
    if (!textStack.isEmpty()) {
      String text = imageStack.pop();
      conversationTextLabel.setText(text);
    }
  }
}
