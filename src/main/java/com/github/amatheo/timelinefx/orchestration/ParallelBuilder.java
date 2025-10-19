package com.github.amatheo.timelinefx.orchestration;

import com.github.amatheo.timelinefx.core.EffectClip;
import com.github.amatheo.timelinefx.core.EffectClipBuilder;
import com.github.amatheo.timelinefx.core.Playable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Builder for creating {@link Parallel} instances with multiple playables
 * executed concurrently.
 */
public final class ParallelBuilder {
  private final List<Playable> children = new ArrayList<>();
  private Runnable onComplete;

  public ParallelBuilder add(Playable playable) {
    children.add(playable);
    return this;
  }

  public ParallelBuilder sequence(Consumer<SequenceBuilder> configurer) {
    SequenceBuilder sequenceBuilder = new SequenceBuilder();
    configurer.accept(sequenceBuilder);
    children.add(sequenceBuilder.build());
    return this;
  }

  public ParallelBuilder clip(Consumer<EffectClipBuilder> configurer) {
    EffectClipBuilder builder = EffectClip.builder();
    configurer.accept(builder);
    children.add(builder.build());
    return this;
  }

  public ParallelBuilder parallel(Consumer<ParallelBuilder> nested) {
    ParallelBuilder nestedBuilder = new ParallelBuilder();
    nested.accept(nestedBuilder);
    children.add(nestedBuilder.build());
    return this;
  }

  public ParallelBuilder waitTicks(long ticks) {
    children.add(new WaitTicks(ticks));
    return this;
  }

  /**
   * Add a callback that will be executed asynchronously on the same thread as the AnimationEngine.
   * Ideal for non-Bukkit actions (logging, modifying variables, etc.).
   */
  public ParallelBuilder callbackAsync(Runnable r) {
    children.add(Callback.async(r));
    return this;
  }

  /**
   * Add a callback that will be executed synchronously on the main server thread.
   * Ideal for Bukkit actions (modifying the world, entities, etc.).
   */
  public ParallelBuilder callbackSync(Runnable r) {
    children.add(Callback.sync(r));
    return this;
  }

  public ParallelBuilder onComplete(Runnable runnable) {
    this.onComplete = runnable;
    return this;
  }

  public Parallel build() {
    return Parallel.fromBuilder(children, onComplete);
  }
}
