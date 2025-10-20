package io.github.amatheo.timelinefx.animation.timeline;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.amatheo.timelinefx.animation.ChannelBuilder;
import io.github.amatheo.timelinefx.animation.Keyframe;
import io.github.amatheo.timelinefx.animation.PropertyChannel;
import org.junit.jupiter.api.Test;

final class TimelineTrackTest {
  @Test
  void selectsSegmentBasedOnTimelineOffset() {
    PropertyChannel<Double> first = ChannelBuilder.doubles()
        .add(Keyframe.of(0.0, 0.0))
        .add(Keyframe.of(1.0, 10.0))
        .build();

    PropertyChannel<Double> second = ChannelBuilder.doubles()
        .add(Keyframe.of(0.0, 10.0))
        .add(Keyframe.of(1.0, 20.0))
        .build();

    TimelineTrack<Double> track = TimelineTrack.<Double>builder()
        .add(0.0, first)
        .add(1.0, second)
        .build();

    assertEquals(0.0, track.get(0L, -1.0), 1e-9);  // before first segment -> hold first value
    assertEquals(5.0, track.get(0L, 0.5), 1e-9);
    assertEquals(15.0, track.get(0L, 1.5), 1e-9);
    assertEquals(20.0, track.get(0L, 3.0), 1e-9);  // after last segment -> hold last value
  }
}
