package io.github.amatheo.timelinefx.animation;

public interface Animatable<T> {
    T get(long tick, double tSeconds);
}