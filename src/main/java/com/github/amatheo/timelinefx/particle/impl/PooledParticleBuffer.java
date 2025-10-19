package com.github.amatheo.timelinefx.particle.impl;

import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import com.github.amatheo.timelinefx.particle.ParticleBuffer;
import com.github.amatheo.timelinefx.particle.ParticleVertex;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PooledParticleBuffer implements ParticleBuffer {
  private final ArrayList<ParticleVertex> pool;
  private int activeParticles = 0;

  public PooledParticleBuffer(int initialCapacity){
    this.pool = new ArrayList<>(Math.max(16, initialCapacity));
    // Pre-allocate the pool
    for (int i = 0; i < Math.max(16, initialCapacity); i++) {
      this.pool.add(new ParticleVertex());
    }
  }

  @Override public void clear(){
    activeParticles = 0;
  }

  @Override
  public void add(Vector3d pos, ParticleType particleType){
    ParticleVertex vertex = getNext();
    vertex.getPos().set(pos);
    vertex.setParticleType(particleType);
    vertex.setVel(null);
    vertex.setParticleData(null);
  }

  @Override
  public void add(Vector3d pos, ParticleType particleType, Vector3d velocity){
    ParticleVertex vertex = getNext();
    vertex.getPos().set(pos);
    vertex.setParticleType(particleType);
    vertex.setVel(velocity);
    vertex.setParticleData(null);
  }

  /**
   * Get the next available particle from the pool.
   * Expands the pool if needed.
   * @return A reusable ParticleVertex from the pool
   */
  public ParticleVertex getNext() {
    if (activeParticles >= pool.size()) {
      // Expand the pool if necessary
      pool.add(new ParticleVertex());
    }
    return pool.get(activeParticles++);
  }

  @Override
  public int size(){
    return activeParticles;
  }

  @Override public List<ParticleVertex> view(){
    return Collections.unmodifiableList(pool.subList(0, activeParticles));
  }

  @Override
  public void ensureCapacity(int minCapacity) {
    while (pool.size() < minCapacity) {
      pool.add(new ParticleVertex());
    }
  }
}