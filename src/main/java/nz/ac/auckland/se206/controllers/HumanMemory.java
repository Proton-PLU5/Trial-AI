package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.se206.utils.SceneManager;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.memory.MemoryController;
import nz.ac.auckland.se206.utils.DraggableMaker;

public class HumanMemory extends MemoryController {

  @FXML private Button roomBtn;
  @FXML private Button chatBtn;
  @FXML private Label timerLabel;
  @FXML private Pane chatPanel;

  // Title
  @FXML private AnchorPane titleBlock;
  @FXML private Label titleLabel;
  @FXML private Label descriptionLabel;

  // Shopping List
  @FXML private Label shoppingListItem1Label;
  @FXML private Label shoppingListItem2Label;
  @FXML private Label shoppingListItem3Label;
  @FXML private Label shoppingListItem4Label;

  // Main Aisle
  @FXML private AnchorPane mainAislePane;
  @FXML private Rectangle aisle1Rectangle;

  // Misc
  @FXML private Polygon shoppingCartHitbox;
  @FXML private Button backToAislesButton;

  // Aisle 1
  @FXML private AnchorPane aisle1Pane;
  @FXML private ImageView aisle1Item;

  public static Map<String, Boolean> itemCollected = new HashMap<>();
  public static Map<String, Label> itemToLabel = new HashMap<>();

  DraggableMaker draggableMaker = new DraggableMaker();

  public HumanMemory() {
    super("prompts/witnessHuman.txt");
    itemToLabel.put("aisle1Item", shoppingListItem1Label);
  }

  @Override
  @FXML
  protected void initialize() {
    App.timer.addConsumer(getTimerConsumer());
    createTitleDisappearAnimation();
    super.initialize();

    // Load shopping list items, if collected, then make invisible
    // if (itemCollected.getOrDefault(aisle1Item.getId(), true)) {
    //   aisle1Item.setVisible(false);
    // }

    setupDraggableItem(aisle1Item, "aisle1Item");
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
  
  @FXML
  private void onBackToAislesButtonPressed() throws IOException {
    mainAislePane.setVisible(true);
    aisle1Pane.setVisible(false);
    shoppingCartHitbox.setVisible(false);
    backToAislesButton.setVisible(false);
  }

  @FXML
  private void handleAisle1RectangleClicked(MouseEvent event) throws IOException {
    mainAislePane.setVisible(false);
    aisle1Pane.setVisible(true);
    shoppingCartHitbox.setVisible(true);
    backToAislesButton.setVisible(true);
    // checkIfItemHasBeenCollected();
  }

  // @FXML
  // private void checkIfItemHasBeenCollected() {
  //   // Load shopping list items, if collected, then make invisible
  //   if (itemCollected.getOrDefault(aisle1Item.getId(), true)) {
  //     aisle1Item.setVisible(true);
  //   } else {
  //     aisle1Item.setVisible(false);
  //   }
  // }

  @FXML
  private void setupDraggableItem(Node item, String itemName) {
      draggableMaker.makeDraggable(item, shoppingCartHitbox);
      itemCollected.put(item.getId(), false);
      // Check if released on shopping cart hitbox
      item.setOnMouseReleased(event -> {
          if (item.getBoundsInParent().intersects(shoppingCartHitbox.getBoundsInParent())) {
              handleItemInCart(itemName);
              // Hide the item once in cart
              item.setVisible(false);
          } else {
              return;
          }
      });
  }

  @FXML
  private void handleItemInCart(String itemName) {
    switch(itemName) {
      case "aisle1Item":
        System.out.println("Item 1 in cart!"); // Debugging
        // Check off the shopping list
        break;
      default:
        // placeholder
    }
  }
}
