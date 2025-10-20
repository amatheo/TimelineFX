package io.github.amatheo.timelinefx.effect;

import io.github.amatheo.timelinefx.particle.ParticleBuffer;

public interface Effect {
    void sample(EffectSamplingContext ctx, EvaluatedParams params, ParticleBuffer outBuffer);
}