package com.github.amatheo.timelinefx.core;

public interface Playable {
    void start(PlaybackContext ctx);
    void tick(PlaybackContext ctx);
    boolean isDone();
    void stop(PlaybackContext ctx);
}