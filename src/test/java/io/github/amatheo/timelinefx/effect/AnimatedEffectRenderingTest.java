package io.github.amatheo.timelinefx.effect;

import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import io.github.amatheo.timelinefx.annotation.AnimatedProperty;
import io.github.amatheo.timelinefx.particle.ParticleBuffer;
import io.github.amatheo.timelinefx.particle.impl.PooledParticleBuffer;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AnimatedEffect rendering behavior, particularly focusing on
 * the first-render guarantee when properties are initialized through constructors.
 */
class AnimatedEffectRenderingTest {

  @Test
  void effectRendersAtLeastOnceEvenWithNoParamChanges() {
    // Create an effect with constructor-initialized properties
    TestEffectWithConstructorProps effect = new TestEffectWithConstructorProps("initialValue");
    
    // Create empty params (no values to bind)
    EvaluatedParams params = EvaluatedParams.fromValues(Map.of());
    
    // Create context and buffer
    EffectSamplingContext ctx = new EffectSamplingContext(0L, 0.05, new Random(123));
    ParticleBuffer buffer = new PooledParticleBuffer(16);
    
    // First sample should always render
    effect.sample(ctx, params, buffer);
    assertEquals(1, effect.renderCount, "Effect should render on first sample");
    
    // Second sample with same (empty) params should NOT render (no changes)
    effect.sample(ctx, params, buffer);
    assertEquals(1, effect.renderCount, "Effect should not render again with no changes");
    
    // Sample with different params should render again
    EvaluatedParams newParams = EvaluatedParams.fromValues(Map.of("value", "newValue"));
    effect.sample(ctx, newParams, buffer);
    assertEquals(2, effect.renderCount, "Effect should render when params change");
  }

  @Test
  void debugEffectScenarioRendersCorrectly() {
    // This simulates the bug scenario: DebugEffect with constructor-initialized particle types
    TestDebugLikeEffect effect = new TestDebugLikeEffect(
        new MockParticleType("red"),
        new MockParticleType("green"),
        new MockParticleType("blue")
    );
    
    // In a sequence, bindings might not provide these parameters
    EvaluatedParams params = EvaluatedParams.fromValues(Map.of());
    
    EffectSamplingContext ctx = new EffectSamplingContext(0L, 0.05, new Random(123));
    ParticleBuffer buffer = new PooledParticleBuffer(16);
    
    // Should render on first call even though params are empty
    effect.sample(ctx, params, buffer);
    assertEquals(1, effect.renderCount, "DebugEffect should render on first sample");
    assertTrue(effect.lastRenderHadParticles(), 
        "DebugEffect should have access to constructor-initialized particle types");
  }

  @Test
  void effectWithUsesContextAlwaysRenders() {
    // Effects that use context should always render
    TestEffectWithContext effect = new TestEffectWithContext();
    
    EvaluatedParams params = EvaluatedParams.fromValues(Map.of());
    EffectSamplingContext ctx = new EffectSamplingContext(0L, 0.05, new Random(123));
    ParticleBuffer buffer = new PooledParticleBuffer(16);
    
    // Should render every time because usesContext() returns true
    effect.sample(ctx, params, buffer);
    assertEquals(1, effect.renderCount);
    
    effect.sample(ctx, params, buffer);
    assertEquals(2, effect.renderCount);
    
    effect.sample(ctx, params, buffer);
    assertEquals(3, effect.renderCount);
  }

  @Test
  void effectWithDefaultValuesRendersOnFirstSample() {
    // Effect with default values should render on first sample
    TestEffectWithDefaults effect = new TestEffectWithDefaults();
    
    EvaluatedParams params = EvaluatedParams.fromValues(Map.of());
    EffectSamplingContext ctx = new EffectSamplingContext(0L, 0.05, new Random(123));
    ParticleBuffer buffer = new PooledParticleBuffer(16);
    
    // First sample applies defaults and renders
    effect.sample(ctx, params, buffer);
    assertEquals(1, effect.renderCount, "Effect should render on first sample");
    assertEquals(10.0, effect.radius, 0.001, "Default value should be applied");
    
    // Second sample with no changes should not render
    effect.sample(ctx, params, buffer);
    assertEquals(1, effect.renderCount, "Effect should not render again with no changes");
  }

