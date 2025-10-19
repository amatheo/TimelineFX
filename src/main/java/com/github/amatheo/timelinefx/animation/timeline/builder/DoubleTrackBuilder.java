package com.github.amatheo.timelinefx.animation.timeline.builder;

import com.github.amatheo.timelinefx.animation.ChannelBuilder;
import com.github.amatheo.timelinefx.animation.PropertyChannel;
import com.github.amatheo.timelinefx.animation.timeline.TimelineProperty;
import com.github.amatheo.timelinefx.animation.timeline.TimelineTrack;

import java.util.function.Consumer;

public final class DoubleTrackBuilder implements TrackBuilder<Double, DoubleTrackBuilder> {
  private final TimelineProperty<Double> property;
  private final TimelineTrack.Builder<Double> trackBuilder = new TimelineTrack.Builder<>();

  public DoubleTrackBuilder(TimelineProperty<Double> property) {
    this.property = property;
  }

  @Override
  public DoubleTrackBuilder segment(double startSeconds, PropertyChannel<Double> channel) {
    trackBuilder.add(startSeconds, channel);
    return this;
  }

  @Override
  public DoubleTrackBuilder segment(double startSeconds, Consumer<ChannelBuilder<Double>> channelConfigurer) {
    ChannelBuilder<Double> builder = ChannelBuilder.doubles();
    channelConfigurer.accept(builder);
    return segment(startSeconds, builder.build());
  }

  @Override
  public TimelineProperty<Double> property() {
    return property;
  }

  @Override
  public TimelineTrack<Double> build() {
    return trackBuilder.build();
  }
}
