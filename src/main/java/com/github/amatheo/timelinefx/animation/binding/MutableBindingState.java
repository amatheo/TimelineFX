package com.github.amatheo.timelinefx.animation.binding;

import com.github.amatheo.timelinefx.transform.Transform;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.Map;

final class MutableBindingState {
  private final Vector3d position;
  private final Vector3d scale;
  private final Quaterniond rotation;
  private final Map<String, Object> parameters = new HashMap<>();

  MutableBindingState(Transform baseTransform) {
    this.position = new Vector3d(baseTransform.position());
    this.scale = new Vector3d(baseTransform.scale());
    this.rotation = new Quaterniond(baseTransform.rotation());
  }

  void overrideTransform(Transform transform) {
    this.position.set(transform.position());
    this.scale.set(transform.scale());
    this.rotation.set(transform.rotation());
  }

  void setPosition(Vector3d vector) {
    this.position.set(vector);
  }

  void setPositionComponent(TransformAxis axis, double value) {
    switch (axis) {
      case X -> this.position.x = value;
      case Y -> this.position.y = value;
      case Z -> this.position.z = value;
    }
  }

  void setScale(Vector3d vector) {
    this.scale.set(vector);
  }

  void setScaleComponent(TransformAxis axis, double value) {
    switch (axis) {
      case X -> this.scale.x = value;
      case Y -> this.scale.y = value;
      case Z -> this.scale.z = value;
    }
  }

  void setUniformScale(double value) {
    this.scale.set(value, value, value);
  }

  void setRotation(Quaterniond quaternion) {
    this.rotation.set(quaternion);
  }

  void applyRotation(Quaterniond delta) {
    this.rotation.mul(delta);
  }

  void setParameter(String name, Object value) {
    this.parameters.put(name, value);
  }

  BindingResult toResult() {
    Transform transform = new Transform(new Vector3d(position), new Quaterniond(rotation), new Vector3d(scale));
    return new BindingResult(transform, Map.copyOf(parameters));
  }
}
