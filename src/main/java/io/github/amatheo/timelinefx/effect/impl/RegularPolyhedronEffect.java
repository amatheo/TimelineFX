package io.github.amatheo.timelinefx.effect.impl;

import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import io.github.amatheo.timelinefx.annotation.AnimatedProperty;
import io.github.amatheo.timelinefx.effect.AnimatedEffect;
import io.github.amatheo.timelinefx.effect.EffectSamplingContext;
import io.github.amatheo.timelinefx.particle.ParticleBuffer;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Wireframe renderer for any of the five Platonic solids.
 * Renders particles along the edges of the selected solid.
 */
public final class RegularPolyhedronEffect extends AnimatedEffect {


  @AnimatedProperty
  public ParticleType particleType;

  /** Points sampled per edge (>= 2). */
  @AnimatedProperty(defaultValue = "16")
  public Integer pointsPerEdge;

  /** Solid to render. */
  @AnimatedProperty
  public Solid solid = Solid.TETRAHEDRON;

  public enum Solid { TETRAHEDRON, CUBE, OCTAHEDRON, DODECAHEDRON, ICOSAHEDRON }


  public RegularPolyhedronEffect(ParticleType particleType) {
    this.particleType = Objects.requireNonNull(particleType, "particleType");
  }

  public RegularPolyhedronEffect() {
  }


  @Override
  protected void render(EffectSamplingContext ctx, ParticleBuffer buffer) {
    if (particleType == null) return; // nothing to render
    final Mesh mesh = buildUnitEdgeMesh(solid);

    final int P = Math.max(2, pointsPerEdge);
    final double step = 1.0 / (P - 1);

    for (int[] e : mesh.edges) {
      final Vector3d a = new Vector3d(mesh.vertices[e[0]]);
      final Vector3d b = new Vector3d(mesh.vertices[e[1]]);
      for (int i = 0; i < P; i++) {
        double t = i * step;
        Vector3d p = new Vector3d(a).lerp(b, t);
        buffer.add(p, particleType);
      }
    }
  }

  private static Mesh buildUnitEdgeMesh(Solid solid) {
    Vector3d[] v;
    switch (solid) {
      case TETRAHEDRON -> v = vertsTetra();
      case CUBE -> v = vertsCube();
      case OCTAHEDRON -> v = vertsOcta();
      case DODECAHEDRON -> v = vertsDodeca();
      case ICOSAHEDRON -> v = vertsIcosa();
      default -> throw new IllegalArgumentException("Unknown solid: " + solid);
    }

    // Infer edges by shortest pairwise distance (i.e., edge length), then normalize edge to 1
    double min = Double.POSITIVE_INFINITY;
    for (int i = 0; i < v.length; i++) {
      for (int j = i + 1; j < v.length; j++) {
        double d = v[i].distance(v[j]);
        if (d > 1e-9 && d < min) min = d;
      }
    }
    final double edge = min;
    final double invEdge = 1.0 / edge;
    for (Vector3d p : v) p.mul(invEdge);


    // collect edges with ~unit length after normalization
    List<int[]> edges = new ArrayList<>();
    for (int i = 0; i < v.length; i++) {
      for (int j = i + 1; j < v.length; j++) {
        double d = v[i].distance(v[j]);
        if (Math.abs(d - 1.0) < 1e-6) edges.add(new int[]{i, j});
      }
    }
    return new Mesh(v, edges.toArray(new int[0][]));
  }


  private record Mesh(Vector3d[] vertices, int[][] edges) {}


  // === Canonical vertex sets ===
  private static Vector3d[] vertsTetra() {
    return new Vector3d[]{
        new Vector3d( 1, 1, 1),
        new Vector3d( 1, -1, -1),
        new Vector3d(-1, 1, -1),
        new Vector3d(-1, -1, 1)
    };
  }


  private static Vector3d[] vertsCube() {
    Vector3d[] v = new Vector3d[8];
    int k = 0;
    for (int sx = -1; sx <= 1; sx += 2)
      for (int sy = -1; sy <= 1; sy += 2)
        for (int sz = -1; sz <= 1; sz += 2)
          v[k++] = new Vector3d(sx, sy, sz);
    return v;
  }


  private static Vector3d[] vertsOcta() {
    return new Vector3d[]{
        new Vector3d( 1, 0, 0), new Vector3d(-1, 0, 0),
        new Vector3d( 0, 1, 0), new Vector3d( 0,-1, 0),
        new Vector3d( 0, 0, 1), new Vector3d( 0, 0,-1)
    };
  }


  private static Vector3d[] vertsIcosa() {
    // 12 vertices: permutations of (0, ±1, ±phi)
    final double phi = (1.0 + Math.sqrt(5.0)) * 0.5; // golden ratio
    List<Vector3d> v = new ArrayList<>(12);
    double[] s = new double[]{-1, 1};
    for (double a : s) for (double b : s) v.add(new Vector3d(0, a, b * phi));
    for (double a : s) for (double b : s) v.add(new Vector3d(a, b * phi, 0));
    for (double a : s) for (double b : s) v.add(new Vector3d(a * phi, 0, b));
    return v.toArray(new Vector3d[0]);
  }


  private static Vector3d[] vertsDodeca() {
    // 20 vertices: (±1,±1,±1) and cyclic perms of (0, ±1/phi, ±phi), (±1/phi, ±phi, 0), (±phi, 0, ±1/phi)
    final double phi = (1.0 + Math.sqrt(5.0)) * 0.5;
    final double inv = 1.0 / phi;
    List<Vector3d> v = new ArrayList<>(20);
    // (±1, ±1, ±1)
    for (int sx = -1; sx <= 1; sx += 2)
      for (int sy = -1; sy <= 1; sy += 2)
        for (int sz = -1; sz <= 1; sz += 2)
          v.add(new Vector3d(sx, sy, sz));
    // (0, ±1/phi, ±phi)
    for (int s1 = -1; s1 <= 1; s1 += 2)
      for (int s2 = -1; s2 <= 1; s2 += 2)
        v.add(new Vector3d(0, s1 * inv, s2 * phi));
    // (±1/phi, ±phi, 0)
    for (int s1 = -1; s1 <= 1; s1 += 2)
      for (int s2 = -1; s2 <= 1; s2 += 2)
        v.add(new Vector3d(s1 * inv, s2 * phi, 0));
    // (±phi, 0, ±1/phi)
    for (int s1 = -1; s1 <= 1; s1 += 2)
      for (int s2 = -1; s2 <= 1; s2 += 2)
        v.add(new Vector3d(s1 * phi, 0, s2 * inv));
    return v.toArray(new Vector3d[0]);
  }
}
