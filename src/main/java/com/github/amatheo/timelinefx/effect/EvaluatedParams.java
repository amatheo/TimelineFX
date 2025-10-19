package com.github.amatheo.timelinefx.effect;

import org.bukkit.Color;
import org.joml.Vector3d;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public final class EvaluatedParams implements AutoCloseable {
  private static final ThreadLocal<Deque<EvaluatedParams>> STACK = ThreadLocal.withInitial(ArrayDeque::new);
  private final Map<String, Object> values;

  private EvaluatedParams(Map<String, Object> values) {
    this.values = values;
  }

  public static EvaluatedParams fromValues(Map<String, Object> values) {
    if (values == null || values.isEmpty()) {
      return new EvaluatedParams(Map.of());
    }
    return new EvaluatedParams(Map.copyOf(values));
  }

  public Map<String, Object> raw() {
    return values;
  }

  public Object getRaw(String key, Object def) {
    Object v = values.get(key);
    if (v == null) return def;
    return v;
  }

  // Type-safe helpers
  public double getDouble(String key, double def) {
    Object v = values.get(key);
    if (v instanceof Number n) return n.doubleValue();
    return def;
  }

  public int getInt(String key, int def) {
    Object v = values.get(key);
    if (v instanceof Number n) return n.intValue();
    return def;
  }

  public boolean getBool(String key, boolean def) {
    Object v = values.get(key);
    if (v instanceof Boolean b) return b;
    return def;
  }

  public Vector3d getVec3(String key, Vector3d def) {
    Object v = values.get(key);
    if (v instanceof Vector3d vv) return vv;
    if (v instanceof List<?> lst && lst.size() == 3 && lst.stream().allMatch(x -> x instanceof Number)) {
      return new Vector3d(((Number) lst.get(0)).doubleValue(), ((Number) lst.get(1)).doubleValue(), ((Number) lst.get(2)).doubleValue());
    }
    return def;
  }

  public Color getColor(String key, Color def) {
    Object v = values.get(key);
    if (v instanceof Color c) return c;
    return def;
  }

  public boolean has(String key) {
    return values.containsKey(key);
  }

  public Object raw(String key) {
    return values.get(key);
  }

  @Override
  public void close() {
    var d = STACK.get();
    if (!d.isEmpty()) d.pop();
  }
}
