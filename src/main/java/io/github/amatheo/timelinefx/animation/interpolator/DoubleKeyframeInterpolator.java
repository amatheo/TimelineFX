package io.github.amatheo.timelinefx.animation.interpolator;

import io.github.amatheo.timelinefx.animation.Keyframe;
import io.github.amatheo.timelinefx.animation.KeyframeInterpolator;

public final class DoubleKeyframeInterpolator implements KeyframeInterpolator<Double> {
  private static final DoubleKeyframeInterpolator INSTANCE = new DoubleKeyframeInterpolator();

  public static DoubleKeyframeInterpolator instance() {
    return INSTANCE;
  }

  private DoubleKeyframeInterpolator() {}

  @Override
  public Double interpolate(Keyframe<Double> from, Keyframe<Double> to, double progress, double segmentDurationSeconds) {
    if (segmentDurationSeconds <= 0.0d) {
      return to.value();
    }

    double hermite = hermite(from, to, progress, segmentDurationSeconds);
    if (!Double.isNaN(hermite)) {
      return hermite;
    }

    double start = from.value();
    double end = to.value();
    return start + (end - start) * progress;
  }

  private static double hermite(Keyframe<Double> from, Keyframe<Double> to, double progress, double duration) {
    Double m0 = from.outTangent().orElse(null);
    Double m1 = to.inTangent().orElse(null);
    if (m0 == null && m1 == null) {
      return Double.NaN;
    }

    double start = from.value();
    double end = to.value();
    double slope = (end - start) / duration;
    double tangent0 = (m0 != null) ? m0 : slope;
    double tangent1 = (m1 != null) ? m1 : slope;

    double t2 = progress * progress;
    double t3 = t2 * progress;

    double h00 = 2.0 * t3 - 3.0 * t2 + 1.0;
    double h10 = t3 - 2.0 * t2 + progress;
    double h01 = -2.0 * t3 + 3.0 * t2;
    double h11 = t3 - t2;

    return h00 * start + h10 * tangent0 * duration + h01 * end + h11 * tangent1 * duration;
  }
}
