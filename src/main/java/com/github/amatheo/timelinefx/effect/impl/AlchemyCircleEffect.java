package com.github.amatheo.timelinefx.effect.impl;

import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import com.github.amatheo.timelinefx.annotation.AnimatedProperty;
import com.github.amatheo.timelinefx.effect.AnimatedEffect;
import com.github.amatheo.timelinefx.effect.EffectSamplingContext;
import com.github.amatheo.timelinefx.particle.ParticleBuffer;
import org.joml.Vector3d;

import java.util.Arrays;


/**
 * Generates a procedural alchemy circle from particles.
 * Uses a seed-based algorithm to create unique geometric patterns.
 */
public final class AlchemyCircleEffect extends AnimatedEffect {

  private static final int RESOLUTION = 128;
  private static final double SCALE_FACTOR = 1.0 / (RESOLUTION / 2.0);

  @AnimatedProperty
  public ParticleType particleType;

  /**
   * Seed for procedural generation. Changing the seed creates a different pattern.
   */
  @AnimatedProperty(defaultValue = "0")
  public Integer seed;

  /**
   * Particle density. 1.0 means every "lit" pixel becomes a particle.
   * Lower values reduce the number of particles.
   */
  @AnimatedProperty(defaultValue = "1.0")
  public Double density;

  private byte[][] pixelBuffer;
  private int lastSeed = Integer.MIN_VALUE;

  public AlchemyCircleEffect(ParticleType particleType) {
    this.particleType = particleType;
  }

  public AlchemyCircleEffect() {
  }

  @Override
  protected void render(EffectSamplingContext ctx, ParticleBuffer out) {
    // Regenerate buffer only when seed changes
    if (seed != lastSeed) {
      pixelBuffer = AlchemyCircleGenerator.generateBuffer(seed);
      lastSeed = seed;
    }

    if (pixelBuffer == null || particleType == null) {
      return;
    }

    double densityClamped = Math.max(0.0, Math.min(1.0, density));

    for (int y = 0; y < RESOLUTION; y++) {
      for (int x = 0; x < RESOLUTION; x++) {
        if (pixelBuffer[y][x] == AlchemyCircleGenerator.PIXEL_ON) {
          if (densityClamped >= 1.0 || ctx.rng().nextDouble() < densityClamped) {
            // Normalize coordinates to [-1, 1] and place on XZ plane
            double normX = (x - RESOLUTION / 2.0) * SCALE_FACTOR;
            double normZ = (y - RESOLUTION / 2.0) * SCALE_FACTOR;
            out.add(new Vector3d(normX, 0, normZ), particleType);
          }
        }
      }
    }
  }

  /**
   * Internal class encapsulating the alchemy circle generation logic.
   */
  private static class AlchemyCircleGenerator {
    public static final byte PIXEL_ON = 1;
    private static final int FIXED_SIZE = RESOLUTION;
    private static final int CENTER = FIXED_SIZE / 2;
    private static final double BASE_RADIUS = FIXED_SIZE * 0.375d;
    private static final int BASE_RADIUS_INT = (int) BASE_RADIUS;
    private static final byte PIXEL_OFF = 0;

    public static byte[][] generateBuffer(int seed) {
      byte[][] buffer = new byte[FIXED_SIZE][FIXED_SIZE];
      generate(seed, new BinaryDrawContext(buffer));
      return buffer;
    }

