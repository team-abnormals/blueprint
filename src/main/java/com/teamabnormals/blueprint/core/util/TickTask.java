package com.teamabnormals.blueprint.core.util;

import java.util.function.Consumer;

/**
 * This class works as a tick timer that accepts a consumer on an object when complete.
 *
 * @author SmellyModder(Luke Tonon)
 */
public final class TickTask<T> {
	private final T owner;
	private final Consumer<T> consumer;
	private final int tickDuration;
	private int ticks;
	private boolean paused;

	public TickTask(T owner, Consumer<T> consumer, int tickDuration) {
		this.owner = owner;
		this.consumer = consumer;
		this.tickDuration = tickDuration;
	}

	/**
	 * Updates this task.
	 */
	public void tick() {
		if (!this.paused) this.ticks++;

		if (this.isComplete()) {
			this.consumer.accept(this.owner);
		}
	}

	/**
	 * Sets if this task is paused.
	 *
	 * @param paused If this task should be paused.
	 */
	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	/**
	 * Checks if this task is complete.
	 *
	 * @return If this task is complete.
	 */
	public boolean isComplete() {
		return this.ticks >= this.tickDuration;
	}
}