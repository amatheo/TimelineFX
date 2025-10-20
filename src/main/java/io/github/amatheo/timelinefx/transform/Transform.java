package io.github.amatheo.timelinefx.transform;

import org.joml.Quaterniond;
import org.joml.Vector3d;

public record Transform(Vector3d position, Quaterniond rotation, Vector3d scale) {
  public static Transform identity() {
    return new Transform(new Vector3d(0), new Quaterniond(), new Vector3d(1, 1, 1));
  }

  /**
   * Composes two transforms hierarchically, treating 'child' as being in the local space of 'parent'.
   * This follows standard 3D engine behavior (e.g., Unity, Unreal).
   * <p>
   * The operations are applied in the standard order: Scale -> Rotate -> Translate.
   *
   * @param parent The parent (world) transform.
   * @param child The child (local) transform.
   * @return The resulting world transform of the child.
   */
  public static Transform compose(Transform parent, Transform child) {
    // 1. Scale: The scales are multiplied component-wise.
    Vector3d finalScale = new Vector3d(parent.scale).mul(child.scale);

    // 2. Rotation: The rotations are composed. The child's rotation is applied relative to the parent's.
    Quaterniond finalRotation = new Quaterniond(parent.rotation).mul(child.rotation);

    // 3. Position: The child's position is scaled by the parent's scale,
    // then rotated by the parent's rotation, and finally added to the parent's position.
    Vector3d finalPosition = new Vector3d();
    child.position.mul(parent.scale, finalPosition); // Scale child position
    parent.rotation.transform(finalPosition);       // Rotate child position
    finalPosition.add(parent.position);             // Translate to parent's world position

    return new Transform(finalPosition, finalRotation, finalScale);
  }

  /**
   * Transforms a point from local space to world space.
   * @param localPoint The point in local space.
   * @return The transformed point in world space.
   */
  public Vector3d transformPoint(Vector3d localPoint) {
    Vector3d result = new Vector3d();
    localPoint.mul(this.scale, result);
    this.rotation.transform(result);
    result.add(this.position);
    return result;
  }

  /**
   * Transforms a direction from local space to world space (ignores translation and scale).
   * @param localDirection The direction in local space.
   * @return The transformed direction in world space.
   */
  public Vector3d transformDirection(Vector3d localDirection) {
    Vector3d result = new Vector3d();
    this.rotation.transform(localDirection, result);
    return result;
  }
}