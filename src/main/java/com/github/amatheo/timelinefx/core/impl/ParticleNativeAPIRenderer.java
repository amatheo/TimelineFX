package com.github.amatheo.timelinefx.core.impl;

import com.github.fierioziy.particlenativeapi.api.packet.ParticlePacket;
import com.github.amatheo.timelinefx.core.ParticleRenderer;
import com.github.amatheo.timelinefx.particle.ParticleBuffer;
import com.github.amatheo.timelinefx.particle.ParticleVertex;
import com.github.amatheo.timelinefx.transform.Transform;
import org.bukkit.entity.Player;
import org.joml.Vector3d;

import java.util.List;

public class ParticleNativeAPIRenderer implements ParticleRenderer {
  // Pre-allocate objects to reuse across ticks and avoid GC pressure
  private final Vector3d worldPos = new Vector3d();
  private final Vector3d scaled = new Vector3d();
  private final Vector3d rotated = new Vector3d();

  @Override
  public void render(ParticleBuffer buffer, Transform transform, List<Player> players) {
    if (buffer.size() == 0) return;

    for (ParticleVertex vertex : buffer.view()) {
      applyTransformToVertex(vertex, transform, worldPos);
      ParticlePacket packet = vertex.getParticleType().packet(false, worldPos.x(), worldPos.y(), worldPos.z());
      sendPacket(packet, players);
    }
  }

  /**
   * Sends a particle packet to the specified players.
   */
  private void sendPacket(ParticlePacket packet, List<Player> players) {
    if (players != null && !players.isEmpty()) {
      packet.sendTo(players);
    }
  }

  /**
   * Apply transform to vertex and store result in outWorldPos.
   * Uses member variables to avoid allocations.
   * @param vertex The particle vertex to transform
   * @param t The transform to apply
   * @param outWorldPos Output parameter for the world position
   */
  private void applyTransformToVertex(ParticleVertex vertex, Transform t, Vector3d outWorldPos) {
    Vector3d local = vertex.getPos();
    // Scale: local * scale -> scaled
    local.mul(t.scale(), scaled);
    // Rotate: rotation.transform(scaled) -> rotated
    t.rotation().transform(scaled, rotated);
    // Translate: position + rotated -> outWorldPos
    t.position().add(rotated, outWorldPos);
  }

}