  @Test
  void propertyBindingStepsWorkCorrectly() {
    // Test that the 4-step binding process works as documented
    TestEffectWithMixedProps effect = new TestEffectWithMixedProps("initial");
    
    // Step 1-2: No params provided, should use defaults for unset fields
    EvaluatedParams emptyParams = EvaluatedParams.fromValues(Map.of());
    boolean changed = PropertyBinder.bindPropertiesAndDetectChanges(effect, emptyParams);
    assertTrue(changed, "Defaults should be applied to unset fields");
    assertEquals(5.0, effect.radius, 0.001, "Default radius should be applied");
    assertEquals("initial", effect.name, "Constructor value should be unchanged");
    
    // Step 3: Provide new values that differ from current
    EvaluatedParams newParams = EvaluatedParams.fromValues(Map.of(
        "radius", 15.0,
        "name", "updated"
    ));
    changed = PropertyBinder.bindPropertiesAndDetectChanges(effect, newParams);
    assertTrue(changed, "Changed values should be detected");
    assertEquals(15.0, effect.radius, 0.001, "Radius should be updated");
    assertEquals("updated", effect.name, "Name should be updated");
    
    // Step 4: Same values again should not trigger changes
    changed = PropertyBinder.bindPropertiesAndDetectChanges(effect, newParams);
    assertFalse(changed, "Same values should not trigger changes");
  }

  // Test effect with constructor-initialized properties
  private static class TestEffectWithConstructorProps extends AnimatedEffect {
    @AnimatedProperty
    private String value;
    
    int renderCount = 0;

    TestEffectWithConstructorProps(String value) {
      this.value = value;
    }

    @Override
    protected void render(EffectSamplingContext ctx, ParticleBuffer out) {
      renderCount++;
    }
  }

  // Test effect similar to DebugEffect
  private static class TestDebugLikeEffect extends AnimatedEffect {
    @AnimatedProperty
    private ParticleType xParticle;
    
    @AnimatedProperty
    private ParticleType yParticle;
    
    @AnimatedProperty
    private ParticleType zParticle;
    
    int renderCount = 0;

    TestDebugLikeEffect(ParticleType x, ParticleType y, ParticleType z) {
      this.xParticle = x;
      this.yParticle = y;
      this.zParticle = z;
    }

    @Override
    protected void render(EffectSamplingContext ctx, ParticleBuffer out) {
      renderCount++;
    }
    
    boolean lastRenderHadParticles() {
      return xParticle != null && yParticle != null && zParticle != null;
    }
  }

  // Test effect that uses context
  private static class TestEffectWithContext extends AnimatedEffect {
    int renderCount = 0;

    @Override
    protected void render(EffectSamplingContext ctx, ParticleBuffer out) {
      renderCount++;
    }

    @Override
    protected boolean usesContext() {
      return true;
    }
  }

  // Test effect with default values
  private static class TestEffectWithDefaults extends AnimatedEffect {
    @AnimatedProperty(defaultValue = "10.0")
    public Double radius;
    
    int renderCount = 0;

    @Override
    protected void render(EffectSamplingContext ctx, ParticleBuffer out) {
      renderCount++;
    }
  }

  // Test effect with mixed properties (constructor-initialized + defaults)
  private static class TestEffectWithMixedProps extends AnimatedEffect {
    @AnimatedProperty
    public String name;
    
    @AnimatedProperty(defaultValue = "5.0")
    public Double radius;

    TestEffectWithMixedProps(String name) {
      this.name = name;
    }

    @Override
    protected void render(EffectSamplingContext ctx, ParticleBuffer out) {
      // No-op
    }
  }

  // Mock ParticleType for testing
  private static class MockParticleType implements ParticleType {
    private final String name;

    MockParticleType(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return "MockParticle(" + name + ")";
    }
  }
}
