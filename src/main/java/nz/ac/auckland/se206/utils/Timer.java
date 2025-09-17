package nz.ac.auckland.se206.utils;

import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

/**
 * A custom timer class that is able to count to a specific
 * amount
 * without blocking the main thread of the application.
 */
public class Timer {
  private Thread timerThread;
  private Label timerLabel;
  private Consumer<Void> consumer;
  private boolean countDown;
  private int length;

  // The current count of the timer,
  // used to track how many seconds
  // have passed.
  private int count = 0;

  /**
   * The main constructor for the timer class.
   * 
   * @param length     The duration the timer is to count
   *                   for.
   * @param timerLabel The label to update during the
   *                   counting process.
   */
  public Timer(int length, Label timerLabel) {
    this.timerLabel = timerLabel;
    this.consumer = null;
    this.countDown = false;
    this.length = length;
  }

  public Timer setConsumer(Consumer<Void> consumer) {
    this.consumer = consumer;
    return this;
  }

  public Timer setCountDown(boolean countDown) {
    this.countDown = countDown;
    return this;
  }

  public void buildTimer() {
    Task<Void> task = this.createTimerTask(length,
        timerLabel, consumer, countDown);
    this.timerThread = new Thread(task);

    // Set the thread to be a daemon thread so that it does
    // not block the
    // application from exiting.
    // This is important because the timer normally runs in
    // the background.
    // If the thread is not a daemon thread, the application
    // will not exit until the
    // timer finishes.
    this.timerThread.setDaemon(true);
    timerThread.start();
  }

  public void stopTimer() {
    if (this.timerThread != null) {
      this.timerThread.interrupt();
      this.timerThread = null;
    }
  }

  public int getCount() {
    return this.count;
  }

  public void setCount(int count) {
    this.count = count;
    updateTimerLabel(count);
  }

  private void updateTimerLabel(int countToUse) {
    // Update the timer label.
    // We need to turn the seconds into minutes and seconds.
    double minutes = Math.floor(countToUse / 60.0);
    double seconds = countToUse % 60; // Remainder

    StringBuilder builder = new StringBuilder();
    builder.append(String.format("%02.0f", minutes));
    builder.append(" : ");
    builder.append(String.format("%02.0f", seconds));

    Platform.runLater(
        () -> timerLabel.setText(builder.toString()));
  }

  private Task<Void> createTimerTask(int length,
      Label timerLabel, Consumer<Void> consumer,
      boolean countDown) {
    Task<Void> timerTask = new Task<Void>() {
      private boolean shouldRunConsumer = true;

      private void count() {
        if (count < length) {
          count++;
          int reverseCount = length - count;
          int countToUse = countDown ? reverseCount : count;

          updateTimerLabel(countToUse);

          // Make the current thread sleep for 1 second.
          try {
            Thread.sleep(1000);
          } catch (InterruptedException exception) {
            exception.printStackTrace();
            // Break out of the loop if the timer runs out.
            shouldRunConsumer = false;
            return;
          }
          count();
        }
      }

      @Override
      protected Void call() throws Exception {
        count();
        System.out.println("Finished Counting!");
        ;
        Platform.runLater(() -> {
          // If a consumer has been provided, accept the
          // consumer.
          if (consumer != null && shouldRunConsumer) {
            consumer.accept(null);
          }
        });
        return null;
      }
    };

    return timerTask;
  }
}
