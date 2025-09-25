package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
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

  @FXML
  private Button roomBtn;
  @FXML
  private Button chatBtn;
  @FXML
  private Label timerLabel;
  @FXML
  private Pane chatPanel;

  // Title
  @FXML
  private AnchorPane titleBlock;
  @FXML
  private Label titleLabel;
  @FXML
  private Label descriptionLabel;

  // Shopping List
  @FXML
  private ImageView markerLine1;
  @FXML
  private ImageView markerLine2;
  @FXML
  private ImageView markerLine3;
  @FXML
  private ImageView markerLine4;

  // Main Aisle
  @FXML
  private AnchorPane mainAislePane;
  @FXML
  private Rectangle aisle1Rectangle;
  @FXML
  private Rectangle aisle2Rectangle;

  // Misc
  @FXML
  private Polygon shoppingCartHitbox;
  @FXML
  private Rectangle purseHitbox;
  @FXML
  private Button backToAislesButton;

  // Aisle 1
  @FXML
  private AnchorPane aisle1Pane;
  @FXML
  private ImageView aisle1Item;

  // Aisle 2
  @FXML
  private AnchorPane aisle2Pane;
  @FXML
  private ImageView aisle2Item1;
  @FXML
  private ImageView aisle2Item2;

  // Aisle 3
  @FXML
  private AnchorPane aisle3Pane;
  @FXML
  private ImageView aisle3Item;

  public static Map<String, Boolean> itemCollected = new HashMap<>();
  public static Map<String, ImageView> itemToLabel = new HashMap<>();

  DraggableMaker draggableMaker = new DraggableMaker();

  private ArrayList<ImageView> aisleItems = new ArrayList<ImageView>();
  private ArrayList<ImageView> itemMarkers = new ArrayList<ImageView>();

  public static boolean hasChattedWithHuman;
  public static boolean isFirstTimeInteract = true;

  public HumanMemory() {
    super("prompts/witnessHuman.txt");
    itemToLabel.put("aisle1Item", markerLine1);
    itemToLabel.put("aisle2Item1", markerLine2);
    itemToLabel.put("aisle2Item2", markerLine3);
    itemToLabel.put("aisle3Item", markerLine4);
  }

  @Override
  @FXML
  protected void initialize() {
    App.timer.addConsumer(getTimerConsumer());
    createTitleDisappearAnimation();
    super.initialize();

    this.roleOfCharacter = "Maria Shader";

    // Add items to arraylists
    aisleItems.add(aisle1Item);
    aisleItems.add(aisle2Item1);
    aisleItems.add(aisle2Item2);
    aisleItems.add(aisle3Item);
    itemMarkers.add(markerLine1);
    itemMarkers.add(markerLine2);
    itemMarkers.add(markerLine3);
    itemMarkers.add(markerLine4);

    // Initial UI setup
    for (ImageView item : aisleItems) {
      item.setVisible(true);
    }

    // Load shopping list items, if collected, then make invisible
    for (int i = 0; i < aisleItems.size(); i++) {
      if (itemCollected.getOrDefault(aisleItems.get(i).getId(), false)) {
        aisleItems.get(i).setVisible(false);
        itemMarkers.get(i).setVisible(true);
      } else {
        aisleItems.get(i).setVisible(true);
        itemMarkers.get(i).setVisible(false);
      }
      setupDraggableItem(aisleItems.get(i), aisleItems.get(i).getId());
    }
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
    aisle2Pane.setVisible(false);
    aisle3Pane.setVisible(false);
    shoppingCartHitbox.setVisible(false);
    purseHitbox.setVisible(false);
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

  @FXML
  private void handleAisle2RectangleClicked(MouseEvent event) throws IOException {
    mainAislePane.setVisible(false);
    aisle2Pane.setVisible(true);
    shoppingCartHitbox.setVisible(true);
    backToAislesButton.setVisible(true);
    // checkIfItemHasBeenCollected();
  }

  @FXML
  private void handleAisle3RectangleClicked(MouseEvent event) throws IOException {
    mainAislePane.setVisible(false);
    aisle3Pane.setVisible(true);
    purseHitbox.setVisible(true);
    backToAislesButton.setVisible(true);
    // checkIfItemHasBeenCollected();
  }

  // @FXML
  // private void checkIfItemHasBeenCollected() {
  // // Load shopping list items, if collected, then make invisible
  // if (itemCollected.getOrDefault(aisle1Item.getId(), true)) {
  // aisle1Item.setVisible(true);
  // } else {
  // aisle1Item.setVisible(false);
  // }
  // }

  @FXML
  private void setupDraggableItem(Node item, String itemName) {
    final Node hitbox;
    if (!"aisle3Item".equals(itemName)) {
      hitbox = shoppingCartHitbox;
    } else {
      hitbox = purseHitbox;
    }
    draggableMaker.makeDraggable(item, hitbox);
    // Check if released on shopping cart hitbox
    item.setOnMouseReleased(event -> {
      if (item.getBoundsInParent().intersects(hitbox.getBoundsInParent())) {
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
    itemCollected.put(itemName, true);
    switch (itemName) {
      case "aisle1Item":
        System.out.println("Item 1 in cart!"); // Debugging
        // Check off the shopping list
        markerLine1.setVisible(true);
        break;
      case "aisle2Item1":
        System.out.println("Item 2 in cart!"); // Debugging
        // Check off the shopping list
        markerLine2.setVisible(true);
        break;
      case "aisle2Item2":
        System.out.println("Item 3 in cart!"); // Debugging
        // Check off the shopping list
        markerLine3.setVisible(true);
        break;
      case "aisle3Item":
        System.out.println("Item 4 in cart!"); // Debugging
        // Check off the shopping list
        markerLine4.setVisible(true);
        interactableDone();
        break;
      default:
        // placeholder
    }
  }

  @Override
  protected void markAsChatted() {
    hasChattedWithHuman = true;
  }

  // Add interaction event when shoplifting info is revealed
  @FXML
  private void interactableDone() {
    if (isFirstTimeInteract) {
      Task<Void> interactableDoneTask = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
          String output = sendGPTRequest(loadPrompt("prompts/humanInteractableDone.txt"));

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
}
