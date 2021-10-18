package com.teamabnormals.blueprint.common.item;

import com.teamabnormals.blueprint.common.entity.BucketableWaterAnimal;
import com.teamabnormals.blueprint.core.util.item.filling.TargetedItemCategoryFiller;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * A {@link BucketItem} extension that can empty out entities typed of {@link BucketableWaterAnimal}.
 */
public class MobBucketItem extends BucketItem {
	private static final TargetedItemCategoryFiller FILLER = new TargetedItemCategoryFiller(() -> Items.TROPICAL_FISH_BUCKET);
	private final Supplier<EntityType<? extends BucketableWaterAnimal>> entityType;

	public MobBucketItem(Supplier<EntityType<? extends BucketableWaterAnimal>> entityType, Supplier<? extends Fluid> supplier, Item.Properties builder) {
		super(supplier, builder);
		this.entityType = entityType;
	}

	public void checkExtraContent(Level level, ItemStack stack, BlockPos pos) {
		if (!level.isClientSide) {
			this.placeEntity((ServerLevel) level, stack, pos);
		}
	}

	protected void playEmptySound(@Nullable Player player, LevelAccessor levelAccessor, BlockPos pos) {
		levelAccessor.playSound(player, pos, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
	}

	protected void placeEntity(ServerLevel level, ItemStack stack, BlockPos pos) {
		Entity entity = this.entityType.get().spawn(level, stack, null, pos, MobSpawnType.BUCKET, true, false);
		if (entity != null) {
			((BucketableWaterAnimal) entity).setFromBucket(true);
		}
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
		FILLER.fillItem(this, tab, items);
	}
}