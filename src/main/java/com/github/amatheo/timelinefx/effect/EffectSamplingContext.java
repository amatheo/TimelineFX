package com.github.amatheo.timelinefx.effect;

import java.util.Random;

public record EffectSamplingContext(long tick, double dtSeconds, Random rng) {}
