package io.github.amatheo.timelinefx.effect;

import io.github.amatheo.timelinefx.annotation.AnimatedProperty;
import org.bukkit.Color;
import org.joml.Vector3d;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Utility class for binding animated properties from EvaluatedParams to Effect fields.
 *
 * <p>This class uses reflection to discover fields annotated with {@link AnimatedProperty}
 * and automatically populates them with values from the provided {@link EvaluatedParams}.
 *
 * <p>Parameters are passed explicitly through method signatures rather than using ThreadLocal,
 * making the data flow clear and improving testability.
 */
public final class PropertyBinder {

  private static final Map<Class<?>, List<Binding>> CACHE = new ConcurrentHashMap<>();
  private static final Object ABSENT = new Object();

  private PropertyBinder() {
  }

  private static List<Binding> buildBindings(Class<?> effectClass) {
    final ArrayList<Binding> list = new ArrayList<>();
    final var lookup = MethodHandles.lookup();

    for (Field f : getAllInstanceFields(effectClass)) {
      AnimatedProperty ann = f.getAnnotation(AnimatedProperty.class);
      if (ann == null) {
        continue;
      }
      if (Modifier.isStatic(f.getModifiers())) {
        continue;
      }

      try {
        String name = ann.name().isEmpty() ? f.getName() : ann.name();
        Class<?> fieldType = f.getType();

        BiConsumer<Object, Object> setter = compileSetter(lookup, f);
        Function<Object, Object> getter = compileGetter(lookup, f);

        ValueExtractor extractor = buildExtractor(fieldType);

        boolean hasDefault = ann.defaultValue() != null && !ann.defaultValue().isEmpty();
        Object defaultValue = hasDefault ? parseDefault(ann.defaultValue(), fieldType) : null;

        list.add(new Binding(name, fieldType, setter, getter, extractor, hasDefault, defaultValue));
      } catch (Throwable e) {
        throw new IllegalStateException("Failed to bind @AnimatedProperty for field '" + f + "' in " + effectClass, e);
      }
    }
    return Collections.unmodifiableList(list);
  }

  private static List<Field> getAllInstanceFields(Class<?> type) {
    ArrayList<Field> out = new ArrayList<>();
    Class<?> c = type;
    while (c != null && c != Object.class) {
      for (Field f : c.getDeclaredFields()) {
        if (!Modifier.isStatic(f.getModifiers())) {
          f.setAccessible(true);
          out.add(f);
        }
      }
      c = c.getSuperclass();
    }
    return out;
  }

  // ---- LMF compilers ----
  @SuppressWarnings("unchecked")
  private static BiConsumer<Object, Object> compileSetter(MethodHandles.Lookup caller, Field f) throws Throwable {
    final Class<?> owner = f.getDeclaringClass();
    final Class<?> ftype = f.getType();
    final MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(owner, caller);

    MethodHandle mh;
    try {
      VarHandle vh = lookup.findVarHandle(owner, f.getName(), ftype);
      MethodType setType = MethodType.methodType(void.class, owner, ftype);
      MethodHandle invoker = MethodHandles.varHandleExactInvoker(VarHandle.AccessMode.SET, setType);
      mh = MethodHandles.insertArguments(invoker, 0, vh);
    } catch (Throwable ignore) {
      mh = lookup.unreflectSetter(f);
    }

    MethodHandle erased = mh.asType(MethodType.methodType(void.class, Object.class, Object.class));
    return (BiConsumer<Object, Object>) MethodHandleProxies.asInterfaceInstance(BiConsumer.class, erased);
  }

  @SuppressWarnings("unchecked")
  private static Function<Object, Object> compileGetter(MethodHandles.Lookup caller, Field f) throws Throwable {
    final Class<?> owner = f.getDeclaringClass();
    final Class<?> ftype = f.getType();
    final MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(owner, caller);

    MethodHandle mh;
    try {
      VarHandle vh = lookup.findVarHandle(owner, f.getName(), ftype);
      MethodType getType = MethodType.methodType(ftype, owner);
      MethodHandle invoker = MethodHandles.varHandleExactInvoker(VarHandle.AccessMode.GET, getType);
      mh = MethodHandles.insertArguments(invoker, 0, vh);
    } catch (Throwable ignore) {
      mh = lookup.unreflectGetter(f);
    }

    MethodHandle erased = mh.asType(MethodType.methodType(Object.class, Object.class));
    return (Function<Object, Object>) MethodHandleProxies.asInterfaceInstance(Function.class, erased);
  }

