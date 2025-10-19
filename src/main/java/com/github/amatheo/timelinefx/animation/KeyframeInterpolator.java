package com.github.amatheo.timelinefx.animation;

public interface KeyframeInterpolator<T> {
  T interpolate(Keyframe<T> from, Keyframe<T> to, double progress, double segmentDurationSeconds);
}
