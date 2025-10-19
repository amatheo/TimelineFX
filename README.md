<div align="center"> <h1 align="center">TimelineFX</h1> <p align="center"> A declarative particle animation engine for Minecraft Paper/Spigot plugins. </p>

<p align="center"> <a href="https://github.com/amatheo/TimelineFX/releases/latest">
<img src="https://img.shields.io/github/v/release/amatheo/TimelineFX?style=for-the-badge&logo=github" alt="Latest Release"></a>
<a href="https://github.com/amatheo/TimelineFX/actions/workflows/maven-publish.yml">
<img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/amatheo/timelinefx/maven-publish.yml?style=for-the-badge">
</a>
<a href="https://github.com/amatheo/TimelineFX/wiki">
    <img src="https://img.shields.io/badge/Docs-Wiki-blueviolet?style=for-the-badge&logo=gitbook&logoColor=white" alt="Documentation">
</a>
<img src="https://img.shields.io/badge/Java-21+-blue?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21+">
<img src="https://img.shields.io/badge/API-PaperMC_/_Spigot-brightgreen?style=for-the-badge&logo=data:image/svg%2bxml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA5OTUuMyA5OTUuMzEiPjxkZWZzPjxjbGlwUGF0aCBpZD0iYSI+PHBhdGggZmlsbD0ibm9uZSIgZD0iTS0uMDA1IDQ5OC43MjggNDk2LjYwNy4wMzJsNDk4LjY5NSA0OTYuNjEyLTQ5Ni42MSA0OTguNjk2eiIvPjwvY2xpcFBhdGg+PGNsaXBQYXRoIGlkPSJiIj48cGF0aCBmaWxsPSJub25lIiBkPSJNNy43MDIgNDEwLjQ0NiA1ODQuODQ2IDcuNjc0IDk4Ny42MTggNTg0LjgyIDQxMC40NzMgOTg3LjU5eiIvPjwvY2xpcFBhdGg+PGNsaXBQYXRoIGlkPSJjIj48cGF0aCBmaWxsPSJub25lIiBkPSJtMjguMzUzIDMzMi4wNDcgNjM0Ljg2LTMwMy43NjUgMzAzLjc2NSA2MzQuODYtNjM0Ljg2IDMwMy43NjV6Ii8+PC9jbGlwUGF0aD48Y2xpcFBhdGggaWQ9ImQiPjxwYXRoIGZpbGw9Im5vbmUiIGQ9Im03MS41NTkgMjQwLjU0IDY4My4yMS0xNjguOTUyIDE2OC45NSA2ODMuMjFMMjQwLjUxIDkyMy43NXoiLz48L2NsaXBQYXRoPjwvZGVmcz48cGF0aCBmaWxsPSIjZjdlMzQ2IiBkPSJNLjAwNSA0OTguNzI4IDQ5Ni42MTcuMDMybDQ5OC42OTUgNDk2LjYxMi00OTYuNjEgNDk4LjY5NnoiLz48ZyBjbGlwLXBhdGg9InVybCgjYSkiPjxwYXRoIGZpbGw9IiNhNDk3MzEiIGQ9Ik0zLjUyNSA0MzguNDIgNTU2LjgzMSAzLjQ4bDQzNC45NCA1NTMuMzA3LTU1My4zMDYgNDM0Ljk0eiIvPjwvZz48cGF0aCBmaWxsPSIjNDI4ZWRiIiBkPSJNNy42OTggNDEwLjQ1NCA1ODQuODQyIDcuNjgzbDQwMi43NzEgNTc3LjE0NEw0MTAuNDcgOTg3LjU5OHoiLz48ZyBjbGlwLXBhdGg9InVybCgjYikiPjxwYXRoIGZpbGw9IiMyYjVmOTIiIGQ9Ik0xOC45OTkgMzYxLjQ2NCA2MzMuODMyIDE4Ljk3bDM0Mi40OTIgNjE0LjgzMy02MTQuODMyIDM0Mi40OTJ6Ii8+PC9nPjxwYXRoIGZpbGw9IiM5NmM5M2QiIGQ9Im0yOC4zNjYgMzMyLjA1MiA2MzQuODYtMzAzLjc2NSAzMDMuNzY1IDYzNC44Ni02MzQuODYgMzAzLjc2NXoiLz48ZyBjbGlwLXBhdGg9InVybCgjYykiPjxwYXRoIGZpbGw9IiM2Yzk4M2YiIGQ9Ik01OC43NTcgMjYzLjA2OCA3MzIuMjYgNTguODI3IDkzNi41IDczMi4zMyAyNjIuOTk5IDkzNi41N3oiLz48L2c+PHBhdGggZmlsbD0iI2ZkNGY1NyIgZD0ibTcxLjU2NiAyNDAuNTM3IDY4My4yMS0xNjguOTUgMTY4Ljk1MSA2ODMuMjEtNjgzLjIxIDE2OC45NXoiLz48ZyBjbGlwLXBhdGg9InVybCgjZCkiPjxwYXRoIGZpbGw9IiNiMDM2M2MiIGQ9Im0xMjkuNzgxIDE2Mi40OTIgNzAzLjAyNi0zMi43ODUgMzIuNzg1IDcwMy4wMjYtNzAzLjAyNiAzMi43ODV6Ii8+PC9nPjxwYXRoIGZpbGw9IiMzZDNkM2QiIGQ9Ik0xNDUuNzYgMTQ1Ljc2aDcwMy43OXY3MDMuNzlIMTQ1Ljc2eiIvPjxwYXRoIGZpbGw9IiM0NzQ3NDciIGQ9Ik0xNjQuNjEgMTY0LjZIODMwLjd2NjY2LjA5SDE2NC42MXoiLz48ZyBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxwYXRoIGZpbGw9IiMzMzMiIGQ9Ik01NTQuNDUgNDYyLjMyYy01NC4zOSA0Mi4xMy02OS4yMyA1Ny4yMi0xNjQuMzUgMTI3LjZsLTkuNjItNC44NGM1MS42LTQxLjcgMTI3LjU4LTEwMi42MiAxNzkuMTQtMTQ0LjI2IDE5LjI1LTE1LjI4IDM4LjY5LTM5LjExIDU3LjctNTQuOTF6bS0yMS43OCAxODguNzZjMy40NiAxLjcyIDYuMjUgMi42IDkuNjIgNC4zLTI2LjAzIDIzLjEyLTUzLjc2IDQ5LjI1LTc4Ljk2IDczLjYxLTQuODkgMi4zLTkuMzMgMS43OS0xMy4zMi0xLjU0bDE4LjQ1LTE5LjQ3YTQzMzAgNDMzMCAwIDAgMCA1Ny4zOS01NC44MmMzLjItMi40IDMuMTItMy43MiA2LjgzLTIuMDhabTExMy42NSA0Mi41NWMzLjcyLTIuMzEgNS42Ni00LjEyIDcuMDctNy42NSAxOS44Ny0xMjYuNjIgNDAuOTktMjUyLjk2IDYzLjM1LTM3OS4wNiA1LjA5LTI1LjMyIDYuMDgtMjEuNzYgNi4zNC0zMy43OSAxMS40MyAxLjc3IDE2LjE4IDguNDYgMTQuMjUgMjAuMDYtNS4zIDMyLjUzLTExLjI5IDY0LjkxLTE3Ljk1IDk3LjE0LTE1LjQxIDk0Ljc1LTMwLjk5IDE4OS40Ni00Ny4yNiAyODQuMDYtMS4zNyAxMC40NC0yLjE1IDE3LjIxLTQuNDcgMjEuMDktMy42OCA0LjUyLTguNDMgNi4xLTE0LjI1IDQuNzUtNC44My0xLjMzLTMuNzgtMS4xLTkuNjgtMy4zNGwtMy4yMS0xLjMzczMuOTktLjk1IDUuODItMS45M1oiLz48cGF0aCBmaWxsPSIjOTk5IiBkPSJNNTMyLjY3IDY1MS4wOWMtMS4yMy0uMTQtMi40OC0uMzUtMy41OS40NWEyMDA5IDIwMDkgMCAwIDAtNjIuOSA2MC42NWMtLjEzLS45NS4wMi0xLjg0LjQ1LTIuNyAzLjI5LTQuNzkgNS44OS04LjU3IDkuMTgtMTMuMzYuNjUtMS42My41NC0zLjA3LjQ0LTQuMDktMi4zOC0yNS41NS0xMC4wNS00OS40Ni0yMS43NS03Mi40MS0uMjUtLjQ4LjI1LTEuMjEgMS4zNS0uOSAyNS44NyAxMC4zNiA1MS40OCAyMS4xNSA3Ni44MiAzMi4zNVoiLz48cGF0aCBmaWxsPSIjY2NjIiBkPSJNNTU5LjYzIDQ0MC44M2E4NTM3IDg1MzcgMCAwIDEtMTgxLjk1IDE0Ni40NmMtMzYuNS0xNC44NC03Mi45Ni0yOS44LTEwOS4zNy00NC44Ny0xNC4wOC01LjgzLTE0LjAzLTE4LjU0LTEuNi0yNy4wMS02Ljc0IDguNTQtNS41NCAxNS44OCAzLjU5IDIyLjAxIDEyLjQ5IDUuMyAxMDYuNDEgNDQuMjUgMTA2LjQxIDQ0LjI1cy0uNzYtMy4wMyAxODIuOTEtMTQwLjg0WiIvPjxwYXRoIGZpbGw9IiNiMmIyYjIiIGQ9Ik02MzYuOSA2ODcuOTNjMTMuOTQgNC45OCAxMy40MyAxLjUgMTkuMzktNC4wOC0uNTEgMy45Ni0uOTUgOC4zLTUuOTkgMTEuMTItMy40NiAyLTYuNTkgMS45My05LjgxLjZhMTgyMCAxODIwIDAgMCAxLTEwNy44Mi00NC40OGMtMjUuMzQtMTEuMi01MC45NS0yMS45OC03Ni44Mi0zMi4zNWwtMS4zNS45YzEyLjY0IDIzLjYyIDE5Ljk4IDQ4Ljc3IDIyLjAxIDc1LjQ4LTMuMjkgNC43OS02LjU5IDkuNTgtOS44OCAxNC4zOHEtLjY0NSAxLjI3NS0uNDUgMi43bC0xNi4xNyAxNS4yN2MtNC4wNi0uMDEtNi4zMS0yLjExLTYuNzQtNi4yOS0uNTktMzMuNzMtLjYtNjcuNDYtLjAyLTEwMS4xOSAwLS40MS4wMS0uODEuMDItMS4yNC4xNS01LjA3IDUuMjYtNi41MiAxMC4zMy00Ljk0cTkxLjg0NSAzNi42NzUgMTgzLjMgNzQuMTNaIi8+PHBhdGggZmlsbD0iI2NjYyIgZD0iTTcxOS42NCAyNjcuOTNjNSAzLjk4IDQuMjMgMTAuNTcgMy4yOCAxNi4xNy0yMi42MyAxMzIuODItNDQuNTYgMjY2LjMyLTY2LjUzIDM5OS4yLS4yMSAxLjI0LS43NiA0LjE1LTIuNDIgNS42NS0zLjc3IDMtNy4xNyAyLjQ4LTE3LjA2LTEuMDEgMS40My0zLjM3IDIuNDgtNi45NiAzLjE0LTEwLjc4IDI1LjM0LTEyOS40MSA1MS4yNS0yNTguNjUgNzcuNzItMzg3LjcxIDIuMzMtMTMuNjUgMi4xMy05LjcxIDEuODctMjEuNVoiLz48cGF0aCBmaWxsPSIjZjJmMmYyIiBkPSJNNzE5LjY0IDI2Ny45M2MuOTIgMTAuNiAxLjA1IDUuODktMS44NyAyMS41LTI2LjQ3IDEyOS4wNi01Mi4zOCAyNTguMy03Ny43MiAzODcuNzEtLjY2IDMuODItMS43MSA3LjQxLTMuMTQgMTAuNzhxLTkxLjQ1NS0zNy40NTUtMTgzLjMtNzQuMTNjLTUuMDgtMS41OC04LjUyLjA3LTEwLjMzIDQuOTQtLjYgMzQuMTQtLjYgNjguMjkgMCAxMDIuNDMuNDMgNC4xOCAyLjY4IDYuMjggNi43NCA2LjI5LTEuMTIgMS4xNS0zLjc4IDEuMDctMy43OCAxLjA3cy0xMS45Mi0uNjktMTMuMzYtMy41NGMtLjM3LS43MS0uMzgtMS40Ni0uMzktMi4wOC0uMTgtMzYuMy0uNjEtNzIuMzktMS4yOS0xMDguMjktLjAyLS44NyAwLTEuNzIuMzUtMy4yNHMxLjM2LTMuNDYgMi43NS01LjIyYTExNjExIDExNjExIDAgMCAwIDE1OS4wNC0xOTEuMzkgMTM0MyAxMzQzIDAgMCAxLTMzLjY5IDI2LjA2QzQwMi40NCA1NTguNzggMzc2Ljc0IDU4MS42NiAzNzYuNzQgNTgxLjY2cy03MS45LTI5LjYzLTEwNi40MS00NC4yNWMtOS4xMy02LjEzLTEwLjMzLTEzLjQ3LTMuNTktMjIuMDFhNTA2MjggNTA2MjggMCAwIDAgNDI5LjQ5LTI0Ni42NGM1LjI4LTMuMjYgMTAuOTctNC40NiAxNy4wNy0zLjU5IDEuMzEuMTMgNC4xMS41OCA2LjM2IDIuNzZaIi8+PC9nPjwvc3ZnPg==" alt="PaperMC / Spigot">
<img src="https://img.shields.io/github/license/amatheo/TimelineFX?style=for-the-badge" alt="License">
</p>
</div>

