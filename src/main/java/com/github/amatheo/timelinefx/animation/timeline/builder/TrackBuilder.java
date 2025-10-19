package com.github.amatheo.timelinefx.animation.timeline.builder;

import com.github.amatheo.timelinefx.animation.ChannelBuilder;
import com.github.amatheo.timelinefx.animation.PropertyChannel;
import com.github.amatheo.timelinefx.animation.timeline.TimelineProperty;
import com.github.amatheo.timelinefx.animation.timeline.TimelineTrack;

import java.util.function.Consumer;

/**
 * Common interface for all timeline track builders.
 * Defines the contract for building timeline tracks with type-safe property channels.
 *
 * @param <T> the type of values in the timeline track
 * @param <B> the concrete builder type for fluent API support
 */
public interface TrackBuilder<T, B extends TrackBuilder<T, B>> {
  
  /**
   * Adds a segment to the track starting at the specified time with a pre-built property channel.
   *
   * @param startSeconds the start time of the segment in seconds
   * @param channel the property channel defining the segment's behavior
   * @return this builder for method chaining
   */
  B segment(double startSeconds, PropertyChannel<T> channel);
  
  /**
   * Adds a segment to the track starting at the specified time, configuring the channel inline.
   *
   * @param startSeconds the start time of the segment in seconds
   * @param channelConfigurer a consumer that configures the channel builder
   * @return this builder for method chaining
   */
  B segment(double startSeconds, Consumer<ChannelBuilder<T>> channelConfigurer);
  
  /**
   * Returns the timeline property associated with this track builder.
   *
   * @return the timeline property
   */
  TimelineProperty<T> property();
  
  /**
   * Builds and returns the timeline track.
   *
   * @return the constructed timeline track
   */
  TimelineTrack<T> build();
}
