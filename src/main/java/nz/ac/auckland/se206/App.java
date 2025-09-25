package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nz.ac.auckland.se206.utils.SceneManager;
import nz.ac.auckland.se206.utils.Timer;

/**
 * This is the entry point of the JavaFX application. This class initializes and
 * runs the JavaFX
 * application.
 */
public class App extends Application {

  private static Scene scene;
  public static Stage primaryStage;
  public static LinkedHashMap<String, String> chatHistoryMap = new LinkedHashMap<>();
  public static final int TIMER_DURATION = 5 * 60; // 5 minutes in seconds
  public static Timer timer = new Timer(TIMER_DURATION); // 5 minutes

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
   * Loads the FXML file and returns the associated node. The method expects that
   * the file is
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
   * This method is invoked when the application starts. It loads and shows the
   * "room" scene.
   *
   * @param stage the primary stage of the application
   * @throws IOException if the "src/main/resources/fxml/room.fxml" file is not
   *                     found
   */
  @Override
  public void start(final Stage stage) throws IOException {

    primaryStage = stage;

    primaryStage.setWidth(1308);
    primaryStage.setHeight(736);
    SceneManager.switchScene(SceneManager.Scenes.start);
    SceneManager.setStyleSheet("/css/style.css");
    primaryStage.show();

    // Timer Setup
    timer.setCountDown(true);
    timer.buildTimer();

    primaryStage.requestFocus();
  }
}
