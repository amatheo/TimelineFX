package com.github.amatheo.timelinefx.orchestration;

import com.github.amatheo.timelinefx.core.Playable;
import com.github.amatheo.timelinefx.core.PlaybackContext;

import java.util.List;

/**
 * Executes several {@link Playable} instances concurrently. Build instances through
 * {@link ParallelBuilder ParallelBuilder} to compose clips, timeline groups, nested sequences or other parallel
 * blocks while keeping a single completion callback.
 *
 */
public final class Parallel implements Playable {
  private final List<Playable> children;
  private final Runnable onComplete;
  private boolean completionFired;

  public Parallel(List<Playable> children) {
    this(children, null);
  }

  public Parallel(List<Playable> children, Runnable onComplete) {
    this.children = List.copyOf(children);
    this.onComplete = onComplete;
  }

  /**
   * Internal factory method for use by ParallelBuilder.
   */
  public static Parallel fromBuilder(List<Playable> children, Runnable onComplete) {
    return new Parallel(children, onComplete);
  }

  public static ParallelBuilder builder() {
    return new ParallelBuilder();
  }

  public void start(PlaybackContext ctx) {
    completionFired = false;
    children.forEach(p -> p.start(ctx));
  }

  public void tick(PlaybackContext ctx) {
    children.forEach(child -> child.tick(ctx));
    fireIfNeeded();
  }

  public boolean isDone() {
    return children.stream().allMatch(Playable::isDone);
  }

  public void stop(PlaybackContext ctx) {
    children.forEach(p -> p.stop(ctx));
    fireIfNeeded();
  }

  private void fireIfNeeded() {
    if (!completionFired && onComplete != null && children.stream().allMatch(Playable::isDone)) {
      completionFired = true;
      onComplete.run();
    }
  }
}
