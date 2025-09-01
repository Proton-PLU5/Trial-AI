package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import nz.ac.auckland.se206.App;

public class SceneManager {

  public enum AppUi {
    chat,
    defendant,
    room,
    witnessAi,
    witnessHuman,
    defendantMemory,
    humanMemory,
    aiMemory
  }

  private static Map<String, ChatController> chatControllers = new HashMap<>();
  private static Map<String, Parent> chatViews = new HashMap<>();

  private static HashMap<AppUi, Parent> sceneMap = new HashMap<>();

  public static void addUi(AppUi appUi, Parent uiRoot) {
    sceneMap.put(appUi, uiRoot);
  }

  public static Parent getUiRoot(AppUi appUi) {
    return sceneMap.get(appUi);
  }

  public static void initializeChats() throws IOException {
    String[] professions = {"defendant", "witnessHuman", "witnessAi"};

    for (String profession : professions) {
      FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/chat.fxml"));
      Parent chatContent = loader.load();

      ChatController chatController = loader.getController();
      chatController.setProfession(profession);

      chatControllers.put(profession, chatController);
      chatViews.put(profession, chatContent);
    }
  }

  public static ChatController getChatController(String profession) {
    return chatControllers.get(profession);
  }

  public static Parent getChatView(String profession) {
    return chatViews.get(profession);
  }

  public static boolean hasChatForProfession(String profession) {
    return chatControllers.containsKey(profession);
  }

  /**
   * Notifies other chat instances about a message from one chat.
   *
   * @param fromProfession the profession of the chat that sent the message
   * @param message the message content to share with other chats
   */
  public static void notifyOtherChats(String fromProfession, String message) {
    String[] allProfessions = {"defendant", "witnessHuman", "witnessAi"};

    for (String profession : allProfessions) {
      if (!profession.equals(fromProfession)) {
        ChatController controller = chatControllers.get(profession);
        if (controller != null) {
          controller.receiveContextUpdate(fromProfession, message);
        }
      }
    }
  }
}
