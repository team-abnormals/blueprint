package com.minecraftabnormals.abnormals_core.core.api;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.util.LazyLoadedValue;

import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class AbnormalsItemTier implements Tier {
	private final int level;
	private final int uses;
	private final float speed;
	private final float damage;
	private final int enchantmentValue;
	private final LazyLoadedValue<Ingredient> repairIngredient;

	public AbnormalsItemTier(int levelIn, int usesIn, float speedIn, float damageIn, int enchantmentValueIn, Supplier<Ingredient> repairIngredientIn) {
		this.level = levelIn;
		this.uses = usesIn;
		this.speed = speedIn;
		this.damage = damageIn;
		this.enchantmentValue = enchantmentValueIn;
		this.repairIngredient = new LazyLoadedValue<>(repairIngredientIn);
	}

	@Override
	public int getUses() {
		return uses;
	}

	@Override
	public float getSpeed() {
		return this.speed;
	}

	@Override
	public float getAttackDamageBonus() {
		return this.damage;
	}

	@Override
	public int getLevel() {
		return this.level;
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}
}
