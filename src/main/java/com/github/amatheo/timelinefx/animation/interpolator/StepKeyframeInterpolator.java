package com.github.amatheo.timelinefx.animation.interpolator;

import com.github.amatheo.timelinefx.animation.Keyframe;
import com.github.amatheo.timelinefx.animation.KeyframeInterpolator;

public class StepKeyframeInterpolator<T> implements KeyframeInterpolator<T> {
  private static final StepKeyframeInterpolator<?> INSTANCE = new StepKeyframeInterpolator<>();

  private StepKeyframeInterpolator() {}

  @SuppressWarnings("unchecked")
  public static <T> StepKeyframeInterpolator<T> instance() {
    return (StepKeyframeInterpolator<T>) INSTANCE;
  }

  @Override
  public T interpolate(Keyframe<T> from, Keyframe<T> to, double progress,
                       double segmentDurationSeconds) {
    if (segmentDurationSeconds <= 0.0d) {
      return to.value();
    }
    return (progress < 1.0d) ? from.value() : to.value();
  }
}
