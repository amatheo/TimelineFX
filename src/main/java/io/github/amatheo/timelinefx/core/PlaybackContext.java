package io.github.amatheo.timelinefx.core;

import org.bukkit.plugin.Plugin;

import java.util.Random;
import java.util.function.Supplier;

public record PlaybackContext(
    Plugin plugin,
    long startTick,
    Supplier<Long> nowTick,
    double tickToSeconds,
    ParticleRenderer renderer,
    Random rng
) {}