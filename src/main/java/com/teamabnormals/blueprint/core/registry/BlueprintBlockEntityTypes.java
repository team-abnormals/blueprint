package com.teamabnormals.blueprint.core.registry;

import com.teamabnormals.blueprint.common.block.entity.AbnormalsBeehiveBlockEntity;
import com.teamabnormals.blueprint.common.block.entity.AbnormalsChestBlockEntity;
import com.teamabnormals.blueprint.common.block.entity.AbnormalsSignBlockEntity;
import com.teamabnormals.blueprint.common.block.entity.AbnormalsTrappedChestBlockEntity;
import com.teamabnormals.blueprint.common.block.AbnormalsBeehiveBlock;
import com.teamabnormals.blueprint.common.block.chest.AbnormalsChestBlock;
import com.teamabnormals.blueprint.common.block.chest.AbnormalsTrappedChestBlock;
import com.teamabnormals.blueprint.common.block.sign.IAbnormalsSign;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.registry.BlockEntitySubRegistryHelper;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.RegistryObject;

/**
 * Registry class for the built-in {@link BlockEntityType}s.
 */
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BlueprintBlockEntityTypes {
	public static final BlockEntitySubRegistryHelper HELPER = Blueprint.REGISTRY_HELPER.getBlockEntitySubHelper();

	public static final RegistryObject<BlockEntityType<AbnormalsSignBlockEntity>> SIGN = HELPER.createBlockEntity("sign", AbnormalsSignBlockEntity::new, () -> BlockEntitySubRegistryHelper.collectBlocks(IAbnormalsSign.class));
	public static final RegistryObject<BlockEntityType<AbnormalsBeehiveBlockEntity>> BEEHIVE = HELPER.createBlockEntity("beehive", AbnormalsBeehiveBlockEntity::new, AbnormalsBeehiveBlock.class);
	public static final RegistryObject<BlockEntityType<AbnormalsChestBlockEntity>> CHEST = HELPER.createBlockEntity("chest", AbnormalsChestBlockEntity::new, AbnormalsChestBlock.class);
	public static final RegistryObject<BlockEntityType<AbnormalsTrappedChestBlockEntity>> TRAPPED_CHEST = HELPER.createBlockEntity("trapped_chest", AbnormalsTrappedChestBlockEntity::new, AbnormalsTrappedChestBlock.class);
}