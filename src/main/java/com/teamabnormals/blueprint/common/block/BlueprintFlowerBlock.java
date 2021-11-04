package com.teamabnormals.blueprint.common.block;

import com.teamabnormals.blueprint.core.util.item.filling.TargetedItemCategoryFiller;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.NonNullList;

import java.util.function.Supplier;

/**
 * A {@link FlowerBlock} extension that stores information about the flower's stew effect and fills its item after the latest vanilla flower item.
 */
public class BlueprintFlowerBlock extends FlowerBlock {
	private static final TargetedItemCategoryFiller FILLER = new TargetedItemCategoryFiller(() -> Items.WITHER_ROSE);
	private final Supplier<MobEffect> stewEffect;
	private final int stewEffectDuration;

	public BlueprintFlowerBlock(Supplier<MobEffect> stewEffect, int stewEffectDuration, Properties properties) {
		super(MobEffects.WEAKNESS, stewEffectDuration, properties);
		this.stewEffect = stewEffect;
		this.stewEffectDuration = stewEffectDuration;
	}

	@Override
	public MobEffect getSuspiciousStewEffect() {
		return this.stewEffect.get();
	}

	@Override
	public int getEffectDuration() {
		return this.getSuspiciousStewEffect().isInstantenous() ? this.stewEffectDuration : this.stewEffectDuration * 20;
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this.asItem(), group, items);
	}
}
