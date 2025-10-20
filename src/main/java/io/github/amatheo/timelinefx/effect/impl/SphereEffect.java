package io.github.amatheo.timelinefx.effect.impl;

import io.github.amatheo.timelinefx.effect.AnimatedEffect;
import io.github.amatheo.timelinefx.effect.EffectSamplingContext;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import io.github.amatheo.timelinefx.annotation.AnimatedProperty;
import io.github.amatheo.timelinefx.effect.*;
import io.github.amatheo.timelinefx.particle.ParticleBuffer;
import org.joml.Vector3d;

import java.util.Objects;

/**
 * Sphere surface made of particles.
 */
public final class SphereEffect extends AnimatedEffect {


  @AnimatedProperty
  public ParticleType particleType;

  @AnimatedProperty(defaultValue = "1.0")
  public Double radius;

  @AnimatedProperty(defaultValue = "16")
  public Integer rings;

  @AnimatedProperty(defaultValue = "32")
  public Integer segments;

  public SphereEffect(ParticleType particleType) {
    this.particleType = Objects.requireNonNull(particleType, "particleType");
  }

  public SphereEffect(){
  }

  @Override
  protected void render(EffectSamplingContext ctx, ParticleBuffer out) {
    if (radius <= 0.0) {
      return;
    }
    out.ensureCapacity(rings * segments);

    for (int ring = 0; ring <= rings; ring++) {
      double v = (double) ring / rings;
      double phi = Math.PI * v;
      double sinPhi = Math.sin(phi);
      double cosPhi = Math.cos(phi);

      for (int segment = 0; segment < segments; segment++) {
        double u = (double) segment / segments;
        double theta = 2.0 * Math.PI * u;
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);

        double x = radius * sinPhi * cosTheta;
        double y = radius * cosPhi;
        double z = radius * sinPhi * sinTheta;
        out.add(new Vector3d(x, y, z), particleType);
      }
    }
  }
}
