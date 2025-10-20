package io.github.amatheo.timelinefx.animation;

public enum Easing implements TemporalCurve {
  LINEAR {
    @Override public double map(double progress) {
      return progress;
    }
  },
  EASE_IN {
    @Override public double map(double progress) {
      return progress * progress;
    }
  },
  EASE_OUT {
    @Override public double map(double progress) {
      double inv = 1.0 - progress;
      return 1.0 - inv * inv;
    }
  },
  EASE_IN_OUT {
    @Override public double map(double progress) {
      if (progress < 0.5) {
        return 2.0 * progress * progress;
      }
      double inv = -2.0 * progress + 2.0;
      return 1.0 - (inv * inv) / 2.0;
    }
  };
}
