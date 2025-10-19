package com.github.amatheo.timelinefx.effect;

import com.github.amatheo.timelinefx.annotation.AnimatedProperty;
import com.github.amatheo.timelinefx.particle.ParticleBuffer;
import com.github.amatheo.timelinefx.particle.impl.PooledParticleBuffer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that validate explicit parameter passing through the Effect interface.
 * These tests ensure that parameters flow correctly through method signatures
 * without relying on ThreadLocal context.
 */
class ExplicitParamPassingTest {

  @Test
  void effectReceivesExplicitParams() {
    // Create a test effect that captures the params it receives
    TestEffect effect = new TestEffect();
    
    // Create params with test data
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("testValue", 42.0);
    paramMap.put("testString", "hello");
    EvaluatedParams params = EvaluatedParams.fromValues(paramMap);
    
    // Create context and buffer
    EffectSamplingContext ctx = new EffectSamplingContext(0L, 0.05, new Random(123));
    ParticleBuffer buffer = new PooledParticleBuffer(16);
    
    // Call sample with explicit params
    effect.sample(ctx, params, buffer);
    
    // Verify the effect received the params
    assertNotNull(effect.receivedParams, "Effect should receive params");
    assertEquals(42.0, effect.receivedParams.getDouble("testValue", 0.0), 
        "Effect should receive correct parameter values");
    assertEquals("hello", effect.receivedParams.raw("testString"),
        "Effect should receive correct parameter values");
  }

  @Test
  void animatedEffectBindsPropertiesFromExplicitParams() {
    // Create an animated effect with properties
    TestAnimatedEffect effect = new TestAnimatedEffect();
    
    // Create params with values for the animated properties
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("radius", 5.0);
    paramMap.put("count", 10);
    EvaluatedParams params = EvaluatedParams.fromValues(paramMap);
    
    // Create context and buffer
    EffectSamplingContext ctx = new EffectSamplingContext(0L, 0.05, new Random(123));
    ParticleBuffer buffer = new PooledParticleBuffer(16);
    
    // Verify initial values (should be defaults)
    assertNull(effect.radius, "radius should initially be null");
    assertNull(effect.count, "count should initially be null");
    
    // Call sample with explicit params
    effect.sample(ctx, params, buffer);
    
    // Verify properties were bound from explicit params
    assertEquals(5.0, effect.radius, 0.001, "radius should be bound from params");
    assertEquals(10, effect.count, "count should be bound from params");
  }

  @Test
  void propertyBinderWorksWithExplicitParams() {
    // Create an effect with animated properties
    TestAnimatedEffect effect = new TestAnimatedEffect();
    
    // Create params
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("radius", 3.5);
    paramMap.put("count", 20);
    EvaluatedParams params = EvaluatedParams.fromValues(paramMap);
    
    // Bind properties directly
    boolean changed = PropertyBinder.bindPropertiesAndDetectChanges(effect, params);
    
    // Verify binding worked
    assertTrue(changed, "Properties should be detected as changed");
    assertEquals(3.5, effect.radius, 0.001, "radius should be bound");
    assertEquals(20, effect.count, "count should be bound");
    
    // Bind again with same values
    changed = PropertyBinder.bindPropertiesAndDetectChanges(effect, params);
    
    // Should not detect changes on second bind
    assertFalse(changed, "Properties should not be detected as changed when values are same");
  }

  @Test
  void emptyParamsUsesDefaultValues() {
    // Create an animated effect with default values
    TestAnimatedEffect effect = new TestAnimatedEffect();
    
    // Create empty params
    EvaluatedParams params = EvaluatedParams.fromValues(Map.of());
    
    // Create context and buffer
    EffectSamplingContext ctx = new EffectSamplingContext(0L, 0.05, new Random(123));
    ParticleBuffer buffer = new PooledParticleBuffer(16);
    
    // Call sample - should apply defaults
    effect.sample(ctx, params, buffer);
    
    // Verify defaults were applied
    assertEquals(1.0, effect.radius, 0.001, "radius should use default value");
    assertEquals(5, effect.count, "count should use default value");
  }

  /**
   * Test effect that captures the params it receives
   */
  private static class TestEffect implements Effect {
    EvaluatedParams receivedParams;

    @Override
    public void sample(EffectSamplingContext ctx, EvaluatedParams params, ParticleBuffer outBuffer) {
      this.receivedParams = params;
    }
  }

  /**
   * Test animated effect with properties
   */
  private static class TestAnimatedEffect extends AnimatedEffect {
    @AnimatedProperty(defaultValue = "1.0")
    public Double radius;

    @AnimatedProperty(defaultValue = "5")
    public Integer count;

    @Override
    protected void render(EffectSamplingContext ctx, ParticleBuffer out) {
      // No-op for testing
    }
  }
}
