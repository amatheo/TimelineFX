package io.github.amatheo.timelinefx.animation.target;


import io.github.amatheo.timelinefx.transform.Transform;

/**
 * Provides a dynamic Transform at runtime.
 * This allows animations to be anchored to moving objects like entities.
 */
@FunctionalInterface
public interface TransformProvider {
  /**
   * Gets the current transform.
   * @return The current transform, or null if the target is no longer valid.
   */
  Transform getTransform();
}