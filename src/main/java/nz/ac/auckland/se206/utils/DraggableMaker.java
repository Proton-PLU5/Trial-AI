package nz.ac.auckland.se206.utils;

import javafx.scene.Node;

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
      }
    });
  }
}
