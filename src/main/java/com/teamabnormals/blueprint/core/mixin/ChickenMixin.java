package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.core.api.EggLayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Chicken.class)
public abstract class ChickenMixin extends Animal implements EggLayer {

	protected ChickenMixin(EntityType<? extends Animal> type, Level level) {
		super(type, level);
	}

	@Shadow
	public int eggTime;

	@Shadow
	public boolean isChickenJockey;

	@Shadow public abstract void setChickenJockey(boolean isChickenJockey);

	@Override
	public int getEggTimer() {
		return this.eggTime;
	}

	@Override
	public void setEggTimer(int time) {
		this.eggTime = time;
	}

	@Override
	public boolean isBirdJockey() {
		return this.isChickenJockey;
	}

	@Override
	public void setBirdJockey(boolean jockey) {
		this.setChickenJockey(jockey);
	}

	@Override
	public Item getEggItem() {
		return Items.EGG;
	}

	@Override
	public int getNextEggTime(RandomSource rand) {
		return rand.nextInt(6000) + 6000;
	}

	@Override
	public SoundEvent getEggLayingSound() {
		return SoundEvents.CHICKEN_EGG;
	}
}