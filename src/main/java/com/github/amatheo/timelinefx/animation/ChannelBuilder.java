package com.github.amatheo.timelinefx.animation;

import com.github.amatheo.timelinefx.animation.interpolator.*;
import com.github.amatheo.timelinefx.animation.interpolator.*;
import com.github.amatheo.timelinefx.animation.target.TransformProvider;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A builder for creating PropertyChannel instances with a fluent API.
 * <p>
 * Provides static methods for common types with sensible defaults.
 * </p>
 * @param <T> the type of objects in the channel
 */
public final class ChannelBuilder<T> {
  private final PropertyChannel.Builder<T> delegate;

  private ChannelBuilder(KeyframeInterpolator<T> interpolator) {
    this.delegate = new PropertyChannel.Builder<>();
    this.delegate.interpolator(Objects.requireNonNull(interpolator, "interpolator"));
  }

  /**
   * Creates a builder for a PropertyChannel of Booleans.
   * <p>
   * Defaults to using {@link StepKeyframeInterpolator} for step-wise changes.
   * </p>
   * @return a ChannelBuilder for Booleans
   */
  public static ChannelBuilder<Boolean> booleans() {
    return new ChannelBuilder<>(StepKeyframeInterpolator.instance());
  }

  /**
   * Creates a builder for a PropertyChannel of TransformProviders.
   * <p>
   * Defaults to using {@link TransformProviderInterpolator} for smooth anchor transitions.
   * </p>
   * @return a ChannelBuilder for TransformProviders
   */
  public static ChannelBuilder<TransformProvider> transformProvider() {
    return new ChannelBuilder<>(TransformProviderInterpolator.instance());
  }

  /**
   * Creates a builder for a PropertyChannel of generic objects.
   * <p>
   * Defaults to using StepKeyframeInterpolator for step-wise changes.
   * </p>
   * @return a ChannelBuilder for objects
   * @param <T> the type of objects in the channel
   */
  public static <T> ChannelBuilder<T> objects() {
    return new ChannelBuilder<>(StepKeyframeInterpolator.instance());
  }

  /**
   * Creates a builder for a PropertyChannel of Doubles.
   * <p>
   * Defaults to using {@link DoubleKeyframeInterpolator} for smooth numeric interpolation.
   * </p>
   * @return a ChannelBuilder for Doubles
   */
  public static ChannelBuilder<Double> doubles() {
    return new ChannelBuilder<>(DoubleKeyframeInterpolator.instance());
  }

  /**
   * Creates a builder for a PropertyChannel of Integers.
   * Defaults to using {@link IntegerKeyframeInterpolator} for smooth numeric interpolation.
   * @return a ChannelBuilder for Integers
   */
  public static ChannelBuilder<Integer> integers() {
    return new ChannelBuilder<>(IntegerKeyframeInterpolator.instance());
  }

  /**
   * Creates a builder for a PropertyChannel of Vector3d.
   * Defaults to using {@link Vector3dKeyframeInterpolator} for smooth vector interpolation.
   * @return a ChannelBuilder for Vector3d
   */
  public static ChannelBuilder<Vector3d> vector3d() {
    return new ChannelBuilder<>(Vector3dKeyframeInterpolator.instance());
  }

  /**
   * Creates a builder for a PropertyChannel of Quaterniond.
   * Defaults to using {@link QuaterniondKeyframeInterpolator} for smooth quaternion interpolation.
   * @return a ChannelBuilder for Quaterniond
   */
  public static ChannelBuilder<Quaterniond> quaterniond() {
    return new ChannelBuilder<>(QuaterniondKeyframeInterpolator.instance());
  }

  /**
   * Creates a builder for a PropertyChannel of generic objects with a custom interpolator.
   * @param interpolator the KeyframeInterpolator to use for this channel
   * @param <T> the type of objects in the channel
   * @return a ChannelBuilder for objects of type T
   */
  public static <T> ChannelBuilder<T> of(KeyframeInterpolator<T> interpolator) {
    return new ChannelBuilder<>(interpolator);
  }

  /**
   * Sets the pre-behavior for the channel.
   * @param behavior the {@link Extrapolation} behavior to apply before the first keyframe
   * @return this builder for chaining
   */
  public ChannelBuilder<T> preBehavior(Extrapolation behavior) {
    delegate.preBehavior(behavior);
    return this;
  }

  /**
   * Sets the post-behavior for the channel.
   * @param behavior the {@link Extrapolation} behavior to apply after the last keyframe
   * @return this builder for chaining
   */
  public ChannelBuilder<T> postBehavior(Extrapolation behavior) {
    delegate.postBehavior(behavior);
    return this;
  }

  /**
   * Adds a keyframe to the channel.
   * @param keyframe the Keyframe to add
   * @return this builder for chaining
   */
  public ChannelBuilder<T> add(Keyframe<T> keyframe) {
    delegate.add(keyframe);
    return this;
  }

  /**
   * Adds multiple keyframes to the channel.
   * @param keyframes an Iterable of Keyframes to add
   * @return this builder for chaining
   */
  public ChannelBuilder<T> addAll(Iterable<Keyframe<T>> keyframes) {
    for (Keyframe<T> keyframe : keyframes) {
      delegate.add(keyframe);
    }
    return this;
  }

  /**
   * Adds a keyframe at the specified time with the given value.
   * @param timeSeconds the time in seconds for the keyframe
   * @param value the value of the keyframe
   * @return this builder for chaining
   */
  public ChannelBuilder<T> add(double timeSeconds, T value) {
    return add(Keyframe.of(timeSeconds, value));
  }

  /**
   * Adds a keyframe at the specified time, configured via the provided Consumer.
   * @param timeSeconds the time in seconds for the keyframe
   * @param configurer a Consumer that configures a Keyframe.Builder
   * @return this builder for chaining
   */
  public ChannelBuilder<T> keyframe(double timeSeconds, Consumer<Keyframe.Builder<T>> configurer) {
    var builder = Keyframe.<T>at(timeSeconds);
    configurer.accept(builder);
    return add(builder.build());
  }

  /**
   * Builds the PropertyChannel with the configured keyframes and behaviors.
   * @return the constructed PropertyChannel
   */
  public PropertyChannel<T> build() {
    return delegate.build();
  }
}
