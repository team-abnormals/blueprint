package com.minecraftabnormals.abnormals_core.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author SmellyModder(Luke Tonon)
 * This annotation is to annotated on test code. This annotation should only be used on types and methods.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Test {}