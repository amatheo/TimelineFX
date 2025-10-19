package com.github.amatheo.timelinefx.animation.timeline.builder;

import com.github.amatheo.timelinefx.animation.ChannelBuilder;
import com.github.amatheo.timelinefx.animation.PropertyChannel;
import com.github.amatheo.timelinefx.animation.timeline.TimelineProperty;
import com.github.amatheo.timelinefx.animation.timeline.TimelineTrack;
import org.joml.Vector3d;

import java.util.function.Consumer;

public final class Vector3TrackBuilder implements TrackBuilder<Vector3d, Vector3TrackBuilder> {
  private final TimelineProperty<Vector3d> property;
  private final TimelineTrack.Builder<Vector3d> trackBuilder = new TimelineTrack.Builder<>();

  public Vector3TrackBuilder(TimelineProperty<Vector3d> property) {
    this.property = property;
  }

  @Override
  public Vector3TrackBuilder segment(double startSeconds, PropertyChannel<Vector3d> channel) {
    trackBuilder.add(startSeconds, channel);
    return this;
  }

  @Override
  public Vector3TrackBuilder segment(double startSeconds, Consumer<ChannelBuilder<Vector3d>> channelConfigurer) {
    ChannelBuilder<Vector3d> builder = ChannelBuilder.vector3d();
    channelConfigurer.accept(builder);
    return segment(startSeconds, builder.build());
  }

  @Override
  public TimelineProperty<Vector3d> property() {
    return property;
  }

  @Override
  public TimelineTrack<Vector3d> build() {
    return trackBuilder.build();
  }
}
