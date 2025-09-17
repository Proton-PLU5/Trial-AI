package nz.ac.auckland.se206;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import nz.ac.auckland.se206.controllers.SceneManager;
import nz.ac.auckland.se206.controllers.SceneManager.AppUi;
import nz.ac.auckland.se206.utils.Timer;

/**
 * This is the entry point of the JavaFX application. This class initializes and runs the JavaFX
 * application.
 */
public class App extends Application {

  private static Scene scene;
  public static Stage primaryStage;
  public static StringBuilder chatHistory = new StringBuilder();
  public static Timer timer = new Timer(5*60); // 5 minutes

  /**
   * The main method that launches the JavaFX application.
   *
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    launch();
  }

  /**
   * Sets the root of the scene to the specified FXML file.
   *
   * @param fxml the name of the FXML file (without extension)
   * @throws IOException if the FXML file is not found
   */
  public static void setRoot(String fxml) throws IOException {
    scene.setRoot(loadFxml(fxml));
  }

  /**
   * Loads the FXML file and returns the associated node. The method expects that the file is
   * located in "src/main/resources/fxml".
   *
   * @param fxml the name of the FXML file (without extension)
   * @return the root node of the FXML file
   * @throws IOException if the FXML file is not found
   */
  public static Parent loadFxml(final String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  /**
   * Opens the chat view and sets the profession in the chat controller.
   *
   * @param event the mouse event that triggered the method
   * @param profession the profession to set in the chat controller
   * @throws IOException if the FXML file is not found
   */
  public static void openChat(MouseEvent event, String profession) throws IOException {
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Parent currentRoot = stage.getScene().getRoot();

    if (currentRoot.lookup("#chatPanel") != null) {
      return; // Chat already open
    }

    Parent chatRoot = SceneManager.getChatView(profession);

    if (currentRoot instanceof Pane) {
      Pane parentPane = (Pane) currentRoot;

      double scale = 0.4;
      chatRoot.setScaleX(scale);
      chatRoot.setScaleY(scale);

      Scene scene = stage.getScene();
      double chatWidth = 789 * scale;

      // Position in top-right corner of the scene
      chatRoot.setLayoutX(scene.getWidth() - chatWidth - 20);
      chatRoot.setLayoutY(20);

      chatRoot.setStyle(
          chatRoot.getStyle()
              + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0);");

      parentPane.getChildren().add(chatRoot);
    }
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "room" scene.
   *
   * @param stage the primary stage of the application
   * @throws IOException if the "src/main/resources/fxml/room.fxml" file is not found
   */
  @Override
  public void start(final Stage stage) throws IOException {
    SceneManager.addUi(AppUi.room, loadFxml("room"));
    SceneManager.addUi(AppUi.defendant, loadFxml("defendant"));
    SceneManager.addUi(AppUi.witnessAi, loadFxml("witnessAi"));
    SceneManager.addUi(AppUi.witnessHuman, loadFxml("witnessHuman"));
    SceneManager.addUi(AppUi.defendantMemory, loadFxml("defendantMemory"));
    SceneManager.addUi(AppUi.humanMemory, loadFxml("humanMemory"));
    SceneManager.addUi(AppUi.aiMemory, loadFxml("aiMemory"));
    Parent root = SceneManager.getUiRoot(AppUi.room);

    primaryStage = stage;

    SceneManager.initializeChats();

    primaryStage.setWidth(1308);
    primaryStage.setHeight(736);
    scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();

    // Timer Setup
    timer.setCountDown(true);
    timer.addConsumer(timeString -> scene.lookup("#timerLabel"));

    root.requestFocus();
  }
}
