package com.github.amatheo.timelinefx.particle;

import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import org.joml.Vector3d;

import java.util.List;

/** Reusable buffer for accumulating particle spawn data. */
public interface ParticleBuffer {
  void clear();
  
  void add(Vector3d pos, ParticleType particleType);
  
  void add(Vector3d pos, ParticleType particleType, Vector3d velocity);

  int size();

  /** Returns a read-only view without copying. */
  List<ParticleVertex> view();

  /**
   * Make sure the backing array can hold at least minCapacity elements without resizing.
   * @param minCapacity the desired minimum capacity
   */
  void ensureCapacity(int minCapacity);
}