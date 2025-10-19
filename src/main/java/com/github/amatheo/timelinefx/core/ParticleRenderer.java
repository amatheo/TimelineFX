package com.github.amatheo.timelinefx.core;

import com.github.amatheo.timelinefx.particle.ParticleBuffer;
import com.github.amatheo.timelinefx.transform.Transform;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Service for rendering particle buffers into the world.
 */
public interface ParticleRenderer {
  /**
   * Renders particles from a buffer to players, applying a spatial transformation.
   *
   * @param buffer The buffer containing local particle data.
   * @param transform The transformation to apply to the particles.
   * @param players The list of players who will see the particles.
   */
  void render(ParticleBuffer buffer, Transform transform, List<Player> players);
}

