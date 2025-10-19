package com.github.amatheo.timelinefx.animation.timeline.builder;

import com.github.amatheo.timelinefx.animation.ChannelBuilder;
import com.github.amatheo.timelinefx.animation.PropertyChannel;
import com.github.amatheo.timelinefx.animation.timeline.TimelineProperty;
import com.github.amatheo.timelinefx.animation.timeline.TimelineTrack;

import java.util.function.Consumer;

public final class IntegerTrackBuilder implements TrackBuilder<Integer, IntegerTrackBuilder> {
  private final TimelineProperty<Integer> property;
  private final TimelineTrack.Builder<Integer> trackBuilder = new TimelineTrack.Builder<>();

  public IntegerTrackBuilder(TimelineProperty<Integer> property) {
    this.property = property;
  }

  @Override
  public IntegerTrackBuilder segment(double startSeconds, PropertyChannel<Integer> channel) {
    trackBuilder.add(startSeconds, channel);
    return this;
  }

  @Override
  public IntegerTrackBuilder segment(double startSeconds, Consumer<ChannelBuilder<Integer>> channelConfigurer) {
    ChannelBuilder<Integer> builder = ChannelBuilder.integers();
    channelConfigurer.accept(builder);
    return segment(startSeconds, builder.build());
  }

  @Override
  public TimelineProperty<Integer> property() {
    return property;
  }

  @Override
  public TimelineTrack<Integer> build() {
    return trackBuilder.build();
  }
}
