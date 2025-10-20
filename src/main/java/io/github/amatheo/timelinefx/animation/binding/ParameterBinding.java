package io.github.amatheo.timelinefx.animation.binding;

import io.github.amatheo.timelinefx.animation.timeline.TimelineSnapshot;
import io.github.amatheo.timelinefx.animation.value.ValueProvider;

import java.util.Objects;

/**
 * Link a custom parameter to a ValueProvider.
 * This class is agnostic to the source of the value (constant or animated)
 */
final class ParameterBinding implements TimelineBinding {

  private final String name;
  private final ValueProvider<?> valueProvider;

  /**
   * Create a new ParameterBinding.
   *
   * @param name          The name of the parameter to bind. Must not be blank.
   * @param valueProvider The provider of the value. Must not be null.
   */
  ParameterBinding(String name, ValueProvider<?> valueProvider) {
    if (name.isBlank()) {
      throw new IllegalArgumentException("Parameter name must not be blank");
    }
    this.name = Objects.requireNonNull(name, "name");
    this.valueProvider = Objects.requireNonNull(valueProvider, "valueProvider");
  }

  @Override
  public void apply(TimelineSnapshot snapshot, MutableBindingState state) {
    Object value = valueProvider.get(snapshot);
    if (value != null) {
      state.setParameter(name, value);
    }
  }
}
