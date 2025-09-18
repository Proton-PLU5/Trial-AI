package nz.ac.auckland.se206.utils;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.ChatController;

public class SceneManager {

  public enum Scenes {
    chat,
    defendant,
    room,
    witnessAi,
    witnessHuman,
    defendantMemory,
    humanMemory,
    aiMemory,
    verdict
  }

  public static void switchScene(Scenes scene) {
    try {
      Parent root = FXMLLoader.load(
          SceneManager.class.getResource("/fxml/" + scene.toString() + ".fxml"));
      App.primaryStage.setScene(new javafx.scene.Scene(root));
      SceneManager.setStyleSheet("/css/style.css");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void setStyleSheet(String path) {
    javafx.scene.Scene scene = App.primaryStage.getScene();
    scene.getStylesheets().add(SceneManager.class.getResource(path).toExternalForm());
  }
}
