package com.teamabnormals.abnormals_core.core.library;

/**
 * A simple functional interface that takes in an object and returns a new instance of the object
 * @author SmellyModder(Luke Tonon)
 * @param <T> - Type to modify
 */
@FunctionalInterface
public interface Modifier<T> {
	public T modify(T toModify);
}