    private static void generate(int id, DrawContext draw) {
      CiaccoRandom randomer = new CiaccoRandom();
      randomer.setSeed(id);

      final double centerD = CENTER;
      final double radius = BASE_RADIUS;
      final int radiusInt = BASE_RADIUS_INT;

      draw.drawCircle(CENTER, CENTER, radiusInt);

      int lati = randomer.getRand(4, 8);
      final int initialRadialSides = lati;

      draw.drawPolygon(lati, radiusInt, 0);
      iterateAngles(initialRadialSides, 0.0, (cos, sin) -> {
        int x = (int) (centerD + radius * cos);
        int y = (int) (centerD + radius * sin);
        draw.drawLine(CENTER, CENTER, x, y);
      });
      int latis;
      if (lati % 2 == 0) {
        latis = randomer.getRand(2, 6);
        while (latis % 2 != 0) latis = randomer.getRand(3, 6);
        draw.drawFilledPolygon(latis, radiusInt, 180);
        final int chordSides = latis;
        iterateAngles(chordSides, 0.0, (cos, sin) -> {
          int x = (int) (centerD + radius * cos);
          int y = (int) (centerD + radius * sin);
          draw.drawLine(CENTER, CENTER, x, y);
        });
      } else {
        latis = randomer.getRand(2, 6);
        while (latis % 2 == 0) latis = randomer.getRand(3, 6);
        draw.drawFilledPolygon(latis, radiusInt, 180);
      }

      if (randomer.getRand(0, 1) % 2 == 0) {
        int ronad = randomer.getRand(0, 4);
        double starRadius = radius * 0.625d + 2.0d;
        if (ronad % 2 == 1) {
          final int starSides = lati + 4;
          iterateAngles(starSides, 0.0, (cos, sin) -> {
            int x = (int) (centerD + starRadius * cos);
            int y = (int) (centerD + starRadius * sin);
            draw.drawLine(CENTER, CENTER, x, y);
          });
          draw.drawFilledPolygon(lati + 4, (int) (radius / 2.0), 0);
        } else if (ronad % 2 == 0) {
          final int starSides = lati - 2;
          iterateAngles(starSides, 0.0, (cos, sin) -> {
            int x = (int) (centerD + starRadius * cos);
            int y = (int) (centerD + starRadius * sin);
            draw.drawLine(CENTER, CENTER, x, y);
          });
          draw.drawFilledPolygon(lati - 2, (int) (radius / 4.0), 0);
        }
      }

      if (randomer.getRand(0, 4) % 2 == 0) {
        int midCircleRadius = (int) ((radius / 16.0) * 11.0);
        draw.drawCircle(CENTER, CENTER, midCircleRadius);
        if (lati % 2 == 0) {
          latis = randomer.getRand(2, 8);
          while (latis % 2 != 0) latis = randomer.getRand(3, 8);
          draw.drawPolygon(latis, (int) ((radius / 3.0) * 2.0), 180);
        } else {
          latis = randomer.getRand(2, 8);
          while (latis % 2 == 0) latis = randomer.getRand(3, 8);
          draw.drawPolygon(latis, (int) ((radius / 3.0) * 2.0), 180);
        }
      }

      int caso = randomer.getRand(0, 3);
      if (caso == 0) {
        double ringRadius = radius * 11.0 / 18.0;
        int smallRadius = (int) ((radius / 44.0) * 6.0);
        iterateAngles(latis, 0.0, (cos, sin) -> {
          int px = (int) (centerD + ringRadius * cos);
          int py = (int) (centerD + ringRadius * sin);
          draw.drawFilledCircle(px, py, smallRadius);
        });
      } else if (caso == 1) {
        int smallRadius = (int) ((radius / 44.0) * 6.0);
        iterateAngles(latis, 0.0, (cos, sin) -> {
          int px = (int) (centerD + radius * cos);
          int py = (int) (centerD + radius * sin);
          draw.drawFilledCircle(px, py, smallRadius);
        });
      } else if (caso == 2) {
        int outer = (int) ((radius / 18.0) * 6.0);
        int inner = (int) ((radius / 22.0) * 6.0);
        draw.drawCircle(CENTER, CENTER, outer);
        draw.drawFilledCircle(CENTER, CENTER, inner);
      } else if (caso == 3) {
        double innerRadius = (radius / 3.0) * 2.0;
        iterateAngles(latis, 0.0, (cos, sin) -> {
          int innerX = (int) (centerD + innerRadius * cos);
          int innerY = (int) (centerD + innerRadius * sin);
          int outerX = (int) (centerD + radius * cos);
          int outerY = (int) (centerD + radius * sin);
          draw.drawLine(innerX, innerY, outerX, outerY);
        });
        if (latis != lati) {
          draw.drawFilledCircle(CENTER, CENTER, (int) ((radius / 3.0) * 2.0));
          lati = randomer.getRand(3, 6);
          draw.drawPolygon(lati, (int) ((radius / 4.0) * 5.0), 0);
          draw.drawPolygon(lati, (int) ((radius / 3.0) * 2.0), 180);
        }
      }
    }

    private static void iterateAngles(int sides, double rotationRad, AngleConsumer consumer) {
      if (sides <= 0) return;
      double angleStep = Math.PI * 2.0 / sides;
      double cosStep = Math.cos(angleStep);
      double sinStep = Math.sin(angleStep);
      double cos = Math.cos(rotationRad);
      double sin = Math.sin(rotationRad);
      for (int i = 0; i < sides; i++) {
        consumer.accept(cos, sin);
        double nextCos = cos * cosStep - sin * sinStep;
        double nextSin = sin * cosStep + cos * sinStep;
        cos = nextCos;
        sin = nextSin;
      }
    }

    @FunctionalInterface
    private interface AngleConsumer {
      void accept(double cos, double sin);
    }

    private interface DrawContext {
      void drawLine(int x0, int y0, int x1, int y1);

      void drawCircle(int cx, int cy, int radius);

      void drawFilledCircle(int cx, int cy, int radius);

      void drawPolygon(int sides, int radius, int rotationDeg);

      void drawFilledPolygon(int sides, int radius, int rotationDeg);
    }

