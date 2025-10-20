package io.github.amatheo.timelinefx.effect.impl;

import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import io.github.amatheo.timelinefx.annotation.AnimatedProperty;
import io.github.amatheo.timelinefx.effect.AnimatedEffect;
import io.github.amatheo.timelinefx.effect.EffectSamplingContext;
import io.github.amatheo.timelinefx.particle.ParticleBuffer;
import org.joml.Vector3d;

/**
 * Flat circle made of particles.
 */
public final class CircleEffect extends AnimatedEffect {

  @AnimatedProperty
  public ParticleType particleType;

  @AnimatedProperty(defaultValue = "1.0")
  public Double radius;

  @AnimatedProperty(defaultValue = "64")
  public Integer points;

  public CircleEffect() {
  }

  public CircleEffect(ParticleType particleType){
    this.particleType = particleType;
  }

  @Override
  protected void render(EffectSamplingContext ctx, ParticleBuffer out) {
    out.ensureCapacity(points);
    for (int i = 0; i < points; i++) {
      double angle = (2 * Math.PI * i) / points;
      double x = Math.cos(angle) * radius;
      double z = Math.sin(angle) * radius;
      out.add(new Vector3d(x, 0, z), particleType);
    }
  }
}
