package com.github.amatheo.timelinefx.animation.target;

import com.github.amatheo.timelinefx.transform.Transform;
import org.joml.Quaterniond;
import org.joml.Vector3d;

/**
 * A TransformProvider that smoothly interpolates between two other TransformProviders.
 * This is used to create a seamless transition when the animation anchor changes.
 */
public final class InterpolatedTransformProvider implements TransformProvider {

    private final TransformProvider from;
    private final TransformProvider to;
    private final double progress;

    public InterpolatedTransformProvider(TransformProvider from, TransformProvider to, double progress) {
        this.from = from;
        this.to = to;
        this.progress = Math.max(0.0, Math.min(1.0, progress));
    }

    @Override
    public Transform getTransform() {
        Transform fromTransform = from.getTransform();
        Transform toTransform = to.getTransform();

        // If one of the providers is invalid, snap to the other one
        if (fromTransform == null && toTransform == null) {
            return null;
        }
        if (fromTransform == null) {
            return toTransform;
        }
        if (toTransform == null) {
            return fromTransform;
        }

        // Interpolate position, rotation, and scale
        Vector3d position = new Vector3d(fromTransform.position()).lerp(toTransform.position(), progress);
        Quaterniond rotation = new Quaterniond(fromTransform.rotation()).slerp(toTransform.rotation(), progress);
        Vector3d scale = new Vector3d(fromTransform.scale()).lerp(toTransform.scale(), progress);

        return new Transform(position, rotation, scale);
    }
}