package com.github.amatheo.timelinefx.core;

import com.github.amatheo.timelinefx.animation.binding.TimelineBindings;
import com.github.amatheo.timelinefx.animation.binding.TimelineBindingsBuilder;
import com.github.amatheo.timelinefx.animation.timeline.Timeline;
import com.github.amatheo.timelinefx.animation.timeline.TimelinePlayback;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builder for creating {@link EffectClip} instances with multiple layers,
 * timeline playback configuration, and player targeting.
 */
public final class EffectClipBuilder {
  private Timeline timeline;
  private TimelinePlayback playback;
  private final List<Consumer<TimelinePlayback.Builder>> playbackConfigurators = new ArrayList<>();
  private TimelineBindings rootBindings;
  private final List<LayerBuilder> layerBuilders = new ArrayList<>();
  private List<Player> players;

  public EffectClipBuilder timeline(Timeline timeline) {
    this.timeline = timeline;
    return this;
  }

  public EffectClipBuilder playback(TimelinePlayback playback) {
    this.playback = playback;
    return this;
  }

  public EffectClipBuilder configurePlayback(Consumer<TimelinePlayback.Builder> configurer) {
    this.playbackConfigurators.add(configurer);
    return this;
  }

  public EffectClipBuilder rootBindings(TimelineBindings bindings) {
    this.rootBindings = bindings;
    return this;
  }

  public EffectClipBuilder rootBindings(Consumer<TimelineBindingsBuilder> configurer) {
    TimelineBindingsBuilder builder = TimelineBindings.builder();
    configurer.accept(builder);
    this.rootBindings = builder.build();
    return this;
  }

  public EffectClipBuilder layer(Consumer<LayerBuilder> configurer) {
    LayerBuilder builder = new LayerBuilder();
    configurer.accept(builder);
    layerBuilders.add(builder);
    return this;
  }

  public EffectClipBuilder players(List<Player> players) {
    this.players = players;
    return this;
  }

  public EffectClipBuilder addPlayer(Player player) {
    if (this.players == null) {
      this.players = new ArrayList<>();
    } else if (!(this.players instanceof ArrayList<?>)) {
      this.players = new ArrayList<>(this.players);
    }
    this.players.add(player);
    return this;
  }

  public EffectClip build() {
    if (layerBuilders.isEmpty()) {
      throw new IllegalStateException("EffectClip requires at least one layer");
    }

    TimelineBindings effectiveRoot = (rootBindings != null)
        ? rootBindings
        : TimelineBindings.builder().build();

    TimelinePlayback resolvedPlayback = resolvePlayback();
    List<EffectClip.Layer> layers = new ArrayList<>(layerBuilders.size());
    for (LayerBuilder builder : layerBuilders) {
      layers.add(builder.build());
    }
    return EffectClip.fromBuilder(resolvedPlayback, effectiveRoot, layers, players);
  }

  private TimelinePlayback resolvePlayback() {
    if (playback != null) {
      return playback;
    }
    Objects.requireNonNull(timeline, "timeline");
    TimelinePlayback.Builder builder = TimelinePlayback.builder(timeline);
    for (Consumer<TimelinePlayback.Builder> configurer : playbackConfigurators) {
      configurer.accept(builder);
    }
    return builder.build();
  }
}
