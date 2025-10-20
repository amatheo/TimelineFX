package io.github.amatheo.timelinefx.animation.binding;

import io.github.amatheo.timelinefx.animation.timeline.TimelineSnapshot;

/**
 * A binding that applies changes to a MutableBindingState based on a TimelineSnapshot.
 *
 * @see MutableBindingState
 * @see TimelineSnapshot
 */
interface TimelineBinding {
  void apply(TimelineSnapshot snapshot, MutableBindingState state);
}
