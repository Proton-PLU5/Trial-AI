package nz.ac.auckland.se206.utils;

import javafx.scene.Node;

public class DraggableMaker {

  private double mouseAnchorX;
  private double mouseAnchorY;

  public void makeDraggable(Node node) {
    
    node.setOnMousePressed(mouseEvent -> {
      mouseAnchorX = mouseEvent.getX();
      mouseAnchorY = mouseEvent.getY();
    });

    node.setOnMouseDragged(mouseEvent -> {
      node.setLayoutX(mouseEvent.getX() - mouseAnchorX);
      node.setLayoutX(mouseEvent.getY() - mouseAnchorY);
    });
  }
}
