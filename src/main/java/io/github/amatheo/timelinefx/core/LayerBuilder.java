package io.github.amatheo.timelinefx.core;

import io.github.amatheo.timelinefx.animation.binding.TimelineBindings;
import io.github.amatheo.timelinefx.animation.binding.TimelineBindingsBuilder;
import io.github.amatheo.timelinefx.effect.Effect;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builder for creating individual layers within an {@link EffectClip}.
 * Each layer has its own effect, bindings, and optional player targeting.
 */
public final class LayerBuilder {
  private Effect effect;
  private TimelineBindings bindings;
  private int bufferCapacity = 1024;
  private List<Player> players;

  public LayerBuilder effect(Effect effect) {
    this.effect = effect;
    return this;
  }

  public LayerBuilder bindings(TimelineBindings bindings) {
    this.bindings = bindings;
    return this;
  }

  public LayerBuilder bindings(Consumer<TimelineBindingsBuilder> configurer) {
    TimelineBindingsBuilder builder = TimelineBindings.builder();
    configurer.accept(builder);
    this.bindings = builder.build();
    return this;
  }

  public LayerBuilder bufferCapacity(int bufferCapacity) {
    this.bufferCapacity = bufferCapacity;
    return this;
  }

  public LayerBuilder players(List<Player> players) {
    this.players = players;
    return this;
  }

  public LayerBuilder addPlayer(Player player) {
    if (this.players == null) {
      this.players = new ArrayList<>();
    } else if (!(this.players instanceof ArrayList<?>)) {
      this.players = new ArrayList<>(this.players);
    }
    this.players.add(player);
    return this;
  }

  public EffectClip.Layer build() {
    Effect resolvedEffect = Objects.requireNonNull(effect, "effect");
    TimelineBindings effectiveBindings = (bindings != null)
        ? bindings
        : TimelineBindings.builder().build();
    return EffectClip.Layer.create(resolvedEffect, effectiveBindings, bufferCapacity, players);
  }
}
