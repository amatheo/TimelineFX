package io.github.amatheo.timelinefx.effect;

import io.github.amatheo.timelinefx.annotation.AnimatedProperty;
import io.github.amatheo.timelinefx.particle.ParticleBuffer;

/**
 * Base class for effects using {@link AnimatedProperty} annotations.
 * Automatically binds annotated fields before rendering.
 *
 * <p>Rendering occurs when properties change, context is used, or on first sample.
 * This ensures constructor-initialized properties render correctly.
 */
public abstract class AnimatedEffect implements Effect {

  private boolean hasRenderedOnce = false;

  @Override
  public final void sample(EffectSamplingContext ctx, EvaluatedParams params, ParticleBuffer outBuffer) {
    boolean propsChanged = PropertyBinder.bindPropertiesAndDetectChanges(this, params);
    boolean shouldRender = propsChanged || usesContext() || !hasRenderedOnce;
    if (!shouldRender) {
      return;
    }
    outBuffer.clear();
    render(ctx, outBuffer);
    hasRenderedOnce = true;
  }

  /**
   * Renders the effect with populated {@link AnimatedProperty} fields.
   */
  protected abstract void render(EffectSamplingContext ctx, ParticleBuffer out);

  /**
   * Override to return true if this effect uses sampling context for dynamic behavior.
   */
  protected boolean usesContext() {
    return false;
  }
}
