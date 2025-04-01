package com.teamabnormals.blueprint.core.api;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public interface EggLayer {
	int getEggTimer();

	void setEggTimer(int time);

	boolean isBirdJockey();

	void setBirdJockey(boolean jockey);

	Item getEggItem();

	int getNextEggTime(RandomSource rand);

	SoundEvent getEggLayingSound();
}
