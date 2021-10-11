package com.minecraftabnormals.abnormals_core.core.registry;

import com.minecraftabnormals.abnormals_core.common.blockentity.AbnormalsBeehiveBlockEntity;
import com.minecraftabnormals.abnormals_core.common.blockentity.AbnormalsChestBlockEntity;
import com.minecraftabnormals.abnormals_core.common.blockentity.AbnormalsSignBlockEntity;
import com.minecraftabnormals.abnormals_core.common.blockentity.AbnormalsTrappedChestBlockEntity;
import com.minecraftabnormals.abnormals_core.common.blocks.AbnormalsBeehiveBlock;
import com.minecraftabnormals.abnormals_core.common.blocks.chest.AbnormalsChestBlock;
import com.minecraftabnormals.abnormals_core.common.blocks.chest.AbnormalsTrappedChestBlock;
import com.minecraftabnormals.abnormals_core.common.blocks.sign.IAbnormalsSign;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.util.registry.BlockEntitySubRegistryHelper;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.RegistryObject;

/**
 * Registry class for the built-in {@link BlockEntityType}s.
 */
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ACBlockEntities {
	public static final BlockEntitySubRegistryHelper HELPER = AbnormalsCore.REGISTRY_HELPER.getBlockEntitySubHelper();

	public static final RegistryObject<BlockEntityType<AbnormalsSignBlockEntity>> SIGN = HELPER.createBlockEntity("sign", AbnormalsSignBlockEntity::new, () -> BlockEntitySubRegistryHelper.collectBlocks(IAbnormalsSign.class));
	public static final RegistryObject<BlockEntityType<AbnormalsBeehiveBlockEntity>> BEEHIVE = HELPER.createBlockEntity("beehive", AbnormalsBeehiveBlockEntity::new, AbnormalsBeehiveBlock.class);
	public static final RegistryObject<BlockEntityType<AbnormalsChestBlockEntity>> CHEST = HELPER.createBlockEntity("chest", AbnormalsChestBlockEntity::new, AbnormalsChestBlock.class);
	public static final RegistryObject<BlockEntityType<AbnormalsTrappedChestBlockEntity>> TRAPPED_CHEST = HELPER.createBlockEntity("trapped_chest", AbnormalsTrappedChestBlockEntity::new, AbnormalsTrappedChestBlock.class);
}