  /**
   * Binds annotated fields from EvaluatedParams to the effect instance.
   * Only updates fields when values change. Applies defaults if fields are unset.
   * 
   * @param effectInstance The effect instance to bind
   * @param params The parameters to bind from
   * @return true if any field was updated
   */
  public static boolean bindPropertiesAndDetectChanges(Object effectInstance, EvaluatedParams params) {
    if (effectInstance == null) return false;

    final Class<?> type = effectInstance.getClass();
    final List<Binding> bindings = CACHE.computeIfAbsent(type, PropertyBinder::buildBindings);

    boolean changed = false;
    for (Binding b : bindings) {
      Object currentValue = b.getter.apply(effectInstance);
      Object newValue = (params != null) ? b.readFromParams(params) : ABSENT;

      if (newValue != ABSENT) {
        if (!java.util.Objects.equals(currentValue, newValue)) {
          b.setter.accept(effectInstance, newValue);
          changed = true;
        }
        continue;
      }
      
      if (b.hasDefault && isUnset(currentValue, b.fieldType)) {
        if (!java.util.Objects.equals(currentValue, b.defaultValue)) {
          b.setter.accept(effectInstance, b.defaultValue);
          changed = true;
        }
      }
    }
    return changed;
  }

  private static ValueExtractor buildExtractor(Class<?> target) {
    if (target == double.class || target == Double.class) return (p, k) -> number(p, k, Double.class);
    if (target == float.class || target == Float.class) return (p, k) -> number(p, k, Float.class);
    if (target == int.class || target == Integer.class) return (p, k) -> number(p, k, Integer.class);
    if (target == long.class || target == Long.class) return (p, k) -> number(p, k, Long.class);
    if (target == boolean.class || target == Boolean.class) return PropertyBinder::bool;

    if (target == String.class)
      return (p, k) -> !p.has(k) ? ABSENT : (p.raw(k) == null ? null : String.valueOf(p.raw(k)));
    if (target == Vector3d.class) return (p, k) -> !p.has(k) ? ABSENT : (p.raw(k) instanceof Vector3d v ? v : ABSENT);
    if (target == Color.class) return (p, k) -> !p.has(k) ? ABSENT : (p.raw(k) instanceof Color c ? c : ABSENT);

    // Fallback: if present, pass as is
    return (p, k) -> !p.has(k) ? ABSENT : p.raw(k);
  }

  private static Object number(EvaluatedParams p, String key, Class<?> to) {
    if (!p.has(key)) return ABSENT;
    Object v = p.raw(key);
    if (v == null) return null;
    if (v instanceof Number n) {
      if (to == Double.class) return n.doubleValue();
      if (to == Float.class) return n.floatValue();
      if (to == Integer.class) return n.intValue();
      if (to == Long.class) return n.longValue();
    }
    if (v instanceof String s && !s.isEmpty()) {
      try {
        if (to == Double.class) return Double.parseDouble(s);
        if (to == Float.class) return Float.parseFloat(s);
        if (to == Integer.class) return Integer.parseInt(s);
        if (to == Long.class) return Long.parseLong(s);
      } catch (NumberFormatException ignored) {
      }
    }
    return ABSENT;
  }

  private static Object bool(EvaluatedParams p, String key) {
    if (!p.has(key)) return ABSENT;
    Object v = p.raw(key);
    if (v == null) return null;
    if (v instanceof Boolean b) return b;
    if (v instanceof String s) {
      if ("true".equalsIgnoreCase(s)) return Boolean.TRUE;
      if ("false".equalsIgnoreCase(s)) return Boolean.FALSE;
    }
    return ABSENT;
  }

  /**
   * Returns true if field is unset (null or zero for primitives).
   */
  private static boolean isUnset(Object cur, Class<?> type) {
    if (cur == null) return true;
    if (type.isPrimitive()) {
      if (type == boolean.class) return Boolean.FALSE.equals(cur);
      if (type == int.class) return ((Integer) cur) == 0;
      if (type == long.class) return ((Long) cur) == 0L;
      if (type == float.class) return ((Float) cur) == 0f;
      if (type == double.class) return ((Double) cur) == 0d;
    }
    return false;
  }

  private static Object parseDefault(String raw, Class<?> type) {
    String s = raw.trim();
    if (type == String.class) return s;
    if (type == int.class || type == Integer.class) return Integer.parseInt(s);
    if (type == long.class || type == Long.class) return Long.parseLong(s);
    if (type == float.class || type == Float.class) return Float.parseFloat(s);
    if (type == double.class || type == Double.class) return Double.parseDouble(s);
    if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(s);
    throw new IllegalArgumentException("Unsupported defaultValue for type " + type.getName() + " : '" + raw + "'");
  }


  private interface ValueExtractor {
    Object extract(EvaluatedParams params, String key);
  }

  private record Binding(String name, Class<?> fieldType, BiConsumer<Object, Object> setter,
                         Function<Object, Object> getter, ValueExtractor extractor, boolean hasDefault,
                         Object defaultValue) {

    Object readFromParams(EvaluatedParams params) {
      return extractor.extract(params, name);
    }
  }
}
