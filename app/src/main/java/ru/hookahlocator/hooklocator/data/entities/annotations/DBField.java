package ru.hookahlocator.hooklocator.data.entities.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Pavel Smelovsky
 *         <p>
 *         Copyright (c) 2015 Onza.ME LLC. All right reserved.
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface DBField
{
    public static final String DEFAULT = "THIS IS DEFAULT VALUE - DO NOT USE";
    String columnName() default DEFAULT;
}
