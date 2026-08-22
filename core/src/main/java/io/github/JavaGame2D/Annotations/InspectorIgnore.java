package io.github.JavaGame2D.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Make it available at runtime for Reflection
@Target(ElementType.FIELD)          // Only applies to fields
public @interface InspectorIgnore {
    // Optionally, you can add a reason, e.g., String value() default "";
}
