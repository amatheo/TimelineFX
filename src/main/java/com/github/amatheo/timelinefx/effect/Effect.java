package com.github.amatheo.timelinefx.effect;

import com.github.amatheo.timelinefx.particle.ParticleBuffer;

public interface Effect {
    void sample(EffectSamplingContext ctx, EvaluatedParams params, ParticleBuffer outBuffer);
}