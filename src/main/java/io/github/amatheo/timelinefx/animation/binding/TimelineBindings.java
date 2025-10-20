package io.github.amatheo.timelinefx.animation.binding;

import io.github.amatheo.timelinefx.animation.target.TransformProvider;
import io.github.amatheo.timelinefx.animation.timeline.TimelineSnapshot;
import io.github.amatheo.timelinefx.animation.value.ValueProvider;
import io.github.amatheo.timelinefx.transform.Transform;

import java.util.List;

public final class TimelineBindings {
  private final Transform transform;
  private final ValueProvider<TransformProvider> anchorProvider;
  private final List<TimelineBinding> bindings;

  private TimelineBindings(Transform transform, ValueProvider<TransformProvider> anchorProvider, List<TimelineBinding> bindings) {
    this.transform = transform;
    this.anchorProvider = anchorProvider;
    this.bindings = List.copyOf(bindings);
  }

  /**
   * Internal factory method for use by TimelineBindingsBuilder.
   */
  public static TimelineBindings fromBuilder(Transform transform, ValueProvider<TransformProvider> anchorProvider, List<TimelineBinding> bindings) {
    return new TimelineBindings(transform, anchorProvider, bindings);
  }

  public BindingResult evaluate(TimelineSnapshot snapshot) {
    Transform seed;

    TransformProvider currentProvider = (anchorProvider != null)
        ? anchorProvider.get(snapshot)
        : null;

    if (currentProvider != null) {
      Transform dynamicTransform = currentProvider.getTransform();
      // If the provider returns null, we ignore it and use only the base setTransform.
      seed = (dynamicTransform != null)
          ? Transform.compose(transform, dynamicTransform)
          : transform;
    } else {
      seed = transform;
    }

    MutableBindingState state = new MutableBindingState(seed);
    for (TimelineBinding binding : bindings) {
      binding.apply(snapshot, state);
    }
    return state.toResult();
  }

  public static TimelineBindingsBuilder builder() {
    return new TimelineBindingsBuilder();
  }
}
