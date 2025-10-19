package com.github.amatheo.timelinefx.animation.timeline.builder;

import com.github.amatheo.timelinefx.animation.ChannelBuilder;
import com.github.amatheo.timelinefx.animation.PropertyChannel;
import com.github.amatheo.timelinefx.animation.timeline.TimelineProperty;
import com.github.amatheo.timelinefx.animation.timeline.TimelineTrack;

import java.util.function.Consumer;

public final class BooleanTrackBuilder implements TrackBuilder<Boolean, BooleanTrackBuilder> {
  private final TimelineProperty<Boolean> property;
  private final TimelineTrack.Builder<Boolean> trackBuilder = new TimelineTrack.Builder<>();

  public BooleanTrackBuilder(TimelineProperty<Boolean> property) {
    this.property = property;
  }

  @Override
  public BooleanTrackBuilder segment(double startSeconds, PropertyChannel<Boolean> channel) {
    trackBuilder.add(startSeconds, channel);
    return this;
  }

  @Override
  public BooleanTrackBuilder segment(double startSeconds,
                                     Consumer<ChannelBuilder<Boolean>> channelConfigurer) {
    ChannelBuilder<Boolean> builder = ChannelBuilder.booleans();
    channelConfigurer.accept(builder);
    return segment(startSeconds, builder.build());
  }

  @Override
  public TimelineProperty<Boolean> property() {
    return property;
  }

  @Override
  public TimelineTrack<Boolean> build() {
    return trackBuilder.build();
  }
}
