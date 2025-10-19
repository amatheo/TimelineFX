package com.github.amatheo.timelinefx.animation.timeline;

import java.util.Objects;

public final class TimelinePlayback {
  private final Timeline timeline;
  private final double speed;
  private final double startDelaySeconds;
  private final boolean infiniteLoops;
  private final int totalCycles; // includes the first playthrough
  private final double loopDelaySeconds;
  private final double timelineDuration;

  private long startedTick = -1L;
  private boolean finished = false;
  private TimelineSnapshot lastSnapshot;

  private TimelinePlayback(Builder builder) {
    this.timeline = Objects.requireNonNull(builder.timeline, "timeline");
    this.speed = builder.speed;
    this.startDelaySeconds = builder.startDelaySeconds;
    this.infiniteLoops = builder.infiniteLoops;
    this.totalCycles = builder.infiniteLoops ? -1 : Math.max(1, builder.extraLoops + 1);
    this.loopDelaySeconds = builder.loopDelaySeconds;
    this.timelineDuration = Math.max(0.0, timeline.durationSeconds());
  }

  public static Builder builder(Timeline timeline) {
    return new Builder(timeline);
  }

  public void start(long startTick) {
    this.startedTick = startTick;
    this.finished = false;
    this.lastSnapshot = null;
  }

  public TimelineSnapshot sample(long nowTick, double tickToSeconds) {
    if (startedTick < 0) {
      return lastSnapshot;
    }

    double elapsed = (nowTick - startedTick) * tickToSeconds;
    double effective = elapsed - startDelaySeconds;
    if (effective < 0.0) {
      return lastSnapshot;
    }

    double scaledTime = effective * speed;
    TimelineSample timelineSample = mapTime(scaledTime);
    if (timelineSample.finished) {
      finished = true;
    }

    TimelineSnapshot snapshot = timeline.get(nowTick, timelineSample.timelineSeconds);
    lastSnapshot = snapshot;
    return snapshot;
  }

  public TimelineSnapshot lastSnapshot() {
    return lastSnapshot;
  }

  public boolean isFinished() {
    return finished;
  }

  private TimelineSample mapTime(double timeSeconds) {
    if (timelineDuration <= 0.0) {
      if (infiniteLoops) {
        return new TimelineSample(0.0, false);
      }
      double loopDelay = Math.max(0.0, loopDelaySeconds);
      double totalDuration = loopDelay * Math.max(0, totalCycles - 1);
      boolean done = timeSeconds >= totalDuration;
      return new TimelineSample(0.0, done);
    }

    if (infiniteLoops) {
      double cycleSpan = timelineDuration + Math.max(0.0, loopDelaySeconds);
      if (cycleSpan <= 0.0) {
        return new TimelineSample(0.0, false);
      }
      double withinCycle = wrap(timeSeconds, cycleSpan);
      if (withinCycle >= timelineDuration) {
        return new TimelineSample(timelineDuration, false);
      }
      return new TimelineSample(withinCycle, false);
    }

    double loopDelay = Math.max(0.0, loopDelaySeconds);
    int cycles = totalCycles;
    double totalDuration = timelineDuration * cycles + loopDelay * (cycles - 1);
    if (timeSeconds >= totalDuration) {
      return new TimelineSample(timelineDuration, true);
    }

    double cycleSpan = timelineDuration + loopDelay;
    if (cycleSpan <= 0.0) {
      return new TimelineSample(timelineDuration, true);
    }
    int cycleIndex = (int) Math.floor(timeSeconds / cycleSpan);
    double withinCycle = timeSeconds - (cycleIndex * cycleSpan);
    if (withinCycle >= timelineDuration) {
      return new TimelineSample(timelineDuration, false);
    }
    return new TimelineSample(withinCycle, false);
  }

  private static double wrap(double value, double span) {
    if (span <= 0.0) {
      return 0.0;
    }
    double result = value % span;
    if (result < 0.0) {
      result += span;
    }
    return result;
  }

  private record TimelineSample(double timelineSeconds, boolean finished) {
  }

  public static final class Builder {
    private final Timeline timeline;
    private double speed = 1.0;
    private double startDelaySeconds = 0.0;
    private int extraLoops = 0;
    private boolean infiniteLoops = false;
    private double loopDelaySeconds = 0.0;

    private Builder(Timeline timeline) {
      this.timeline = Objects.requireNonNull(timeline, "timeline");
    }

    public Builder speed(double speed) {
      if (speed <= 0.0) {
        throw new IllegalArgumentException("speed must be > 0");
      }
      this.speed = speed;
      return this;
    }

    public Builder delaySeconds(double delaySeconds) {
      if (delaySeconds < 0.0) {
        throw new IllegalArgumentException("delaySeconds must be >= 0");
      }
      this.startDelaySeconds = delaySeconds;
      return this;
    }

    public Builder loopCount(int additionalLoops) {
      if (additionalLoops < 0) {
        throw new IllegalArgumentException("additionalLoops must be >= 0");
      }
      this.extraLoops = additionalLoops;
      this.infiniteLoops = false;
      return this;
    }

    public Builder loopInfinite() {
      this.infiniteLoops = true;
      return this;
    }

    public Builder loopDelaySeconds(double loopDelaySeconds) {
      if (loopDelaySeconds < 0.0) {
        throw new IllegalArgumentException("loopDelaySeconds must be >= 0");
      }
      this.loopDelaySeconds = loopDelaySeconds;
      return this;
    }

    public TimelinePlayback build() {
      return new TimelinePlayback(this);
    }
  }
}
