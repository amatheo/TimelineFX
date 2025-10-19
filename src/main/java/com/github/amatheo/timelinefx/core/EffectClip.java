package com.github.amatheo.timelinefx.core;

import com.github.amatheo.timelinefx.animation.binding.BindingResult;
import com.github.amatheo.timelinefx.animation.binding.TimelineBindings;
import com.github.amatheo.timelinefx.animation.timeline.TimelinePlayback;
import com.github.amatheo.timelinefx.animation.timeline.TimelineSnapshot;
import com.github.amatheo.timelinefx.effect.Effect;
import com.github.amatheo.timelinefx.effect.EffectSamplingContext;
import com.github.amatheo.timelinefx.effect.EvaluatedParams;
import com.github.amatheo.timelinefx.particle.impl.PooledParticleBuffer;
import com.github.amatheo.timelinefx.transform.Transform;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Drives a single {@link TimelinePlayback} and fans out its values to multiple effect layers, each
 * with their own bindings. Useful for complex particle compositions that share a master timeline
 * (e.g. multi-ring explosions, spell charge-up sequences, layered auras).
 */
public final class EffectClip implements Playable {
  private final TimelinePlayback playback;
  private final TimelineBindings rootBindings;
  private final List<Layer> layers;
  private final List<Player> players;
  private long startedAtTick = -1L;

  private EffectClip(TimelinePlayback playback, TimelineBindings rootBindings,
                     List<Layer> layers, List<Player> players) {
    this.playback = Objects.requireNonNull(playback, "playback");
    this.rootBindings = Objects.requireNonNull(rootBindings, "rootBindings");
    this.layers = List.copyOf(layers);
    this.players = players == null ? List.of() : List.copyOf(players);
  }

  /**
   * Internal factory method for use by EffectClipBuilder.
   */
  public static EffectClip fromBuilder(TimelinePlayback playback, TimelineBindings rootBindings,
                                       List<Layer> layers, List<Player> players) {
    return new EffectClip(playback, rootBindings, layers, players);
  }

  public static EffectClipBuilder builder() {
    return new EffectClipBuilder();
  }

  @Override
  public void start(PlaybackContext ctx) {
    this.startedAtTick = ctx.nowTick().get();
    playback.start(startedAtTick);
  }

  @Override
  public void tick(PlaybackContext ctx) {
    if (startedAtTick < 0) return;

    long nowTick = ctx.nowTick().get();
    TimelineSnapshot snapshot = playback.sample(nowTick, ctx.tickToSeconds());
    if (snapshot == null) return;

    // Evaluate root (parent) transform
    BindingResult rootResult = rootBindings.evaluate(snapshot);
    Transform rootTransform = rootResult.transform();
    Map<String, Object> rootParams = rootResult.parameters();

    // Render each layer with the root transform as parent
    for (Layer layer : layers) {
      layer.render(ctx, snapshot, rootTransform, rootParams, players);
    }
  }

  @Override
  public boolean isDone() {
    return playback.isFinished();
  }

  @Override
  public void stop(PlaybackContext ctx) {
    this.startedAtTick = -1L;
  }

  static final class Layer {
    private final Effect effect;
    private final TimelineBindings bindings;
    private final List<Player> players;

    private final PooledParticleBuffer bufferA;
    private final PooledParticleBuffer bufferB;
    private PooledParticleBuffer writeBuffer;
    private PooledParticleBuffer readBuffer;

    private Layer(Effect effect, TimelineBindings bindings, int bufferCapacity, List<Player> players) {
      this.effect = Objects.requireNonNull(effect, "effect");
      this.bindings = Objects.requireNonNull(bindings, "bindings");
      this.players = players == null ? null : List.copyOf(players);

      this.bufferA = new PooledParticleBuffer(Math.max(16, bufferCapacity));
      this.bufferB = new PooledParticleBuffer(Math.max(16, bufferCapacity));
      this.writeBuffer = bufferA;
      this.readBuffer = bufferB;
    }

    /**
     * Internal factory method for use by LayerBuilder.
     */
    static Layer create(Effect effect, TimelineBindings bindings, int bufferCapacity, List<Player> players) {
      return new Layer(effect, bindings, bufferCapacity, players);
    }

    private void swapBuffers() {
      PooledParticleBuffer temp = readBuffer;
      readBuffer = writeBuffer;
      writeBuffer = temp;
    }

    private void render(PlaybackContext ctx, TimelineSnapshot snapshot,
                        Transform parentTransform, Map<String, Object> rootParams,
                        List<Player> defaultPlayers) {
      // 1. Evaluate local bindings
      BindingResult localResult = bindings.evaluate(snapshot);
      Transform localTransform = localResult.transform();

      // 2. Compose the world transform
      Transform worldTransform = Transform.compose(parentTransform, localTransform);

      // 3. Combine parent parameters with local parameters
      Map<String, Object> params;
      if (rootParams.isEmpty()) {
        params = localResult.parameters();
      } else {
        params = new HashMap<>(rootParams);
        params.putAll(localResult.parameters());
      }

      // 4. Sample effect and render
      EvaluatedParams evaluated = EvaluatedParams.fromValues(params);
      EffectSamplingContext samplingCtx = new EffectSamplingContext(ctx.nowTick().get(), ctx.tickToSeconds(), ctx.rng());
      effect.sample(samplingCtx, evaluated, writeBuffer);

      List<Player> audience = (players != null) ? players : defaultPlayers;
      ctx.renderer().render(readBuffer, worldTransform, audience);

      swapBuffers();
    }
  }
}
