package io.github.amatheo.timelinefx.orchestration;

import io.github.amatheo.timelinefx.core.EffectClip;
import io.github.amatheo.timelinefx.core.Playable;
import io.github.amatheo.timelinefx.core.PlaybackContext;

import java.util.List;

/**
 * Sequentially executes a list of {@link Playable} units. Usually created through
 * {@link SequenceBuilder SequenceBuilder} so it can mix timeline-driven
 * {@link EffectClip} and {@link EffectClip}
 * instances alongside waits or callbacks.
 */
public final class Sequence implements Playable {
  private final List<Playable> list;
  private final Runnable onComplete;
  private int index = -1;
  private boolean completionFired;

  public Sequence(List<Playable> list) {
    this(list, null);
  }

  public Sequence(List<Playable> list, Runnable onComplete) {
    this.list = List.copyOf(list);
    this.onComplete = onComplete;
  }

  public void start(PlaybackContext ctx) {
    completionFired = false;
    index = 0;
    if (!list.isEmpty()) {
      list.getFirst().start(ctx);
    }
  }

  public void tick(PlaybackContext ctx) {
    if (index < 0 || index >= list.size()) return;
    var cur = list.get(index);
    cur.tick(ctx);
    if (cur.isDone()) {
      cur.stop(ctx);
      index++;
      if (index < list.size()) {
        list.get(index).start(ctx);
      } else {
        fireIfNeeded();
      }
    }
  }

  public boolean isDone() {
    return index >= list.size();
  }

  public void stop(PlaybackContext ctx) {
    if (index >= 0 && index < list.size()) list.get(index).stop(ctx);
    fireIfNeeded();
  }

  private void fireIfNeeded() {
    if (!completionFired && onComplete != null && index >= list.size()) {
      completionFired = true;
      onComplete.run();
    }
  }
}
