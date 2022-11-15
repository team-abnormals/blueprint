package com.teamabnormals.blueprint.core.events;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Function;

/**
 * This class handles the storing and processing of listeners of an event.
 * <p>Adding listeners after mod loading is not recommended because the class is only threadsafe when registering listeners during mod loading.</p>
 *
 * @param <L> The type of the listeners.
 * @author SmellyModder (Luke Tonon)
 */
public final class SimpleEvent<L> {
	private final Function<L[], L> invokerFactory;
	private L[] listeners;
	private L invoker;

	/**
	 * Constructs a new {@link SimpleEvent} instance that has an invoker factory and no listeners.
	 *
	 * @param listenerClass  The type of the listeners.
	 * @param invokerFactory The function to use for creating an invoker for an array of listeners.
	 */
	@SuppressWarnings("unchecked")
	public SimpleEvent(Class<L> listenerClass, Function<L[], L> invokerFactory) {
		this.invoker = (this.invokerFactory = invokerFactory).apply(this.listeners = (L[]) Array.newInstance(listenerClass, 0));
	}

	/**
	 * Registers a listener to this event.
	 * <p>Registering listeners after mod loading is not recommended.</p>
	 *
	 * @param listener A listener object to register.
	 */
	public synchronized void registerListener(L listener) {
		int oldLength = this.listeners.length;
		(this.listeners = Arrays.copyOf(this.listeners, oldLength + 1))[oldLength] = listener;
		this.invoker = this.invokerFactory.apply(this.listeners);
	}

	/**
	 * Gets the invoker for processing the listeners of the event.
	 *
	 * @return The invoker.
	 */
	public L getInvoker() {
		return this.invoker;
	}
}
