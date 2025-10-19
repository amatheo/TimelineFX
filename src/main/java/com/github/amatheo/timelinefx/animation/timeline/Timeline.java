package com.github.amatheo.timelinefx.animation.timeline;

import com.github.amatheo.timelinefx.animation.Animatable;
import com.github.amatheo.timelinefx.animation.timeline.builder.TimelineBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Timeline implements Animatable<TimelineSnapshot> {
  private final List<TrackEntry<?>> entries;
  private final double durationSeconds;

  private Timeline(List<TrackEntry<?>> entries) {
    if (entries.isEmpty()) {
      throw new IllegalArgumentException("timeline requires at least one track");
    }
    this.entries = List.copyOf(entries);
    this.durationSeconds = computeDuration(entries);
  }

  /**
   * Internal factory method for use by TimelineBuilder.
   */
  public static Timeline fromTracks(List<TrackEntry<?>> entries) {
    return new Timeline(entries);
  }

  private static double computeDuration(List<TrackEntry<?>> entries) {
    double max = 0.0;
    for (TrackEntry<?> entry : entries) {
      double end = entry.track.endSeconds();
      if (end > max) {
        max = end;
      }
    }
    return max;
  }

  public double durationSeconds() {
    return durationSeconds;
  }

  public <T> TimelineTrack<T> track(TimelineProperty<T> property) {
    for (TrackEntry<?> entry : entries) {
      if (entry.property.equals(property)) {
        @SuppressWarnings("unchecked")
        TimelineTrack<T> track = (TimelineTrack<T>) entry.track;
        return track;
      }
    }
    return null;
  }

  @Override
  public TimelineSnapshot get(long tick, double tSeconds) {
    Map<TimelineProperty<?>, Object> values = new HashMap<>();
    for (TrackEntry<?> entry : entries) {
      sampleEntry(entry, tick, tSeconds, values);
    }
    return TimelineSnapshot.of(values, tSeconds);
  }

  private static <T> void sampleEntry(TrackEntry<T> entry, long tick, double tSeconds,
                                      Map<TimelineProperty<?>, Object> values) {
    T value = entry.track.get(tick, tSeconds);
    values.put(entry.property, value);
  }

  public static TimelineBuilder builder() {
    return new TimelineBuilder();
  }

  /**
   * Internal record representing a property-track pair.
   * Package-private for use by TimelineBuilder.
   */
  public record TrackEntry<T>(TimelineProperty<T> property, TimelineTrack<T> track) {}
}
