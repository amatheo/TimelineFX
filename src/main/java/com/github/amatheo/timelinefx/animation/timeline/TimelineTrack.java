package com.github.amatheo.timelinefx.animation.timeline;

import com.github.amatheo.timelinefx.animation.Animatable;
import com.github.amatheo.timelinefx.animation.PropertyChannel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * A track maps timeline time to values by stacking property channels with explicit offsets.
 * The track looks up the segment whose start time is closest without exceeding the requested
 * time and delegates evaluation to the underlying {@link PropertyChannel}, letting its
 * extrapolation strategy drive behaviour before/after the segment.
 */
public final class TimelineTrack<T> implements Animatable<T> {
  private final List<Segment<T>> segments;

  private TimelineTrack(List<Segment<T>> segments) {
    if (segments.isEmpty()) {
      throw new IllegalArgumentException("timeline track requires at least one segment");
    }
    this.segments = List.copyOf(sorted(segments));
  }

  private static <T> List<Segment<T>> sorted(List<Segment<T>> input) {
    List<Segment<T>> sorted = new ArrayList<>(input);
    sorted.sort(Comparator.comparingDouble(Segment::startSeconds));
    return sorted;
  }

  public List<Segment<T>> segments() {
    return segments;
  }

  public double startSeconds() {
    return segments.getFirst().startSeconds();
  }

  public double endSeconds() {
    Segment<T> last = segments.getLast();
    return last.endSeconds();
  }

  public double lengthSeconds() {
    return endSeconds() - startSeconds();
  }

  @Override
  public T get(long tick, double tSeconds) {
    Segment<T> segment = resolveSegment(tSeconds);
    return segment.sample(tick, tSeconds);
  }

  private Segment<T> resolveSegment(double tSeconds) {
    Segment<T> candidate = segments.getFirst();
    for (Segment<T> segment : segments) {
      if (segment.startSeconds() > tSeconds) {
        break;
      }
      candidate = segment;
    }
    return candidate;
  }

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  public static final class Builder<T> {
    private final List<Segment<T>> segments = new ArrayList<>();

    public Builder<T> add(double startSeconds, PropertyChannel<T> channel) {
      segments.add(new Segment<>(startSeconds, channel));
      return this;
    }

    public TimelineTrack<T> build() {
      return new TimelineTrack<>(segments);
    }
  }

  public record Segment<T>(double startSeconds, PropertyChannel<T> channel) {
    public Segment {
      if (Double.isNaN(startSeconds) || Double.isInfinite(startSeconds)) {
        throw new IllegalArgumentException("startSeconds must be finite");
      }
      Objects.requireNonNull(channel, "channel");
    }

    public double endSeconds() {
      return startSeconds + channel.durationSeconds();
    }

    public T sample(long tick, double timelineSeconds) {
      double relativeTime = channel.startTimeSeconds() + (timelineSeconds - startSeconds);
      return channel.get(tick, relativeTime);
    }
  }
}
