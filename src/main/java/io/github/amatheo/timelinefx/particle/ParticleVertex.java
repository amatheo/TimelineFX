package io.github.amatheo.timelinefx.particle;

import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

/** Particle vertex containing spawn data (position, type, velocity). */
public class ParticleVertex {

  private Vector3d pos;
  private ParticleType particleType;
  @Nullable
  private Vector3d vel;
  @Nullable
  private Object particleData;

  public ParticleVertex(Vector3d pos, ParticleType particleType, @Nullable Vector3d vel, @Nullable Object particleData) {
    this.pos = pos;
    this.vel = vel;
    this.particleType = particleType;
    this.particleData = particleData;
  }

  public ParticleVertex(Vector3d pos, ParticleType particleType, @Nullable Vector3d vel) {
    this(pos, particleType, vel, null);
  }

  public ParticleVertex(Vector3d pos, ParticleType particleType) {
    this(pos, particleType, null, null);
  }

  /**
   * Default constructor for object pooling.
   */
  public ParticleVertex() {
    this.pos = new Vector3d();
    this.vel = null;
    this.particleType = null;
    this.particleData = null;
  }

  public Vector3d getPos() {
    return pos;
  }

  public void setPos(Vector3d pos) {
    this.pos = pos;
  }

  public ParticleType getParticleType() {
    return particleType;
  }

  public void setParticleType(ParticleType particleType) {
    this.particleType = particleType;
  }

  public @Nullable Vector3d getVel() {
    return vel;
  }

  public void setVel(@Nullable Vector3d vel) {
    this.vel = vel;
  }

  public @Nullable Object getParticleData() {
    return particleData;
  }

  public void setParticleData(@Nullable Object particleData) {
    this.particleData = particleData;
  }
}