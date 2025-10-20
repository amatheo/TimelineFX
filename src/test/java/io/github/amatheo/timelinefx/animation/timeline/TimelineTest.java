package io.github.amatheo.timelinefx.animation.timeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.amatheo.timelinefx.animation.Easing;
import io.github.amatheo.timelinefx.animation.Keyframe;
import org.junit.jupiter.api.Test;

class TimelineTest {
  @Test
  void builderCreatesMultipleTracksAndSamplesValues() {
    TimelineProperty<Double> radius = TimelineProperty.of("radius");
    TimelineProperty<Double> points = TimelineProperty.of("points");

    Timeline timeline = Timeline.builder()
        .doubles(radius, track ->
            track.segment(0.0, channel -> {
              channel.add(Keyframe.of(0.0, 0.0));
              channel.add(Keyframe.of(5.0, 5.0));
            })
        )
        .doubles(points, track ->
            track.segment(0.0, channel -> {
              channel.add(Keyframe.of(0.0, 40.0));
              channel.keyframe(5.0, k -> {
                k.value(40.0);
                k.easing(Easing.EASE_IN_OUT);
              });
              channel.add(Keyframe.of(7.0, 100.0));
            })
        )
        .build();

    TimelineSnapshot snapshot = timeline.get(0L, 2.5);
    assertEquals(2.5, snapshot.get(radius), 1e-9);

    double pointsAt2_5 = snapshot.get(points);
    // halfway between 40 and 40 with ease in-out is still 40
    assertEquals(40.0, pointsAt2_5, 1e-9);

    TimelineSnapshot snapshotLate = timeline.get(0L, 8.0);
    assertEquals(5.0, snapshotLate.get(radius), 1e-9);
    assertEquals(100.0, snapshotLate.get(points), 1e-9);
  }

  @Test
  void builderSupportsMultipleSegmentsPerTrack() {
    TimelineProperty<Double> radius = TimelineProperty.of("radius");

    Timeline timeline = Timeline.builder()
        .doubles(radius, track ->
            track
                .segment(0.0, channel -> {
                  channel.add(Keyframe.of(0.0, 0.0));
                  channel.add(Keyframe.of(1.0, 1.0));
                })
                .segment(1.0, channel -> {
                  channel.add(Keyframe.of(0.0, 1.0));
                  channel.keyframe(1.0, k -> {
                    k.value(2.0);
                    k.easing(Easing.EASE_OUT);
                  });
                })
        )
        .build();

    TimelineSnapshot snapshot = timeline.get(0L, 1.5);
    assertEquals(1.5, snapshot.get(radius), 1e-6);
  }

  @Test
  void builderSupportsBooleanProperties() {
    TimelineProperty<Boolean> enabled = TimelineProperty.of("enabled");

    Timeline timeline = Timeline.builder()
        .booleans(enabled, track ->
            track.segment(0.0, channel -> {
              channel.add(Keyframe.of(0.0, Boolean.FALSE));
              channel.add(Keyframe.of(1.0, Boolean.TRUE));
            })
        )
        .build();

    TimelineSnapshot beforeSwitch = timeline.get(0L, 0.5);
    assertFalse(beforeSwitch.get(enabled));

    TimelineSnapshot atSwitch = timeline.get(0L, 1.0);
    assertTrue(atSwitch.get(enabled));

    TimelineSnapshot afterSwitch = timeline.get(0L, 2.0);
    assertTrue(afterSwitch.get(enabled));
  }

  @Test
  void builderSupportsObjectProperties() {
    TimelineProperty<String> particle = TimelineProperty.of("particle");

    Timeline timeline = Timeline.builder()
        .objects(particle, track ->
            track.segment(0.0, channel -> {
              channel.add(Keyframe.of(0.0, "spark"));
              channel.add(Keyframe.of(2.0, "smoke"));
            })
        )
        .build();

    TimelineSnapshot duringFirst = timeline.get(0L, 1.0);
    assertEquals("spark", duringFirst.get(particle));

    TimelineSnapshot atTransition = timeline.get(0L, 2.0);
    assertEquals("smoke", atTransition.get(particle));

    TimelineSnapshot afterTransition = timeline.get(0L, 3.0);
    assertEquals("smoke", afterTransition.get(particle));
  }
}
