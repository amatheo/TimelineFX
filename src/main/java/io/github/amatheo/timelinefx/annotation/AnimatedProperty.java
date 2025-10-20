package io.github.amatheo.timelinefx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as an animated property that can be bound to timeline values.
 * 
 * <p>Example:
 * <pre>
 * {@literal @}AnimatedProperty(defaultValue = "1.0")
 * public Double radius;
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AnimatedProperty {
    /**
     * Property name in timeline bindings. Defaults to field name if not specified.
     */
    String name() default "";
    
    /**
     * Default value when property is not bound in the timeline.
     */
    String defaultValue() default "";
}
