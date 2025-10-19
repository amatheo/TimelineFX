# TimelineFX
TimelineFX is a declarative animation engine for Minecraft Paper/Spigot plugins. It lets you build complex, timeline-driven particle shows that stay in sync across multiple effects, while keeping the code readable through builders and reusable primitives.

## Features
- **Timeline-based animation** – express all animated properties of effects (radius, position, scale, rotation, custom parameters...) with keyframes, easing functions and looping controls.
- **Annotation-based properties** – declare animated properties with `@AnimatedProperty` annotations for clean, readable code.
- **Reusable primitives** – build complex effects from smaller, reusable building blocks.
- **Shared playheads** – drive several effects/layers from the same `TimelinePlayback` via an `EffectClip`.
- **Orchestration helpers** – `Sequence` and `Parallel` compose clips, groups, waits, and callbacks into
  higher level shows that the `AnimationEngine` ticks for you.
- **Paper ready** – plugs directly into a Bukkit/Paper plugin through `AnimationEngine`, using
    [ParticleNativeAPI](https://github.com/fierioziy/ParticleNativeAPI) for efficient particle dispatch.

> [!IMPORTANT]
> TimelineFX is currently under continuous development
> Backward compatibility is not guaranteed between minor versions.

## Compatibility
TimelineFX is built against the Paper API and does not use any version-specific server internals (NMS), making it broadly compatible with modern Paper/Spigot versions.

Compatibility is primarily determined by its dependency, [ParticleNativeAPI](https://github.com/fierioziy/ParticleNativeAPI).

- **Tested & Verified:** Paper 1.21+ & ParticleNativeAPI 4.4.0+
- **General Rule:** The library should work on any server version supported by a compatible release of ParticleNativeAPI.

## Getting Started

### Installation
To use TimelineFX in your Paper/Spigot plugin, add it as a dependency in your build system. For Maven, add the following to your `pom.xml`:
```xml
<dependency>
    <groupId>com.github.amatheo</groupId>
    <artifactId>timelinefx</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Basic Usage

#### Creating/Initializing the Animation Engine
The animation engine needs to be initialized with your plugin instance to manage the animation lifecycle.

It should be reused across your plugin to ensure proper ticking and resource management.

> **⚠️ — Lifecycle & Single Instance:** Managing the `AnimationEngine` instance is the plugin developer's responsibility. Create a single `AnimationEngine` per plugin (for example in your plugin's `onEnable`), and reuse that instance across your code.
>
> Running multiple `AnimationEngine` instances in the same plugin can cause duplicated ticks, increased resource usage, and unexpected behaviour.

Example initialization in your main plugin class:
```java
public final class YourPlugin extends JavaPlugin {
  private AnimationEngine engine;

  @Override
  public void onEnable() {
    this.engine = new AnimationEngine(this);
  }
}
```

### Build timelines
Timelines are built using the `TimelineBuilder` class, which provides a fluent API for defining keyframes and animated properties.
Example of building a simple timeline:
```java
TimelineProperty<Vector3d> position = TimelineProperty.of("roots.position");

Timeline timelineExample = Timeline.builder()
    .vector3d(position, track -> track
        .segment(0.0, channel -> channel
            .add(Keyframe.of(0.0, new Vector3d(0, 0, 0)))
            .add(Keyframe.of(10.0, new Vector3d(0, 5, 10)))
        )
    )
    .build();
```

This example creates a timeline that animates a `Vector3d` property called `roots.position` from `(0, 0, 0)` to `(0, 5, 10)` over 10 seconds.

### Create a clip
An `EffectClip` ties a timeline to an effect, allowing the effect to be driven by the timeline's playhead.
Example of creating an effect clip with a circle effect:
```java
EffectClip circleClip = EffectClip.builder()
    .timeline(timeline)
    .rootBindings(t -> t
        .bindPosition(position)
    )
    .layer(layer -> layer
        .effect(new CircleEffect(/** ParticleType of ParticleNativeAPI **/))
    )
    .players(/**List of players to show the effect to**/)
    .build();
```

### Play the clip
To play the effect clip, create a sequence or parallel composition and add it to the animation engine.
Example of playing the clip:
```java
Playable show = SequenceBuilder()
    .then(circleClip)
    .build();
engine.play(show);
```

The animation engine will handle ticking the timeline and rendering the effect to the specified players.

In this example, a simple circle effect spawn at world coordinate (0,0,0) and animates its position to (0, 5, 10) over 10 seconds.

## Going further
Once you're comfortable with the basics, you can explore more advanced features of TimelineFX, such as:
- Creating custom effects by extending the `AnimatedEffect` class.
- Animate the effect's `@AnimatedProperty` using ```.bindParameter() ``` 
- Composing complex shows with multiple clips, sequences, and parallel effects.

## Documentation
For detailed documentation, including API references and advanced usage examples, please visit the [TimelineFX Wiki](https://github.com/amatheo/TimelineFX/wiki)
