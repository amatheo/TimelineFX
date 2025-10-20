package io.github.amatheo.timelinefx.animation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PropertyChannelDoubleTest {
  @Test
  void linearInterpolationProducesExpectedValue() {
    var channel = ChannelBuilder.doubles()
        .add(Keyframe.of(0.0, 0.0))
        .add(Keyframe.of(1.0, 10.0))
        .build();

    assertEquals(5.0, channel.get(0L, 0.5), 1e-9);
  }

  @Test
  void hermiteInterpolationUsesTangentsWhenProvided() {
    var channel = ChannelBuilder.doubles()
        .keyframe(0.0, k -> {
          k.value(0.0);
          k.outTangent(2.0); // accelerate quickly at the start
        })
        .keyframe(1.0, k -> {
          k.value(1.0);
          k.inTangent(0.0);
        })
        .build();

    assertEquals(0.75, channel.get(0L, 0.5), 1e-6);
  }
}
