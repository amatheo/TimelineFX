package io.github.amatheo.timelinefx.orchestration;

import io.github.amatheo.timelinefx.core.Playable;
import io.github.amatheo.timelinefx.core.PlaybackContext;

public final class WaitTicks implements Playable {
  private final long ticks;
  private long start;
  private PlaybackContext context;
  public WaitTicks(long ticks) {
    this.ticks = ticks;
  }

  public void start(PlaybackContext ctx) {
    start = ctx.nowTick().get();
    this.context = ctx;
  }

  public void tick(PlaybackContext ctx) {
  }

  public boolean isDone() {
    if (start < 0) return false;
    long now = context.nowTick().get();
    return (now - start) >= ticks;
  }

 public void stop(PlaybackContext ctx){

 }
}