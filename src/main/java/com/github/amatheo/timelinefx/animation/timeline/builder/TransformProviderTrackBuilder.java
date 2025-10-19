package com.github.amatheo.timelinefx.animation.timeline.builder;

import com.github.amatheo.timelinefx.animation.ChannelBuilder;
import com.github.amatheo.timelinefx.animation.PropertyChannel;
import com.github.amatheo.timelinefx.animation.target.TransformProvider;
import com.github.amatheo.timelinefx.animation.timeline.TimelineProperty;
import com.github.amatheo.timelinefx.animation.timeline.TimelineTrack;

import java.util.function.Consumer;

public class TransformProviderTrackBuilder implements TrackBuilder<TransformProvider, TransformProviderTrackBuilder> {
  private final TimelineProperty<TransformProvider> property;
  private final TimelineTrack.Builder<TransformProvider> trackBuilder = new TimelineTrack.Builder<>();

  public TransformProviderTrackBuilder(TimelineProperty<TransformProvider> property) {
    this.property = property;
  }

  @Override
  public TransformProviderTrackBuilder segment(double startSeconds, PropertyChannel<TransformProvider> channel) {
    trackBuilder.add(startSeconds, channel);
    return null;
  }

  @Override
  public TransformProviderTrackBuilder segment(double startSeconds, Consumer<ChannelBuilder<TransformProvider>> channelConfigurer) {
    ChannelBuilder<TransformProvider> builder = ChannelBuilder.transformProvider();
    channelConfigurer.accept(builder);
    return segment(startSeconds, builder.build());
  }

  @Override
  public TimelineProperty<TransformProvider> property() {
    return property;
  }

  @Override
  public TimelineTrack<TransformProvider> build() {
    return trackBuilder.build();
  }
}
