package io.github.amatheo.timelinefx.animation.binding;

import io.github.amatheo.timelinefx.animation.target.TransformProvider;
import io.github.amatheo.timelinefx.animation.timeline.TimelineProperty;
import io.github.amatheo.timelinefx.animation.value.AnimatedValue;
import io.github.amatheo.timelinefx.animation.value.ConstantValue;
import io.github.amatheo.timelinefx.animation.value.ValueProvider;
import io.github.amatheo.timelinefx.transform.Transform;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builder for creating {@link TimelineBindings} instances with support for transforms,
 * anchors, and custom parameters.
 */
public final class TimelineBindingsBuilder {
  private Transform transform = Transform.identity();
  private ValueProvider<TransformProvider> anchorProvider;
  private final List<TimelineBinding> bindings = new ArrayList<>();
  private final TransformBinding.Builder transformBindingBuilder = TransformBinding.builder();
  private boolean transformBindingAdded = false;

  /**
   * Sets the base static transform for these bindings.
   * Other transforms (anchor, position, etc.) are composed on top of this.
   */
  public TimelineBindingsBuilder setTransform(Transform transform) {
    this.transform = Objects.requireNonNull(transform, "transform");
    return this;
  }

  /**
   * Anchors the animation to a constant dynamic target.
   */
  public TimelineBindingsBuilder setAnchor(TransformProvider provider) {
    this.anchorProvider = new ConstantValue<>(provider);
    return this;
  }

  /**
   * Link the animation's to a property that provides a Bukkit entity.
   */
  public TimelineBindingsBuilder bindAnchor(TimelineProperty<TransformProvider> property) {
    this.anchorProvider = new AnimatedValue<>(property);
    return this;
  }

  /** Binds a custom parameter to a timeline property. */
  public <T> TimelineBindingsBuilder bindParameter(String name, TimelineProperty<T> property) {
    bindings.add(new ParameterBinding(name, new AnimatedValue<>(property)));
    return this;
  }

  /**
   * Binds a parameter with a default value when not present in the timeline.
   */
  public <T> TimelineBindingsBuilder bindParameter(String name, TimelineProperty<T> property, T defaultValue) {
    bindings.add(new ParameterBinding(name, new AnimatedValue<>(property, defaultValue)));
    return this;
  }

  /**
   * Define a parameter linked to a property that provides a constant value.
   */
  public TimelineBindingsBuilder setParameter(String name, Object value) {
    bindings.add(new ParameterBinding(name, new ConstantValue<>(value)));
    return this;
  }

  public TimelineBindingsBuilder setPosition(Vector3d value) {
    transformBindingBuilder.position(new ConstantValue<>(value));
    return this;
  }

  public TimelineBindingsBuilder bindPosition(TimelineProperty<Vector3d> property) {
    transformBindingBuilder.position(new AnimatedValue<>(property));
    return this;
  }

  public TimelineBindingsBuilder setPositionX(double value) {
    transformBindingBuilder.positionComponent(TransformAxis.X, new ConstantValue<>(value));
    return this;
  }

  public TimelineBindingsBuilder bindPositionX(TimelineProperty<Double> property) {
    transformBindingBuilder.positionComponent(TransformAxis.X, new AnimatedValue<>(property));
    return this;
  }

  public TimelineBindingsBuilder setPositionY(double value) {
    transformBindingBuilder.positionComponent(TransformAxis.Y, new ConstantValue<>(value));
    return this;
  }

  public TimelineBindingsBuilder bindPositionY(TimelineProperty<Double> property) {
    transformBindingBuilder.positionComponent(TransformAxis.Y, new AnimatedValue<>(property));
    return this;
  }

  public TimelineBindingsBuilder setPositionZ(double value) {
    transformBindingBuilder.positionComponent(TransformAxis.Z, new ConstantValue<>(value));
    return this;
  }

  public TimelineBindingsBuilder bindPositionZ(TimelineProperty<Double> property) {
    transformBindingBuilder.positionComponent(TransformAxis.Z, new AnimatedValue<>(property));
    return this;
  }

  public TimelineBindingsBuilder setScale(Vector3d value) {
    transformBindingBuilder.scale(new ConstantValue<>(value));
    return this;
  }

  public TimelineBindingsBuilder bindScale(TimelineProperty<Vector3d> property) {
    transformBindingBuilder.scale(new AnimatedValue<>(property));
    return this;
  }

  public TimelineBindingsBuilder setUniformScale(double value) {
    transformBindingBuilder.uniformScale(new ConstantValue<>(value));
    return this;
  }

  public TimelineBindingsBuilder bindUniformScale(TimelineProperty<Double> property) {
    transformBindingBuilder.uniformScale(new AnimatedValue<>(property));
    return this;
  }

  public TimelineBindingsBuilder setRotation(Quaterniond value) {
    transformBindingBuilder.rotation(new ConstantValue<>(value));
    return this;
  }

  public TimelineBindingsBuilder bindRotation(TimelineProperty<Quaterniond> property) {
    transformBindingBuilder.rotation(new AnimatedValue<>(property));
    return this;
  }

  public TimelineBindingsBuilder setRotationEuler(Vector3d value) {
    transformBindingBuilder.rotationEuler(new ConstantValue<>(value));
    return this;
  }

  public TimelineBindingsBuilder bindRotationEuler(TimelineProperty<Vector3d> property) {
    transformBindingBuilder.rotationEuler(new AnimatedValue<>(property));
    return this;
  }

  public TimelineBindingsBuilder rotateAxisAngle(Vector3d axis, double angleRadians) {
    transformBindingBuilder.rotateAxisAngle(new ConstantValue<>(axis), new ConstantValue<>(angleRadians));
    return this;
  }

  public TimelineBindingsBuilder rotateAxisAngle(Vector3d axis, TimelineProperty<Double> angleProperty) {
    transformBindingBuilder.rotateAxisAngle(new ConstantValue<>(axis), new AnimatedValue<>(angleProperty));
    return this;
  }

  public TimelineBindingsBuilder rotateAxisAngle(TimelineProperty<Vector3d> axisProperty, double angleRadians) {
    transformBindingBuilder.rotateAxisAngle(new AnimatedValue<>(axisProperty), new ConstantValue<>(angleRadians));
    return this;
  }

  public TimelineBindingsBuilder rotateAxisAngle(TimelineProperty<Vector3d> axisProperty, TimelineProperty<Double> angleProperty) {
    transformBindingBuilder.rotateAxisAngle(new AnimatedValue<>(axisProperty), new AnimatedValue<>(angleProperty));
    return this;
  }

  public TimelineBindings build() {
    // Add the transform binding if it was not added yet and has any modifications.
    if (!transformBindingAdded) {
      bindings.add(transformBindingBuilder.build());
      transformBindingAdded = true;
    }
    return TimelineBindings.fromBuilder(transform, anchorProvider, bindings);
  }
}
