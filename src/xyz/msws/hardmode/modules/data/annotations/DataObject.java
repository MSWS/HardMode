package xyz.msws.hardmode.modules.data.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated No longer using {@link DefaultConstructor}
 *
 *             Annotation that is used internally to make sure the objects that
 *             are stored in the database have a default constructor so the
 *             objects can be reconstructed using reflection
 *
 * @see DefaultConstructor
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataObject {
}
