package io.github.amatheo.timelinefx.orchestration;

import io.github.amatheo.timelinefx.core.Playable;
import io.github.amatheo.timelinefx.core.PlaybackContext;
import org.bukkit.Bukkit;

public final class Callback implements Playable {
  private final Runnable r;
  private final boolean sync;
  private boolean done;

  public static Callback async(Runnable r) {
    return new Callback(r, false);
  }

  public static Callback sync(Runnable r) {
    return new Callback(r, true);
  }

  private Callback(Runnable r, boolean sync) {
    this.r = r;
    this.sync = sync;
  }

  public void start(PlaybackContext ctx) {
    if (sync) {
      // For sync actions, schedule on Bukkit's main thread
      Bukkit.getScheduler().runTask(ctx.plugin(), r);
    } else {
      // For async actions, execute directly on animation thread
      r.run();
    }
    // Callback is marked done immediately; sequence can continue without waiting
    done = true;
  }

  public void tick(PlaybackContext ctx) {
  }

  public boolean isDone() {
    return done;
  }

  public void stop(PlaybackContext ctx) {
  }
}