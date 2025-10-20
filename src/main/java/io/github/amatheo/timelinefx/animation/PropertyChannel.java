package io.github.amatheo.timelinefx.animation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class PropertyChannel<T> implements Animatable<T> {
  private final List<Keyframe<T>> keyframes;
  private final KeyframeInterpolator<T> interpolator;
  private final Extrapolation preBehavior;
  private final Extrapolation postBehavior;

  PropertyChannel(List<Keyframe<T>> keyframes, KeyframeInterpolator<T> interpolator,
                  Extrapolation preBehavior, Extrapolation postBehavior) {
    if (keyframes.isEmpty()) {
      throw new IllegalArgumentException("A channel requires at least one keyframe");
    }
    this.keyframes = List.copyOf(sorted(keyframes));
    this.interpolator = Objects.requireNonNull(interpolator, "interpolator");
    this.preBehavior = Objects.requireNonNull(preBehavior, "preBehavior");
    this.postBehavior = Objects.requireNonNull(postBehavior, "postBehavior");
  }

  private static <T> List<Keyframe<T>> sorted(List<Keyframe<T>> input) {
    List<Keyframe<T>> sorted = new ArrayList<>(input);
    sorted.sort(Comparator.comparingDouble(Keyframe::time));
    return sorted;
  }

  public List<Keyframe<T>> keyframes() {
    return keyframes;
  }

  public double startTimeSeconds() {
    return keyframes.getFirst().time();
  }

  public double endTimeSeconds() {
    return keyframes.getLast().time();
  }

  public double durationSeconds() {
    return endTimeSeconds() - startTimeSeconds();
  }

  public Extrapolation preBehavior() {
    return preBehavior;
  }

  public Extrapolation postBehavior() {
    return postBehavior;
  }

  @Override
  public T get(long tick, double tSeconds) {
    if (keyframes.size() == 1) {
      return keyframes.getFirst().value();
    }
    double time = remapTime(tSeconds);
    for (int i = 0; i < keyframes.size() - 1; i++) {
      Keyframe<T> current = keyframes.get(i);
      Keyframe<T> next = keyframes.get(i + 1);
      if (time <= next.time()) {
        double segmentDuration = next.time() - current.time();
        if (segmentDuration <= 0.0d) {
          return next.value();
        }
        double rawProgress = (time - current.time()) / segmentDuration;

        double easedProgress = current.curve().map(rawProgress);

        return interpolator.interpolate(current, next, easedProgress, segmentDuration);
      }
    }
    return keyframes.getLast().value();
  }

  private double remapTime(double tSeconds) {
    double firstTime = keyframes.getFirst().time();
    double lastTime = keyframes.getLast().time();

    if (tSeconds < firstTime) {
      return applyBehavior(tSeconds, preBehavior, firstTime, lastTime);
    }
    if (tSeconds > lastTime) {
      return applyBehavior(tSeconds, postBehavior, firstTime, lastTime);
    }
    return tSeconds;
  }

  private double applyBehavior(double time, Extrapolation behavior, double first, double last) {
    double duration = last - first;
    if (duration <= 0.0d) {
      return first;
    }
    return switch (behavior) {
      case HOLD -> (time < first) ? first : last;
      case LOOP -> loopTime(time, first, duration);
      case PING_PONG -> pingPongTime(time, first, duration);
    };
  }

  private static double loopTime(double time, double first, double duration) {
    double normalized = (time - first) % duration;
    if (normalized < 0) {
      normalized += duration;
    }
    return first + normalized;
  }

  private static double pingPongTime(double time, double first, double duration) {
    double span = duration * 2.0;
    double normalized = (time - first) % span;
    if (normalized < 0) {
      normalized += span;
    }
    if (normalized <= duration) {
      return first + normalized;
    }
    double overshoot = normalized - duration;
    return first + duration - overshoot;
  }

  private static double clamp01(double value) {
    if (value <= 0.0d) return 0.0d;
    if (value >= 1.0d) return 1.0d;
    return value;
  }

  public static final class Builder<T> {
    private final List<Keyframe<T>> keyframes = new ArrayList<>();
    private KeyframeInterpolator<T> interpolator;
    private Extrapolation preBehavior = Extrapolation.HOLD;
    private Extrapolation postBehavior = Extrapolation.HOLD;

    Builder<T> interpolator(KeyframeInterpolator<T> interpolator) {
      this.interpolator = interpolator;
      return this;
    }

    public Builder<T> preBehavior(Extrapolation behavior) {
      this.preBehavior = behavior;
      return this;
    }

    public Builder<T> postBehavior(Extrapolation behavior) {
      this.postBehavior = behavior;
      return this;
    }

    public Builder<T> add(Keyframe<T> keyframe) {
      keyframes.add(keyframe);
      return this;
    }

    public PropertyChannel<T> build() {
      if (interpolator == null) {
        throw new IllegalStateException("Interpolator must be provided");
      }
      return new PropertyChannel<>(keyframes, interpolator, preBehavior, postBehavior);
    }
  }
}
