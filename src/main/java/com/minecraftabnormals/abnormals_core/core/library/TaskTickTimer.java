package com.minecraftabnormals.abnormals_core.core.library;

import java.util.function.Consumer;

/**
 * Class that works as a timer that can execute a task when it's complete
 * @author SmellyModder(Luke Tonon)
 */
public class TaskTickTimer<T> {
	private final T owner;
	private final Consumer<T> consumer;
	private final int ticks;
	private int ticksPassed;
	private boolean paused;
	
	public TaskTickTimer(T owner, Consumer<T> consumer, int ticks) {
		this.owner = owner;
		this.consumer = consumer;
		this.ticks = ticks;
	}
	
	public void update() {
		if(!this.paused) this.ticksPassed++;
		
		if(this.isComplete()) {
			this.consumer.accept(this.owner);
		}
	}
	
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	public boolean isComplete() {
		return this.ticksPassed >= this.ticks;
	}
}