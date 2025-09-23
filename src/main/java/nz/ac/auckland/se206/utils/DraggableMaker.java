package nz.ac.auckland.se206.utils;

import javafx.scene.Node;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.controllers.HumanMemory;

public class DraggableMaker {

  private double mouseAnchorX;
  private double mouseAnchorY;

  public void makeDraggable(Node node, Node hitbox) {
    
    node.setOnMousePressed(mouseEvent -> {
      mouseAnchorX = mouseEvent.getX();
      mouseAnchorY = mouseEvent.getY();
    });

    node.setOnMouseDragged(mouseEvent -> {
      node.setLayoutX(mouseEvent.getSceneX() - mouseAnchorX);
      node.setLayoutY(mouseEvent.getSceneY() - mouseAnchorY);
    });

    node.setOnMouseReleased(mouseEvent -> {
      if (node.getBoundsInParent().intersects(hitbox.getBoundsInParent())) {
        node.setVisible(false);
        // Mark item as collected once added to cart
        HumanMemory.itemCollected.put(node.getId(), true);
      }
    });
  }
}
