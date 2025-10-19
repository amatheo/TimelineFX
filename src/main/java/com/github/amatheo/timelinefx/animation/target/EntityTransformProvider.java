package com.github.amatheo.timelinefx.animation.target;

import com.github.amatheo.timelinefx.transform.Transform;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A {@link TransformProvider} that tracks a Bukkit {@link Entity}.
 */
public final class EntityTransformProvider implements TransformProvider {
  private final Supplier<Entity> entitySupplier;
  private final boolean trackRotation;

  /**
   * @param entity The entity to track. A direct reference is held.
   * @param trackRotation If true, the entity's yaw/pitch will be used for the transform's rotation.
   */
  public EntityTransformProvider(Entity entity, boolean trackRotation) {
    Objects.requireNonNull(entity, "entity");
    this.entitySupplier = () -> entity;
    this.trackRotation = trackRotation;
  }

  /**
   * @param entitySupplier A supplier for the entity, useful if the entity might change.
   * @param trackRotation If true, the entity's yaw/pitch will be used for the transform's rotation.
   */
  public EntityTransformProvider(Supplier<Entity> entitySupplier, boolean trackRotation) {
    this.entitySupplier = Objects.requireNonNull(entitySupplier, "entitySupplier");
    this.trackRotation = trackRotation;
  }

  @Override
  public Transform getTransform() {
    Entity entity = entitySupplier.get();
    if (entity == null || !entity.isValid()) {
      return null; // The target is gone
    }

    Location loc = entity.getLocation();
    Vector3d position = new Vector3d(loc.getX(), loc.getY(), loc.getZ());
    Quaterniond rotation = new Quaterniond();

    if (trackRotation) {
      rotation = fromBukkitYawPitch(loc.getYaw(), loc.getPitch());
    }

    return new Transform(position, rotation, new Vector3d(1, 1, 1));
  }

  /**
   * Converts Bukkit yaw/pitch (in degrees) to a Quaterniond.
   * <p>
   *   Yaw is applied first, then pitch.
   *   The resulting quaternion represents the rotation from local space to world space.
   * </p>
   * @param yaw
   * @param pitch
   * @return A quaternion representing the combined yaw/pitch rotation.
   */
  private static Quaterniond fromBukkitYawPitch(double yaw, double pitch) {
    double yawRad = Math.toRadians(-yaw);
    double pitchRad = Math.toRadians(pitch);
    return new Quaterniond().rotateY(yawRad).rotateX(pitchRad);
  }
}
