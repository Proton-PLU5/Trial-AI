package nz.ac.auckland.se206.utils;

import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class LoadingAnimationCard extends Card {

  private Timeline loadingAnimation;

  public LoadingAnimationCard(String role, String message, double cardWidth) {
    super(role, message, cardWidth);
    this.loadingAnimation = new Timeline();
    setUpLoadingAnimation();
  }

  private ImageView loadImage(String path) {
    // Load image from resources folder
    // Update the height to 35 while preserving aspect ratio
    ImageView imageView = new ImageView(
        getClass().getResource("/images" + path).toExternalForm());
    imageView.setPreserveRatio(true);
    imageView.setFitHeight(35);
    return imageView;
  }

  private void setUpLoadingAnimation() {
    // Implementation for setting up the loading animation
    // Background rectangle.
    Rectangle background = new Rectangle(this.getCardWidth(), 100);
    background.setFill(javafx.scene.paint.Color.web("#dfdfdfff"));
    background.setArcWidth(35);
    background.setArcHeight(35);

    GridPane loadingPane = new GridPane();

    // Setup loading pane
    loadingPane.setHgap(10);
    loadingPane.setMaxWidth(this.getCardWidth() - 80);
    loadingPane.setAlignment(Pos.CENTER);

    loadingPane.setTranslateX(30);
    // Make the cells expand to fill the available space
    // for four columns and ensure they are evenly spaced.
    for (int i = 0; i < 4; i++) {
      ColumnConstraints column = new ColumnConstraints();
      column.setHgrow(Priority.ALWAYS);
      column.setPercentWidth(100.0 / 4);
      loadingPane.getColumnConstraints().add(column);
    }

    // Load images
    ImageView trolleyImage = loadImage("/trolley.png");
    ImageView orangeJuiceImage = loadImage("/orangeJuice.png");
    ImageView appleJuiceImage = loadImage("/appleJuice.png");
    ImageView candyBarImage = loadImage("/candyBar.png");

    double imageScale = 2.5 - 0.75;
    double trolleyScale = 3.5 - 3.5 * 0.3;
    trolleyImage.setScaleX(trolleyScale);
    trolleyImage.setScaleY(trolleyScale);
    orangeJuiceImage.setScaleX(imageScale);
    orangeJuiceImage.setScaleY(imageScale);
    appleJuiceImage.setScaleX(imageScale);
    appleJuiceImage.setScaleY(imageScale);
    candyBarImage.setScaleX(imageScale);
    candyBarImage.setScaleY(imageScale);

    // Add images to loading pane
    loadingPane.add(trolleyImage, 0, 0);
    loadingPane.add(orangeJuiceImage, 1, 0);
    loadingPane.add(appleJuiceImage, 2, 0);
    loadingPane.add(candyBarImage, 3, 0);

    loadingAnimation.setCycleCount(Timeline.INDEFINITE);
    double duration = 0.25; // duration per keyframe cycle in seconds.

    // Animation keyframes
    // Move the imageviews to the next column.
    // By clearing and re-adding them, we can create a looping effect.
    // Helper to apply a new left-to-right order without duplicating code.
    Consumer<ImageView[]> applyOrder = order -> {
      loadingPane.getChildren().clear();
      for (int i = 0; i < order.length; i++) {
        loadingPane.add(order[i], i, 0);
      }
    };

    KeyFrame kf1 = new KeyFrame(
        Duration.seconds(duration),
        e -> applyOrder.accept(
            new ImageView[] { candyBarImage, trolleyImage,
                orangeJuiceImage, appleJuiceImage }));

    KeyFrame kf2 = new KeyFrame(
        Duration.seconds(2 * duration),
        e -> applyOrder.accept(
            new ImageView[] { appleJuiceImage, candyBarImage,
                trolleyImage, orangeJuiceImage }));

    KeyFrame kf3 = new KeyFrame(
        Duration.seconds(3 * duration),
        e -> applyOrder.accept(
            new ImageView[] { orangeJuiceImage, appleJuiceImage,
                candyBarImage, trolleyImage }));

    KeyFrame kf4 = new KeyFrame(
        Duration.seconds(4 * duration),
        e -> applyOrder.accept(
            new ImageView[] { trolleyImage, orangeJuiceImage,
                appleJuiceImage, candyBarImage }));

    loadingAnimation.getKeyFrames().addAll(kf1, kf2, kf3, kf4);

    loadingAnimation.play();
    this.getChildren().add(background);
    this.getChildren().add(loadingPane);
  }

  public void finishLoadingAnimation(String role, String message, double cardWidth) {
    loadingAnimation.stop();
    this.getChildren().clear();
    this.setRole(role);
    this.setMessage(message);
    this.setCardWidth(cardWidth);
    setUpCard();
  }
}
