package com.github.amatheo.timelinefx.animation.value;

import com.github.amatheo.timelinefx.animation.timeline.TimelineSnapshot;

/**
 * Provide a value of type T, possibly based on the current timeline snapshot.
 * This is a functional interface, allowing the use of lambda expressions or method references.
 * @param <T>
 */
@FunctionalInterface
public interface ValueProvider<T> {
  /**
   * Get the value of type T, possibly based on the provided timeline snapshot.
   * @param snapshot the current timeline snapshot.
   * @return the value of type T.
   */
  T get(TimelineSnapshot snapshot);
}