package com.github.amatheo.timelinefx.animation.timeline;

import java.util.Objects;

public final class TimelineProperty<T> {
  private final String id;

  private TimelineProperty(String id) {
    if (id.isBlank()) {
      throw new IllegalArgumentException("Timeline property id must not be blank");
    }
    this.id = id;
  }

  public static <T> TimelineProperty<T> of(String id) {
    return new TimelineProperty<>(id);
  }

  public String id() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TimelineProperty<?> property)) return false;
    return id.equals(property.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "TimelineProperty{" + id + '}';
  }
}
