package nz.ac.auckland.se206.utils;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.ChatController;

public class SceneManager {

  public enum Scenes {
    chat,
    room,
    defendantFlashback,
    humanFlashback,
    aiFlashback,
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
      Platform.runLater(() -> App.primaryStage.sizeToScene());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void setStyleSheet(String path) {
    javafx.scene.Scene scene = App.primaryStage.getScene();
    scene.getStylesheets().add(SceneManager.class.getResource(path).toExternalForm());
  }
}