TimelineFX is a declarative animation engine for Minecraft Paper/Spigot plugins. It lets you build complex,
timeline-driven particle shows that stay in sync across multiple effects, while keeping the code readable through
builders and reusable primitives.

## Features

- **Timeline-based animation** – express all animated properties of effects (radius, position, scale, rotation, custom
  parameters...) with keyframes, easing functions and looping controls.
- **Annotation-based properties** – declare animated properties with `@AnimatedProperty` annotations for clean, readable
  code.
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

TimelineFX is built against the Paper API and does not use any version-specific server internals (NMS), making it
broadly compatible with modern Paper/Spigot versions.

Compatibility is primarily determined by its
dependency, [ParticleNativeAPI](https://github.com/fierioziy/ParticleNativeAPI).

- **Tested & Verified:** Paper 1.21+ & ParticleNativeAPI 4.4.0+
- **General Rule:** The library should work on any server version supported by a compatible release of
  ParticleNativeAPI.

## Getting Started

### Installation

To use TimelineFX in your Paper/Spigot plugin, add it as a dependency in your build system. For Maven, add the following
to your `pom.xml`:

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

> **⚠️ — Lifecycle & Single Instance:** Managing the `AnimationEngine` instance is the plugin developer's
> responsibility. Create a single `AnimationEngine` per plugin (for example in your plugin's `onEnable`), and reuse that
> instance across your code.
>
> Running multiple `AnimationEngine` instances in the same plugin can cause duplicated ticks, increased resource usage,
> and unexpected behaviour.

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

Timelines are built using the `TimelineBuilder` class, which provides a fluent API for defining keyframes and animated
properties.
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

This example creates a timeline that animates a `Vector3d` property called `roots.position` from `(0, 0, 0)` to
`(0, 5, 10)` over 10 seconds.

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
engine.

play(show);
```

The animation engine will handle ticking the timeline and rendering the effect to the specified players.

In this example, a simple circle effect spawn at world coordinate (0,0,0) and animates its position to (0, 5, 10) over
10 seconds.

## Going further

Once you're comfortable with the basics, you can explore more advanced features of TimelineFX, such as:

- Creating custom effects by extending the `AnimatedEffect` class.
- Animate the effect's `@AnimatedProperty` using ```.bindParameter() ```
- Composing complex shows with multiple clips, sequences, and parallel effects.

## Documentation

For detailed documentation, including API references and advanced usage examples, please visit
the [TimelineFX Wiki](https://github.com/amatheo/TimelineFX/wiki)
