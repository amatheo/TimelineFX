package com.github.amatheo.timelinefx.core;

import com.github.amatheo.timelinefx.core.impl.ParticleNativeAPIRenderer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public final class AnimationEngine implements Listener, AutoCloseable {
  private final List<Playable> actives = new ArrayList<>();
  private final PlaybackContext ctx;
  private final BukkitTask task;

  public AnimationEngine(Plugin plugin) {
    AtomicLong tickCounter = new AtomicLong(Bukkit.getCurrentTick());
    this.ctx = new PlaybackContext(plugin,
        tickCounter.get(),
        () -> (long) Bukkit.getCurrentTick(),
        1.0 / 20.0,
        new ParticleNativeAPIRenderer(),
        new Random());
    this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::tickAll, 1L, 1L);
  }

  public void play(Playable p) {
    p.start(ctx);
    actives.add(p);
  }

  private void tickAll() {
    for (Iterator<Playable> it = actives.iterator(); it.hasNext(); ) {
      var p = it.next();
      p.tick(ctx);
      if (p.isDone()) {
        p.stop(ctx);
        it.remove();
      }
    }
  }

  public void close() {
    task.cancel();
    actives.clear();
  }
}