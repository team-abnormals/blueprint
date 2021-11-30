package com.teamabnormals.blueprint.core.registry;

import com.teamabnormals.blueprint.common.block.BlueprintBeehiveBlock;
import com.teamabnormals.blueprint.common.block.chest.BlueprintChestBlock;
import com.teamabnormals.blueprint.common.block.chest.BlueprintTrappedChestBlock;
import com.teamabnormals.blueprint.common.block.entity.BlueprintBeehiveBlockEntity;
import com.teamabnormals.blueprint.common.block.entity.BlueprintChestBlockEntity;
import com.teamabnormals.blueprint.common.block.entity.BlueprintSignBlockEntity;
import com.teamabnormals.blueprint.common.block.entity.BlueprintTrappedChestBlockEntity;
import com.teamabnormals.blueprint.common.block.sign.IBlueprintSign;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.registry.BlockEntitySubRegistryHelper;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registry class for the built-in {@link BlockEntityType}s.
 */
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BlueprintBlockEntityTypes {
	public static final BlockEntitySubRegistryHelper HELPER = Blueprint.REGISTRY_HELPER.getBlockEntitySubHelper();

	public static final RegistryObject<BlockEntityType<BlueprintSignBlockEntity>> SIGN = HELPER.createBlockEntity("sign", BlueprintSignBlockEntity::new, () -> BlockEntitySubRegistryHelper.collectBlocks(IBlueprintSign.class));
	public static final RegistryObject<BlockEntityType<BlueprintBeehiveBlockEntity>> BEEHIVE = HELPER.createBlockEntity("beehive", BlueprintBeehiveBlockEntity::new, BlueprintBeehiveBlock.class);
	public static final RegistryObject<BlockEntityType<BlueprintChestBlockEntity>> CHEST = HELPER.createBlockEntity("chest", BlueprintChestBlockEntity::new, BlueprintChestBlock.class);
	public static final RegistryObject<BlockEntityType<BlueprintTrappedChestBlockEntity>> TRAPPED_CHEST = HELPER.createBlockEntity("trapped_chest", BlueprintTrappedChestBlockEntity::new, BlueprintTrappedChestBlock.class);
}