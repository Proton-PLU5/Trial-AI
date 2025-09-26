package nz.ac.auckland.se206.utils;

public class Tuple<T, U> {
  public final T key;
  public final U value;

  public Tuple(T key, U value) {
    this.key = key;
    this.value = value;
  }

  public T getKey() {
    return key;
  }

  public U getValue() {
    return value;
  }
}
