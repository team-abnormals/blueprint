package com.minecraftabnormals.abnormals_core.common.blocks;

import com.minecraftabnormals.abnormals_core.core.util.item.filling.TargetedItemGroupFiller;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.NonNullList;

import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class AbnormalsFlowerBlock extends FlowerBlock {
	private static final TargetedItemGroupFiller FILLER = new TargetedItemGroupFiller(() -> Items.WITHER_ROSE);
	private final Supplier<MobEffect> stewEffect;
	private final int stewEffectDuration;

	public AbnormalsFlowerBlock(Supplier<MobEffect> stewEffect, int stewEffectDuration, Properties properties) {
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
