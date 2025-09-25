package nz.ac.auckland.se206.utils;

import javafx.scene.Node;

public class DraggableMaker {

  private double mouseAnchorX;
  private double mouseAnchorY;

  public void makeDraggable(Node node, Node hitbox) {

    // This makes it so that the items are dragable in the human memory

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
      }
    });
  }
}
