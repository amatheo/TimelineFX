package io.github.amatheo.timelinefx.orchestration;

import io.github.amatheo.timelinefx.core.EffectClip;
import io.github.amatheo.timelinefx.core.EffectClipBuilder;
import io.github.amatheo.timelinefx.core.Playable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class SequenceBuilder {
  private final List<Playable> items = new ArrayList<>();
  private Runnable onComplete;

  public SequenceBuilder then(Playable p) {
    items.add(p);
    return this;
  }

  public SequenceBuilder sequence(Consumer<SequenceBuilder> configurer) {
    SequenceBuilder nested = new SequenceBuilder();
    configurer.accept(nested);
    return then(nested.build());
  }

  public SequenceBuilder clip(Consumer<EffectClipBuilder> configurer) {
    EffectClipBuilder builder = EffectClip.builder();
    configurer.accept(builder);
    return then(builder.build());
  }

  public SequenceBuilder inParallel(Playable... ps) {
    items.add(new Parallel(List.of(ps)));
    return this;
  }

  public SequenceBuilder inParallel(Consumer<ParallelBuilder> configurer) {
    ParallelBuilder builder = Parallel.builder();
    configurer.accept(builder);
    items.add(builder.build());
    return this;
  }

  public SequenceBuilder waitTicks(long t) {
    items.add(new WaitTicks(t));
    return this;
  }

  /**
   * Add a callback that will be executed asynchronously on the same thread as the AnimationEngine.
   * Ideal for non-Bukkit actions (logging, modifying variables, etc.).
   */
  public SequenceBuilder callbackAsync(Runnable r) {
    items.add(Callback.async(r));
    return this;
  }

  /**
   * Add a callback that will be executed synchronously on the main server thread.
   * Ideal for Bukkit actions (modifying the world, entities, etc.).
   */
  public SequenceBuilder callbackSync(Runnable r) {
    items.add(Callback.sync(r));
    return this;
  }

  public SequenceBuilder onComplete(Runnable runnable) {
    this.onComplete = runnable;
    return this;
  }

  public Playable build() {
    return new Sequence(items, onComplete);
  }
}
