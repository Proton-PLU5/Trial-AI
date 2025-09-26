package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.se206.utils.SceneManager;
import nz.ac.auckland.se206.utils.Timer;
import nz.ac.auckland.se206.utils.Tuple;

/**
 * This is the entry point of the JavaFX application. This class initializes and
 * runs the JavaFX
 * application.
 */
public class App extends Application {

  private static Scene scene;
  public static Stage primaryStage;
  public static List<Tuple<String, String>> chatHistoryMap = new ArrayList<Tuple<String, String>>();
  public static final int TIMER_DURATION = 5 * 60; // 5 minutes in seconds
  public static Timer timer; // 5 minutes

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
    primaryStage.setTitle("Trial AI");

    primaryStage.requestFocus();
  }

  public static String getChatHistoryString() {
    StringBuilder stringBuilder = new StringBuilder();
    // Load the chat history, iterate through the map and append to the string
    // builder
    for (Tuple<String, String> entry : chatHistoryMap) {
      String role = entry.getKey();
      String message = entry.getValue();
      stringBuilder.append(role.split("\0")[0]).append(":\n").append(message).append("\n");
    }
    return stringBuilder.toString();
  }

  /**
   * Creates a timer that counts down from TIMER_DURATION seconds and switches to
   * the final scene when
   * the time is up.
   */
  public static void createTimer() {
    timer = new Timer(TIMER_DURATION, new Consumer<Void>() {
      @Override
      public void accept(Void t) {
        // Switch to the final scene
        SceneManager.switchScene(SceneManager.Scenes.verdict);
        SceneManager.setStyleSheet("/css/style.css");
      }
    }); // 5 minutes
  }

  /**
   * Creates a scaling animation effect on a node with scale
   * up and down
   * transitions.
   *
   * @param node            The node to animate
   * @param durationSeconds The duration of each transition
   *                        in seconds
   * @param intensity       The maximum scale factor
   */
  public static void createScaleAnimation(Rectangle node,
      double durationSeconds, double intensity) {
    ScaleTransition scaleUpTransition = new ScaleTransition(
        Duration.seconds(durationSeconds), node);
    scaleUpTransition.setToX(intensity);
    scaleUpTransition.setToY(intensity);
    scaleUpTransition.setFromX(1);
    scaleUpTransition.setFromY(1);

    ScaleTransition scaleDownTransition = new ScaleTransition(
        Duration.seconds(durationSeconds), node);
    scaleDownTransition.setToX(1);
    scaleDownTransition.setToY(1);
    scaleDownTransition.setFromX(intensity);
    scaleDownTransition.setFromY(intensity);

    scaleUpTransition.play();

    scaleUpTransition.setOnFinished((event) -> {
      scaleDownTransition.play();
    });

    scaleDownTransition.setOnFinished((event) -> {
      scaleUpTransition.play();
    });

    // Add hover and exit effects
    node.setOnMouseEntered(e -> {
      // set fill to be orange tint
      node.setFill(Color.web("#2197ff", 0.5));
    });

    node.setOnMouseExited(e -> {
      // clear the fill
      node.setFill(Color.web("transparent", 0));
    });
  }

}
