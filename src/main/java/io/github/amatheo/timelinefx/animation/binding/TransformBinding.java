package io.github.amatheo.timelinefx.animation.binding;

import io.github.amatheo.timelinefx.animation.timeline.TimelineSnapshot;
import io.github.amatheo.timelinefx.animation.value.ValueProvider;
import io.github.amatheo.timelinefx.transform.Transform;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

final class TransformBinding implements TimelineBinding {

  private final ValueProvider<Transform> transformProvider;
  private final ValueProvider<Vector3d> positionProvider;
  private final Map<TransformAxis, ValueProvider<Double>> positionComponentProviders;
  private final ValueProvider<Vector3d> scaleProvider;
  private final ValueProvider<Double> uniformScaleProvider;
  private final Map<TransformAxis, ValueProvider<Double>> scaleComponentProviders;
  private final ValueProvider<Quaterniond> rotationProvider;
  private final ValueProvider<Vector3d> eulerRotationProvider;
  private final Map<TransformAxis, ValueProvider<Double>> eulerComponentProviders;
  private final List<AxisAngleRotation> axisAngleRotations;

  private TransformBinding(Builder builder) {
    this.transformProvider = builder.transformProvider;
    this.positionProvider = builder.positionProvider;
    this.positionComponentProviders = Map.copyOf(builder.positionComponentProviders);
    this.scaleProvider = builder.scaleProvider;
    this.uniformScaleProvider = builder.uniformScaleProvider;
    this.scaleComponentProviders = Map.copyOf(builder.scaleComponentProviders);
    this.rotationProvider = builder.rotationProvider;
    this.eulerRotationProvider = builder.eulerRotationProvider;
    this.eulerComponentProviders = Map.copyOf(builder.eulerComponentProviders);
    this.axisAngleRotations = List.copyOf(builder.axisAngleRotations);
  }

  private static Quaterniond quaternionFromEuler(double rotX, double rotY, double rotZ) {
    return new Quaterniond().rotateY(rotY).rotateX(rotX).rotateZ(rotZ);
  }

  static Builder builder() {
    return new Builder();
  }

  @Override
  public void apply(TimelineSnapshot snapshot, MutableBindingState state) {

    if (transformProvider != null && transformProvider.get(snapshot) != null) {
      state.overrideTransform(transformProvider.get(snapshot));
    }
    if (positionProvider != null && positionProvider.get(snapshot) != null) {
      state.setPosition(positionProvider.get(snapshot));
    }
    for (var entry : positionComponentProviders.entrySet()) {
      Double component = entry.getValue().get(snapshot);
      if (component != null) state.setPositionComponent(entry.getKey(), component);
    }
    if (scaleProvider != null && scaleProvider.get(snapshot) != null) {
      state.setScale(scaleProvider.get(snapshot));
    }
    if (uniformScaleProvider != null && uniformScaleProvider.get(snapshot) != null) {
      state.setUniformScale(uniformScaleProvider.get(snapshot));
    }
    for (var entry : scaleComponentProviders.entrySet()) {
      Double component = entry.getValue().get(snapshot);
      if (component != null) state.setScaleComponent(entry.getKey(), component);
    }

    // Compose rotations in a defined order (Quaternion -> Euler -> AxisAngle)
    if (rotationProvider != null && rotationProvider.get(snapshot) != null) {
      state.applyRotation(rotationProvider.get(snapshot));
    }
    applyEulerRotation(snapshot, state);
    for (AxisAngleRotation rotation : axisAngleRotations) {
      rotation.apply(snapshot, state);
    }
  }

