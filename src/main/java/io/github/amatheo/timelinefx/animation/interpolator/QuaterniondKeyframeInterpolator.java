package io.github.amatheo.timelinefx.animation.interpolator;

import io.github.amatheo.timelinefx.animation.Keyframe;
import io.github.amatheo.timelinefx.animation.KeyframeInterpolator;
import org.joml.Quaterniond;

/**
 * Performs spherical linear interpolation (slerp) between quaternion keyframes.
 * Tangents are currently ignored; the shortest arc between orientations is used.
 */
public final class QuaterniondKeyframeInterpolator implements KeyframeInterpolator<Quaterniond> {
  private static final QuaterniondKeyframeInterpolator INSTANCE = new QuaterniondKeyframeInterpolator();

  private QuaterniondKeyframeInterpolator() {}

  public static QuaterniondKeyframeInterpolator instance() {
    return INSTANCE;
  }

  @Override
  public Quaterniond interpolate(Keyframe<Quaterniond> from, Keyframe<Quaterniond> to, double progress,
                                 double segmentDurationSeconds) {
    if (progress <= 0.0d) {
      return new Quaterniond(from.value());
    }
    if (progress >= 1.0d) {
      return new Quaterniond(to.value());
    }

    Quaterniond a = new Quaterniond(from.value()).normalize();
    Quaterniond b = new Quaterniond(to.value()).normalize();

    double dot = a.dot(b);
    if (dot < 0.0d) {
      // Ensure we take the shortest path
      b.x = -b.x;
      b.y = -b.y;
      b.z = -b.z;
      b.w = -b.w;
      dot = -dot;
    }

    if (dot > 0.9995d) {
      // Quaternions are very close; fall back to lerp to avoid precision issues
      a.x += progress * (b.x - a.x);
      a.y += progress * (b.y - a.y);
      a.z += progress * (b.z - a.z);
      a.w += progress * (b.w - a.w);
      return new Quaterniond(a).normalize();
    }

    double theta0 = Math.acos(dot);
    double sinTheta0 = Math.sin(theta0);
    double theta = theta0 * progress;
    double sinTheta = Math.sin(theta);

    double s0 = Math.cos(theta) - dot * sinTheta / sinTheta0;
    double s1 = sinTheta / sinTheta0;

    double x = s0 * a.x + s1 * b.x;
    double y = s0 * a.y + s1 * b.y;
    double z = s0 * a.z + s1 * b.z;
    double w = s0 * a.w + s1 * b.w;

    return new Quaterniond(x, y, z, w).normalize();
  }
}
