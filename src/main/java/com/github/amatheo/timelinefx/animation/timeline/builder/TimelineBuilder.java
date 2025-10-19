package com.github.amatheo.timelinefx.animation.timeline.builder;

import com.github.amatheo.timelinefx.animation.target.TransformProvider;
import com.github.amatheo.timelinefx.animation.timeline.Timeline;
import com.github.amatheo.timelinefx.animation.timeline.TimelineProperty;
import com.github.amatheo.timelinefx.animation.timeline.TimelineTrack;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Builder for creating {@link Timeline} instances with type-safe track builders.
 * Provides methods for adding different types of animated properties (doubles, integers, vectors, etc.).
 */
public final class TimelineBuilder {
  private final List<TrackEntry<?>> entries = new ArrayList<>();

  public <T> TimelineBuilder track(TimelineProperty<T> property, TimelineTrack<T> track) {
    entries.add(new TrackEntry<>(property, track));
    return this;
  }

  public <T> TimelineBuilder objects(TimelineProperty<T> property,
                             Consumer<ObjectTrackBuilder<T>> configurer) {
    ObjectTrackBuilder<T> builder = new ObjectTrackBuilder<>(property);
    configurer.accept(builder);
    entries.add(new TrackEntry<>(builder.property(), builder.build()));
    return this;
  }

  public TimelineBuilder booleans(TimelineProperty<Boolean> property,
                          Consumer<BooleanTrackBuilder> configurer) {
    BooleanTrackBuilder builder = new BooleanTrackBuilder(property);
    configurer.accept(builder);
    entries.add(new TrackEntry<>(builder.property(), builder.build()));
    return this;
  }

  public TimelineBuilder integers(TimelineProperty<Integer> property, Consumer<IntegerTrackBuilder> configurer) {
    IntegerTrackBuilder builder = new IntegerTrackBuilder(property);
    configurer.accept(builder);
    entries.add(new TrackEntry<>(builder.property(), builder.build()));
    return this;
  }

  public TimelineBuilder doubles(TimelineProperty<Double> property, Consumer<DoubleTrackBuilder> configurer) {
    DoubleTrackBuilder builder = new DoubleTrackBuilder(property);
    configurer.accept(builder);
    entries.add(new TrackEntry<>(builder.property(), builder.build()));
    return this;
  }

  public TimelineBuilder vector3d(TimelineProperty<Vector3d> property, Consumer<Vector3TrackBuilder> configurer) {
    Vector3TrackBuilder builder = new Vector3TrackBuilder(property);
    configurer.accept(builder);
    entries.add(new TrackEntry<>(builder.property(), builder.build()));
    return this;
  }

  public TimelineBuilder quaterniond(TimelineProperty<Quaterniond> property,
                             Consumer<QuaternionTrackBuilder> configurer) {
    QuaternionTrackBuilder builder = new QuaternionTrackBuilder(property);
    configurer.accept(builder);
    entries.add(new TrackEntry<>(builder.property(), builder.build()));
    return this;
  }

  public TimelineBuilder transformProvider(TimelineProperty<TransformProvider> property,
                                           Consumer<TransformProviderTrackBuilder> configurer) {
    TransformProviderTrackBuilder builder = new TransformProviderTrackBuilder(property);
    configurer.accept(builder);
    entries.add(new TrackEntry<>(builder.property(), builder.build()));
    return this;
  }

  public Timeline build() {
    List<Timeline.TrackEntry<?>> trackEntries = new ArrayList<>();
    for (TrackEntry<?> e : entries) {
      trackEntries.add(convertEntry(e));
    }
    return Timeline.fromTracks(trackEntries);
  }

  @SuppressWarnings("unchecked")
  private static <T> Timeline.TrackEntry<T> convertEntry(TrackEntry<?> entry) {
    return new Timeline.TrackEntry<>((TimelineProperty<T>) entry.property, (TimelineTrack<T>) entry.track);
  }

  private record TrackEntry<T>(TimelineProperty<T> property, TimelineTrack<T> track) {}
}