  private void applyEulerRotation(TimelineSnapshot snapshot, MutableBindingState state) {
    double x = 0.0, y = 0.0, z = 0.0;
    boolean hasValue = false;

    if (eulerRotationProvider != null) {
      Vector3d euler = eulerRotationProvider.get(snapshot);
      if (euler != null) {
        x = euler.x;
        y = euler.y;
        z = euler.z;
        hasValue = true;
      }
    }
    for (var entry : eulerComponentProviders.entrySet()) {
      Double value = entry.getValue().get(snapshot);
      if (value != null) {
        hasValue = true;
        switch (entry.getKey()) {
          case X -> x = value;
          case Y -> y = value;
          case Z -> z = value;
        }
      }
    }
    if (hasValue) {
      state.applyRotation(quaternionFromEuler(x, y, z));
    }
  }

  public static final class Builder {
    private final Map<TransformAxis, ValueProvider<Double>> positionComponentProviders = new EnumMap<>(TransformAxis.class);
    private final Map<TransformAxis, ValueProvider<Double>> scaleComponentProviders = new EnumMap<>(TransformAxis.class);
    private final Map<TransformAxis, ValueProvider<Double>> eulerComponentProviders = new EnumMap<>(TransformAxis.class);
    private final List<AxisAngleRotation> axisAngleRotations = new ArrayList<>();
    private ValueProvider<Transform> transformProvider;
    private ValueProvider<Vector3d> positionProvider;
    private ValueProvider<Vector3d> scaleProvider;
    private ValueProvider<Double> uniformScaleProvider;
    private ValueProvider<Quaterniond> rotationProvider;
    private ValueProvider<Vector3d> eulerRotationProvider;

    public Builder transform(ValueProvider<Transform> provider) {
      this.transformProvider = provider;
      return this;
    }

    public Builder position(ValueProvider<Vector3d> provider) {
      this.positionProvider = provider;
      return this;
    }

    public Builder positionComponent(TransformAxis axis, ValueProvider<Double> provider) {
      this.positionComponentProviders.put(axis, provider);
      return this;
    }

    public Builder scale(ValueProvider<Vector3d> provider) {
      this.scaleProvider = provider;
      return this;
    }

    public Builder uniformScale(ValueProvider<Double> provider) {
      this.uniformScaleProvider = provider;
      return this;
    }

    public Builder scaleComponent(TransformAxis axis, ValueProvider<Double> provider) {
      this.scaleComponentProviders.put(axis, provider);
      return this;
    }

    public Builder rotation(ValueProvider<Quaterniond> provider) {
      this.rotationProvider = provider;
      return this;
    }

    public Builder rotationEuler(ValueProvider<Vector3d> provider) {
      this.eulerRotationProvider = provider;
      return this;
    }


    public Builder rotationX(ValueProvider<Double> provider) {
      this.eulerComponentProviders.put(TransformAxis.X, provider);
      return this;
    }

    public Builder rotationY(ValueProvider<Double> provider) {
      this.eulerComponentProviders.put(TransformAxis.Y, provider);
      return this;
    }

    public Builder rotationZ(ValueProvider<Double> provider) {
      this.eulerComponentProviders.put(TransformAxis.Z, provider);
      return this;
    }

    public Builder rotateAxisAngle(ValueProvider<Vector3d> axisProvider, ValueProvider<Double> angleProvider) {
      axisAngleRotations.add(new AxisAngleRotation(axisProvider, angleProvider));
      return this;
    }

    public TransformBinding build() {
      return new TransformBinding(this);
    }
  }

  /**
   * Simplified representation of an axis-angle rotation using ValueProviders.
   */
  private record AxisAngleRotation(ValueProvider<Vector3d> axisProvider, ValueProvider<Double> angleProvider) {

    public void apply(TimelineSnapshot snapshot, MutableBindingState state) {
      Vector3d axis = axisProvider.get(snapshot);
      Double angle = angleProvider.get(snapshot);

      if (axis != null && angle != null && angle != 0.0 && axis.lengthSquared() > 1e-12) {
        Vector3d normalized = new Vector3d(axis).normalize();
        Quaterniond delta = new Quaterniond().rotateAxis(angle, normalized.x, normalized.y, normalized.z);
        state.applyRotation(delta);
      }
    }
  }
}
