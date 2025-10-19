package com.github.amatheo.timelinefx.animation.timeline;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class TimelineSnapshot {
  private final Map<TimelineProperty<?>, Object> values;
  private final double timeSeconds;

  private TimelineSnapshot(Map<TimelineProperty<?>, Object> values, double timeSeconds) {
    this.values = Collections.unmodifiableMap(new HashMap<>(values));
    this.timeSeconds = timeSeconds;
  }

  public double timeSeconds() {
    return timeSeconds;
  }

  public <T> T get(TimelineProperty<T> property) {
    Objects.requireNonNull(property, "property");
    @SuppressWarnings("unchecked")
    T value = (T) values.get(property);
    return value;
  }

  public <T> T getOrDefault(TimelineProperty<T> property, T fallback) {
    T value = get(property);
    return value != null ? value : fallback;
  }

  static TimelineSnapshot of(Map<TimelineProperty<?>, Object> values, double timeSeconds) {
    return new TimelineSnapshot(values, timeSeconds);
  }
}
