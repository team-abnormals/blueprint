package com.minecraftabnormals.abnormals_core.common.blocks;

import com.minecraftabnormals.abnormals_core.core.util.item.filling.TargetedItemGroupFiller;
import net.minecraft.block.FlowerBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.NonNullList;

import java.util.function.Supplier;

public class AbnormalsFlowerBlock extends FlowerBlock {
	private static final TargetedItemGroupFiller FILLER = new TargetedItemGroupFiller(() -> Items.WITHER_ROSE);
	private final Supplier<Effect> stewEffect;
	private final int stewEffectDuration;

	public AbnormalsFlowerBlock(Supplier<Effect> stewEffect, int stewEffectDuration, Properties properties) {
		super(Effects.WEAKNESS, stewEffectDuration, properties);
		this.stewEffect = stewEffect;
		this.stewEffectDuration = stewEffectDuration;
	}

	@Override
	public Effect getStewEffect() {
		return this.stewEffect.get();
	}

	@Override
	public int getStewEffectDuration() {
		return this.getStewEffect().isInstant() ? this.stewEffectDuration : this.stewEffectDuration * 20;
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this.asItem(), group, items);
	}
}
