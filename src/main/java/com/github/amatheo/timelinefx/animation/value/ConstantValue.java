package com.github.amatheo.timelinefx.animation.value;

import com.github.amatheo.timelinefx.animation.timeline.TimelineSnapshot;

/**
 * A ValueProvider that always returns a constant value, regardless of the timeline snapshot.
 * @param <T> the type of the value
 */
public final class ConstantValue<T> implements ValueProvider<T> {
    private final T value;

    public ConstantValue(T value) {
        this.value = value;
    }

    @Override
    public T get(TimelineSnapshot snapshot) {
        return value;
    }
}