package com.teamabnormals.abnormals_core.core.library;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author SmellyModder(Luke Tonon)
 * Simply an annotation to put above test code. Should only be used on types and methods.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Test {}