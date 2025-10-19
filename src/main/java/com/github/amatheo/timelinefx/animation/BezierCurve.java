package com.github.amatheo.timelinefx.animation;

import org.joml.Vector2d;

public final class BezierCurve implements TemporalCurve {

    private final Vector2d p1;
    private final Vector2d p2;

    /**
     * Creates a cubic Bézier curve for easing.
     * Control points P0 (0,0) and P3 (1,1) are implicit.
     *
     * @param p1x X coordinate of the first control point.
     * @param p1y Y coordinate of the first control point.
     * @param p2x X coordinate of the second control point.
     * @param p2y Y coordinate of the second control point.
     */
    public BezierCurve(double p1x, double p1y, double p2x, double p2y) {
        this.p1 = new Vector2d(clamp01(p1x), p1y);
        this.p2 = new Vector2d(clamp01(p2x), p2y);
    }

    @Override
    public double map(double progress) {
        if (progress <= 0.0) return 0.0;
        if (progress >= 1.0) return 1.0;

        double t = solveTforX(progress);
        return cubicBezier(t, 0, p1.y, p2.y, 1.0);
    }

    private double cubicBezier(double t, double c0, double c1, double c2, double c3) {
        double t2 = t * t;
        double t3 = t2 * t;
        double mt = 1.0 - t;
        double mt2 = mt * mt;
        double mt3 = mt2 * mt;
        return c0 * mt3 + 3.0 * c1 * mt2 * t + 3.0 * c2 * mt * t2 + c3 * t3;
    }

    // Solves for t given x using Newton-Raphson method
    private double solveTforX(double x) {
        double t = x; // Initial estimate
        for (int i = 0; i < 8; i++) {
            double x_t = cubicBezier(t, 0, p1.x, p2.x, 1.0) - x;
            double dx_dt = 3.0 * (1.0 - t) * (1.0 - t) * (p1.x - 0.0)
                         + 6.0 * (1.0 - t) * t * (p2.x - p1.x)
                         + 3.0 * t * t * (1.0 - p2.x);
            if (Math.abs(dx_dt) < 1e-6) break;
            double dt = x_t / dx_dt;
            t -= dt;
        }
        return t;
    }

    private static double clamp01(double value) {
        if (value < 0.0) return 0.0;
        if (value > 1.0) return 1.0;
        return value;
    }
}
