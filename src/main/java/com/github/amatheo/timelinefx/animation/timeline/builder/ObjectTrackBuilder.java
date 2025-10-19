package com.github.amatheo.timelinefx.animation.timeline.builder;

import com.github.amatheo.timelinefx.animation.ChannelBuilder;
import com.github.amatheo.timelinefx.animation.PropertyChannel;
import com.github.amatheo.timelinefx.animation.timeline.TimelineProperty;
import com.github.amatheo.timelinefx.animation.timeline.TimelineTrack;

import java.util.function.Consumer;

public final class ObjectTrackBuilder<T> implements TrackBuilder<T, ObjectTrackBuilder<T>> {
  private final TimelineProperty<T> property;
  private final TimelineTrack.Builder<T> trackBuilder = new TimelineTrack.Builder<>();

  public ObjectTrackBuilder(TimelineProperty<T> property) {
    this.property = property;
  }

  @Override
  public ObjectTrackBuilder<T> segment(double startSeconds, PropertyChannel<T> channel) {
    trackBuilder.add(startSeconds, channel);
    return this;
  }

  @Override
  public ObjectTrackBuilder<T> segment(double startSeconds,
                                       Consumer<ChannelBuilder<T>> channelConfigurer) {
    ChannelBuilder<T> builder = ChannelBuilder.objects();
    channelConfigurer.accept(builder);
    return segment(startSeconds, builder.build());
  }

  @Override
  public TimelineProperty<T> property() {
    return property;
  }

  @Override
  public TimelineTrack<T> build() {
    return trackBuilder.build();
  }
}
