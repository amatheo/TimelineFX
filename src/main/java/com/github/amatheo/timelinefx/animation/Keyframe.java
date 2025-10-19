package com.github.amatheo.timelinefx.animation;

import java.util.Objects;
import java.util.Optional;

public final class Keyframe<T> {
  private final double timeSeconds;
  private final T value;
  private final T inTangent;
  private final T outTangent;
  private final TemporalCurve curve;

  private Keyframe(Builder<T> builder) {
    this.timeSeconds = builder.timeSeconds;
    this.value = Objects.requireNonNull(builder.value, "value");
    this.inTangent = builder.inTangent;
    this.outTangent = builder.outTangent;
    this.curve = Objects.requireNonNull(builder.curve, "curve");
  }

  public double time() {
    return timeSeconds;
  }

  public double timeSeconds() {
    return timeSeconds;
  }

  public T value() {
    return value;
  }

  public Optional<T> inTangent() {
    return Optional.ofNullable(inTangent);
  }

  public Optional<T> outTangent() {
    return Optional.ofNullable(outTangent);
  }

  public TemporalCurve curve() {
    return curve;
  }

  public static <T> Builder<T> at(double timeSeconds) {
    return new Builder<>(timeSeconds);
  }

  public static <T> Keyframe<T> of(double timeSeconds, T value) {
    return Keyframe.<T>at(timeSeconds).value(value).build();
  }

  public static final class Builder<T> {
    private final double timeSeconds;
    private T value;
    private T inTangent;
    private T outTangent;
    private TemporalCurve curve = Easing.LINEAR;

    private Builder(double timeSeconds) {
      this.timeSeconds = timeSeconds;
    }

    public Builder<T> value(T value) {
      this.value = value;
      return this;
    }

    public Builder<T> inTangent(T inTangent) {
      this.inTangent = inTangent;
      return this;
    }

    public Builder<T> outTangent(T outTangent) {
      this.outTangent = outTangent;
      return this;
    }

    /**
     * Apply a custom temporal curve for the transition from this keyframe.
     *
     * @param curve The temporal curve to apply.
     * @return this builder
     */
    public Builder<T> curve(TemporalCurve curve) {
      this.curve = curve;
      return this;
    }

    /**
     * Apply a predefined easing function for the transition from this keyframe.
     *
     * @param easing The easing function to apply.
     * @return this builder
     */
    public Builder<T> easing(Easing easing) {
      this.curve = Objects.requireNonNull(easing, "easing");
      return this;
    }

    /**
     * Apply a cubic Bézier curve for the transition from this keyframe.
     *
     * @param p1x X coordinate of the first control point.
     * @param p1y Y coordinate of the first control point (can exceed [0,1]).
     * @param p2x X coordinate of the second control point.
     * @param p2y Y coordinate of the second control point (can exceed [0,1]).
     * @return this builder
     */
    public Builder<T> bezier(double p1x, double p1y, double p2x, double p2y) {
      this.curve = new BezierCurve(p1x, p1y, p2x, p2y);
      return this;
    }


    public Keyframe<T> build() {
      return new Keyframe<>(this);
    }
  }
}
