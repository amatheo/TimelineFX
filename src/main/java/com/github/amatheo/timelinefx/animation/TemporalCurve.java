package com.github.amatheo.timelinefx.animation;

/**
 * Maps a normalized progress value in [0,1] to another value in [0,1].
 * Used to remap interpolation progress for easing or custom timing curves.
 */
@FunctionalInterface
public interface TemporalCurve {
  double map(double progress);
}
