package nz.ac.auckland.se206.utils;

import java.util.ArrayList;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;

/**
 * A custom timer class that is able to count to a specific
 * amount
 * without blocking the main thread of the application.
 */
public class Timer {
  private Thread timerThread;

  // A list of consumers which will be used to update individual timer labels.
  private ArrayList<Consumer<String>> consumers;
  private Consumer<Void> finalConsumer;
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
  public Timer(int length, Consumer<Void> finalConsumer) {
    this.consumers = new ArrayList<>();
    this.countDown = false;
    this.length = length;
    this.finalConsumer = finalConsumer;
  }

  /**
   * Add a consumer to be executed when the timer to update timer labels.
   * 
   * @param consumer
   * @return The current timer instance.
   */
  public Timer addConsumer(Consumer<String> consumer) {
    this.consumers.add(consumer);
    updateTimerLabel(App.TIMER_DURATION - count);
    return this;
  }

  /**
   * Set whether the timer counts down or up.
   * 
   * @param countDown
   * @return The current timer instance.
   */
  public Timer setCountDown(boolean countDown) {
    this.countDown = countDown;
    return this;
  }

  /**
   * Build and start the timer.
   */
  public void buildTimer() {
    // This method builds the timer
    Task<Void> task = this.createTimerTask(length, countDown);
    this.timerThread = new Thread(task);

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
        () -> {
          for (Consumer<String> consumer : consumers) {
            // Check if the consumer is still valid
            if (consumer == null) {
              consumers.remove(consumer);
              continue;
            }
            consumer.accept(builder.toString());
          }
        });
  }

  private Task<Void> createTimerTask(int length,
      boolean countDown) {

    Task<Void> timerTask = new Task<Void>() {

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
            // If the thread is interrupted, we stop the timer.
            return;
          }
          count();
        }
      }

      @Override
      protected Void call() throws Exception {
        count();
        System.out.println("Finished Counting!");

        Platform.runLater(() -> {
          finalConsumer.accept(null);
        });
        return null;
      }
    };

    return timerTask;
  }
}
