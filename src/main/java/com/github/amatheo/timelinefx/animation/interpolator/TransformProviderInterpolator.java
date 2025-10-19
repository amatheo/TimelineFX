package com.github.amatheo.timelinefx.animation.interpolator;

import com.github.amatheo.timelinefx.animation.Keyframe;
import com.github.amatheo.timelinefx.animation.KeyframeInterpolator;
import com.github.amatheo.timelinefx.animation.target.InterpolatedTransformProvider;
import com.github.amatheo.timelinefx.animation.target.TransformProvider;

/**
 * Interpolates between two {@link TransformProvider} keyframes.
 * If progress is between 0 and 1, it returns an {@link InterpolatedTransformProvider}
 * to smoothly transition between the two anchors. Otherwise, it returns the start or end provider directly.
 */
public final class TransformProviderInterpolator implements KeyframeInterpolator<TransformProvider> {

    public static final TransformProviderInterpolator INSTANCE = new TransformProviderInterpolator();

  public static TransformProviderInterpolator instance() {
    return INSTANCE;
  }


  private TransformProviderInterpolator() {}

    @Override
    public TransformProvider interpolate(Keyframe<TransformProvider> from, Keyframe<TransformProvider> to, double progress, double segmentDurationSeconds) {
        if (progress <= 0.0) {
            return from.value();
        }
        if (progress >= 1.0) {
            return to.value();
        }

        // Between keyframes, we create a dynamic provider that handles the transition.
        return new InterpolatedTransformProvider(from.value(), to.value(), progress);
    }
}
