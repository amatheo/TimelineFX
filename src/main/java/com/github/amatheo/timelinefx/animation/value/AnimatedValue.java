package com.github.amatheo.timelinefx.animation.value;

import com.github.amatheo.timelinefx.animation.timeline.TimelineProperty;
import com.github.amatheo.timelinefx.animation.timeline.TimelineSnapshot;
import org.jetbrains.annotations.Nullable;

/**
 * A ValueProvider that retrieves its value from a TimelineSnapshot using a TimelineProperty.
 * @param <T> the type of the value
 */
public final class AnimatedValue<T> implements ValueProvider<T> {
    private final TimelineProperty<T> property;

    @Nullable
    private final T defaultValue;

    public AnimatedValue(TimelineProperty<T> property, @Nullable T defaultValue) {
        this.property = property;
        this.defaultValue = defaultValue;
    }

    public AnimatedValue(TimelineProperty<T> property) {
      this.property = property;
      this.defaultValue = null;
    }

    @Override
    public T get(TimelineSnapshot snapshot) {
        if (snapshot == null) return defaultValue;
        return snapshot.getOrDefault(property, defaultValue);
    }
}