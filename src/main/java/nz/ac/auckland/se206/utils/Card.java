package nz.ac.auckland.se206.utils;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Card extends StackPane {
  private String role;
  private String message;
  private double cardWidth;

  public Card(String role, String message, double cardWidth) {
    this.role = role;
    this.message = message;
    this.cardWidth = cardWidth;
  }

  /**
   * Creates and returns a Group containing the text elements of the card.
   * 
   * @return A Group containing the text elements of the card.
   */
  public TextFlow getTextGroup() {
    // Create Text nodes for role (bold) and message (normal)
    Text roleText = new Text(role + ":\n");
    roleText.setFill(Color.WHITE);
    roleText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

    Text messageText = new Text(message + "\n");
    messageText.setFill(Color.WHITE);
    messageText.setStyle("-fx-font-size: 16px;");

    TextFlow textFlow = new TextFlow(roleText, messageText);
    textFlow.setMaxWidth(cardWidth - 40);
    textFlow.setStyle("-fx-padding: 15px;");
    return textFlow;
  }

  /**
   * Sets up the card content elements.
   */
  public void setUpCard() {
    TextFlow textFlow = getTextGroup();

    // Implementation for setting up the card content elements
    Paint colourToUse = Color.web("#00865d");
    if (role.equals("User")) {
      // To differentiate user messages for different characters
      colourToUse = Color.web("#5599d9");
    }

    // Create a rectangle background which scales to the height of the textFlow
    Rectangle background = new Rectangle();
    background.setArcWidth(35);
    background.setArcHeight(35);
    background.setFill(colourToUse);
    background.setWidth(textFlow.getMaxWidth() + 20);
    background.setHeight(Region.USE_PREF_SIZE);
    background.heightProperty().bind(textFlow.heightProperty().add(-15));

    this.getChildren().addAll(background, textFlow);
  }

  public String getRole() {
    return role;
  }

  public String getMessage() {
    return message;
  }

  public double getCardWidth() {
    return cardWidth;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setCardWidth(double cardWidth) {
    this.cardWidth = cardWidth;
  }
}
