package io.github.amatheo.timelinefx.animation.timeline.builder;

import io.github.amatheo.timelinefx.animation.ChannelBuilder;
import io.github.amatheo.timelinefx.animation.PropertyChannel;
import io.github.amatheo.timelinefx.animation.timeline.TimelineProperty;
import io.github.amatheo.timelinefx.animation.timeline.TimelineTrack;
import org.joml.Quaterniond;

import java.util.function.Consumer;

public final class QuaternionTrackBuilder implements TrackBuilder<Quaterniond, QuaternionTrackBuilder> {
  private final TimelineProperty<Quaterniond> property;
  private final TimelineTrack.Builder<Quaterniond> trackBuilder = new TimelineTrack.Builder<>();

  public QuaternionTrackBuilder(TimelineProperty<Quaterniond> property) {
    this.property = property;
  }

  @Override
  public QuaternionTrackBuilder segment(double startSeconds, PropertyChannel<Quaterniond> channel) {
    trackBuilder.add(startSeconds, channel);
    return this;
  }

  @Override
  public QuaternionTrackBuilder segment(double startSeconds,
                                        Consumer<ChannelBuilder<Quaterniond>> channelConfigurer) {
    ChannelBuilder<Quaterniond> builder = ChannelBuilder.quaterniond();
    channelConfigurer.accept(builder);
    return segment(startSeconds, builder.build());
  }

  @Override
  public TimelineProperty<Quaterniond> property() {
    return property;
  }

  @Override
  public TimelineTrack<Quaterniond> build() {
    return trackBuilder.build();
  }
}
