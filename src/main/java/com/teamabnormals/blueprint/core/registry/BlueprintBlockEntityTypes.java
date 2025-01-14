package com.teamabnormals.blueprint.core.registry;

import com.teamabnormals.blueprint.common.block.BlueprintBeehiveBlock;
import com.teamabnormals.blueprint.common.block.BlueprintChiseledBookShelfBlock;
import com.teamabnormals.blueprint.common.block.chest.BlueprintChestBlock;
import com.teamabnormals.blueprint.common.block.chest.BlueprintTrappedChestBlock;
import com.teamabnormals.blueprint.common.block.entity.*;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.events.LoadThisClassEvent;
import com.teamabnormals.blueprint.core.util.registry.BlockEntitySubRegistryHelper;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Registry class for the built-in {@link BlockEntityType}s.
 */
@EventBusSubscriber(modid = Blueprint.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class BlueprintBlockEntityTypes {
	@SubscribeEvent
	public static void $(LoadThisClassEvent event) {}

	public static final BlockEntitySubRegistryHelper HELPER = Blueprint.REGISTRY_HELPER.getBlockEntitySubHelper();

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlueprintSignBlockEntity>> SIGN = HELPER.createBlockEntity("sign", BlueprintSignBlockEntity::new, () -> BlueprintSignBlockEntity.VALID_BLOCKS);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlueprintHangingSignBlockEntity>> HANGING_SIGN = HELPER.createBlockEntity("hanging_sign", BlueprintHangingSignBlockEntity::new, () -> BlueprintHangingSignBlockEntity.VALID_BLOCKS);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlueprintBeehiveBlockEntity>> BEEHIVE = HELPER.createBlockEntity("beehive", BlueprintBeehiveBlockEntity::new, BlueprintBeehiveBlock.class);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlueprintChiseledBookShelfBlockEntity>> CHISELED_BOOKSHELF = HELPER.createBlockEntity("chiseled_bookshelf", BlueprintChiseledBookShelfBlockEntity::new, BlueprintChiseledBookShelfBlock.class);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlueprintChestBlockEntity>> CHEST = HELPER.createBlockEntity("chest", BlueprintChestBlockEntity::new, BlueprintChestBlock.class);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlueprintTrappedChestBlockEntity>> TRAPPED_CHEST = HELPER.createBlockEntity("trapped_chest", BlueprintTrappedChestBlockEntity::new, BlueprintTrappedChestBlock.class);
}