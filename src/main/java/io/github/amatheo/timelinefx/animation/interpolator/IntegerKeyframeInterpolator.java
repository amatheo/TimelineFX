package io.github.amatheo.timelinefx.animation.interpolator;

import io.github.amatheo.timelinefx.animation.Keyframe;
import io.github.amatheo.timelinefx.animation.KeyframeInterpolator;

public final class IntegerKeyframeInterpolator implements KeyframeInterpolator<Integer> {
  private static final IntegerKeyframeInterpolator INSTANCE = new IntegerKeyframeInterpolator();

  public static IntegerKeyframeInterpolator instance() {
    return INSTANCE;
  }

  private IntegerKeyframeInterpolator() {}

  @Override
  public Integer interpolate(Keyframe<Integer> from, Keyframe<Integer> to, double progress, double segmentDurationSeconds) {
    if (segmentDurationSeconds <= 0.0d) {
      return to.value();
    }

    double hermite = hermite(from, to, progress, segmentDurationSeconds);
    if (!Double.isNaN(hermite)) {
      return clampToInt(Math.round(hermite));
    }

    int start = from.value();
    int end = to.value();
    double linear = start + (end - start) * progress;
    return clampToInt(Math.round(linear));
  }

  private static double hermite(Keyframe<Integer> from, Keyframe<Integer> to, double progress, double duration) {
    Integer m0 = from.outTangent().orElse(null);
    Integer m1 = to.inTangent().orElse(null);
    if (m0 == null && m1 == null) {
      return Double.NaN;
    }

    double start = from.value();
    double end = to.value();
    double slope = (end - start) / duration;
    double tangent0 = (m0 != null) ? m0.doubleValue() : slope;
    double tangent1 = (m1 != null) ? m1.doubleValue() : slope;

    double t2 = progress * progress;
    double t3 = t2 * progress;

    double h00 = 2.0 * t3 - 3.0 * t2 + 1.0;
    double h10 = t3 - 2.0 * t2 + progress;
    double h01 = -2.0 * t3 + 3.0 * t2;
    double h11 = t3 - t2;

    return h00 * start + h10 * tangent0 * duration + h01 * end + h11 * tangent1 * duration;
  }

  private static int clampToInt(long value) {
    if (value > Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    }
    if (value < Integer.MIN_VALUE) {
      return Integer.MIN_VALUE;
    }
    return (int) value;
  }
}
