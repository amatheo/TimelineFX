package com.github.amatheo.timelinefx.animation.interpolator;

import com.github.amatheo.timelinefx.animation.Keyframe;
import com.github.amatheo.timelinefx.animation.KeyframeInterpolator;
import org.joml.Vector3d;

/**
 * Component-wise interpolator for {@link Vector3d} keyframes. Supports optional Hermite tangents.
 */
public final class Vector3dKeyframeInterpolator implements KeyframeInterpolator<Vector3d> {
  private static final Vector3dKeyframeInterpolator INSTANCE = new Vector3dKeyframeInterpolator();

  public static Vector3dKeyframeInterpolator instance() {
    return INSTANCE;
  }

  private Vector3dKeyframeInterpolator() {}

  @Override
  public Vector3d interpolate(Keyframe<Vector3d> from, Keyframe<Vector3d> to, double progress,
                              double segmentDurationSeconds) {
    if (segmentDurationSeconds <= 0.0d) {
      return new Vector3d(to.value());
    }

    Vector3d hermite = hermite(from, to, progress, segmentDurationSeconds);
    if (hermite != null) {
      return hermite;
    }

    return new Vector3d(from.value()).lerp(to.value(), progress);
  }

  private static Vector3d hermite(Keyframe<Vector3d> from, Keyframe<Vector3d> to,
                                  double progress, double duration) {
    Vector3d m0 = from.outTangent().orElse(null);
    Vector3d m1 = to.inTangent().orElse(null);
    if (m0 == null && m1 == null) {
      return null;
    }

    Vector3d start = new Vector3d(from.value());
    Vector3d end = new Vector3d(to.value());
    Vector3d slope = new Vector3d(end).sub(start).div(duration);
    Vector3d tangent0 = (m0 != null) ? new Vector3d(m0) : new Vector3d(slope);
    Vector3d tangent1 = (m1 != null) ? new Vector3d(m1) : new Vector3d(slope);

    double t2 = progress * progress;
    double t3 = t2 * progress;

    double h00 = 2.0 * t3 - 3.0 * t2 + 1.0;
    double h10 = t3 - 2.0 * t2 + progress;
    double h01 = -2.0 * t3 + 3.0 * t2;
    double h11 = t3 - t2;

    Vector3d result = new Vector3d();
    result.x = h00 * start.x + h10 * tangent0.x * duration + h01 * end.x + h11 * tangent1.x * duration;
    result.y = h00 * start.y + h10 * tangent0.y * duration + h01 * end.y + h11 * tangent1.y * duration;
    result.z = h00 * start.z + h10 * tangent0.z * duration + h01 * end.z + h11 * tangent1.z * duration;
    return result;
  }
}
