package com.github.amatheo.timelinefx.effect.impl;

import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import com.github.amatheo.timelinefx.annotation.AnimatedProperty;
import com.github.amatheo.timelinefx.effect.AnimatedEffect;
import com.github.amatheo.timelinefx.effect.EffectSamplingContext;
import com.github.amatheo.timelinefx.particle.ParticleBuffer;

public final class DebugEffect extends AnimatedEffect {

  @AnimatedProperty
  private ParticleType xParticleType;

  @AnimatedProperty
  private ParticleType yParticleType;

  @AnimatedProperty
  private ParticleType zParticleType;

  public DebugEffect(ParticleType xParticleType, ParticleType yParticleType, ParticleType zParticleType) {
    this.xParticleType = xParticleType;
    this.yParticleType = yParticleType;
    this.zParticleType = zParticleType;
  }

  @Override
  protected void render(EffectSamplingContext ctx, ParticleBuffer out) {
    // Axe X en rouge
    for (double x = 0; x <= 1.0; x += 0.1) {
      out.add(new org.joml.Vector3d(x, 0, 0), xParticleType);
    }
    // Axe Y en vert
    for (double y = 0; y <= 1.0; y += 0.1) {
      out.add(new org.joml.Vector3d(0, y, 0), yParticleType);
    }
    // Axe Z en bleu
    for (double z = 0; z <= 1.0; z += 0.1) {
      out.add(new org.joml.Vector3d(0, 0, z), zParticleType);
    }
  }
}