    private static final class BinaryDrawContext implements DrawContext {
      private final byte[][] buffer;
      private final int width;
      private final int height;

      BinaryDrawContext(byte[][] buffer) {
        this.buffer = buffer;
        this.height = buffer.length;
        this.width = this.height > 0 ? buffer[0].length : 0;
      }

      @Override
      public void drawLine(int x0, int y0, int x1, int y1) {
        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = dx + dy, e2;
        while (true) {
          set(x0, y0, PIXEL_ON);
          if (x0 == x1 && y0 == y1) break;
          e2 = 2 * err;
          if (e2 >= dy) {
            err += dy;
            x0 += sx;
          }
          if (e2 <= dx) {
            err += dx;
            y0 += sy;
          }
        }
      }

      @Override
      public void drawCircle(int cx, int cy, int radius) {
        if (radius < 0) return;
        int x = radius, y = 0;
        int err = 1 - x;
        while (x >= y) {
          plotCirclePoints(cx, cy, x, y);
          y++;
          if (err < 0) err += 2 * y + 1;
          else {
            x--;
            err += 2 * (y - x) + 1;
          }
        }
      }

      @Override
      public void drawFilledCircle(int cx, int cy, int radius) {
        if (radius < 0) return;
        // Fill interior with PIXEL_OFF to clear
        for (int y = -radius; y <= radius; y++)
          for (int x = -radius; x <= radius; x++)
            if (x * x + y * y <= radius * radius) set(cx + x, cy + y, PIXEL_OFF);
        // Draw outline
        drawCircle(cx, cy, radius);
      }

      @Override
      public void drawPolygon(int sides, int radius, int rotationDeg) {
        if (sides < 3) return;
        int[] px = new int[sides];
        int[] py = new int[sides];
        computePolygonPoints(sides, radius, rotationDeg, px, py);
        for (int i = 0; i < sides; i++) {
          drawLine(px[i], py[i], px[(i + 1) % sides], py[(i + 1) % sides]);
        }
      }

      @Override
      public void drawFilledPolygon(int sides, int radius, int rotationDeg) {
        if (sides < 3) return;
        int[] px = new int[sides];
        int[] py = new int[sides];
        computePolygonPoints(sides, radius, rotationDeg, px, py);

        int minY = Arrays.stream(py).min().getAsInt();
        int maxY = Arrays.stream(py).max().getAsInt();

        // Fill interior with PIXEL_OFF
        for (int y = minY; y <= maxY; y++) {
          int[] nodeX = new int[sides];
          int nodes = 0;
          for (int i = 0, j = sides - 1; i < sides; j = i++) {
            if ((py[i] < y && py[j] >= y) || (py[j] < y && py[i] >= y)) {
              nodeX[nodes++] = (int) (px[i] + (double) (y - py[i]) / (py[j] - py[i]) * (px[j] - px[i]));
            }
          }
          Arrays.sort(nodeX, 0, nodes);
          for (int i = 0; i < nodes; i += 2) {
            if (i + 1 < nodes) {
              for (int x = nodeX[i]; x < nodeX[i + 1]; x++) set(x, y, PIXEL_OFF);
            }
          }
        }

        // Draw outline
        drawPolygon(sides, radius, rotationDeg);
      }

      private void plotCirclePoints(int cx, int cy, int x, int y) {
        set(cx + x, cy + y, PIXEL_ON);
        set(cx - x, cy + y, PIXEL_ON);
        set(cx + x, cy - y, PIXEL_ON);
        set(cx - x, cy - y, PIXEL_ON);
        set(cx + y, cy + x, PIXEL_ON);
        set(cx - y, cy + x, PIXEL_ON);
        set(cx + y, cy - x, PIXEL_ON);
        set(cx - y, cy - x, PIXEL_ON);
      }

      private void computePolygonPoints(int sides, int radius, int rotationDeg, int[] px, int[] py) {
        double angle = 2 * Math.PI / sides;
        double rotation = Math.toRadians(rotationDeg);
        for (int i = 0; i < sides; i++) {
          px[i] = (int) (CENTER + radius * Math.cos(i * angle + rotation));
          py[i] = (int) (CENTER + radius * Math.sin(i * angle + rotation));
        }
      }

      private void set(int x, int y, byte value) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
          buffer[y][x] = value;
        }
      }
    }

    private static class CiaccoRandom {
      private int superSeed = 0;

      public void setSeed(int seed) {
        superSeed = Math.abs(seed) % 9999999 + 1;
        for (int i = 0; i < 9; i++) superSeed = (superSeed * 125) % 2796203;
      }

      public int getRand(int min, int max) {
        superSeed = (superSeed * 125) % 2796203;
        return superSeed % (max - min + 1) + min;
      }
    }
  }
}
