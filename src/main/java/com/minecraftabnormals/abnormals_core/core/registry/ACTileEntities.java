package com.minecraftabnormals.abnormals_core.core.registry;

import com.minecraftabnormals.abnormals_core.common.blocks.AbnormalsBeehiveBlock;
import com.minecraftabnormals.abnormals_core.common.blocks.chest.AbnormalsChestBlock;
import com.minecraftabnormals.abnormals_core.common.blocks.chest.AbnormalsTrappedChestBlock;
import com.minecraftabnormals.abnormals_core.common.blocks.sign.IAbnormalsSign;
import com.minecraftabnormals.abnormals_core.common.tileentity.AbnormalsBeehiveTileEntity;
import com.minecraftabnormals.abnormals_core.common.tileentity.AbnormalsChestTileEntity;
import com.minecraftabnormals.abnormals_core.common.tileentity.AbnormalsSignTileEntity;
import com.minecraftabnormals.abnormals_core.common.tileentity.AbnormalsTrappedChestTileEntity;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.util.registry.BlockEntitySubRegistryHelper;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.RegistryObject;

@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ACTileEntities {
	public static final BlockEntitySubRegistryHelper HELPER = AbnormalsCore.REGISTRY_HELPER.getTileEntitySubHelper();

	public static final RegistryObject<BlockEntityType<AbnormalsSignTileEntity>> SIGN = HELPER.createBlockEntity("sign", AbnormalsSignTileEntity::new, () -> BlockEntitySubRegistryHelper.collectBlocks(IAbnormalsSign.class));
	public static final RegistryObject<BlockEntityType<AbnormalsBeehiveTileEntity>> BEEHIVE = HELPER.createBlockEntity("beehive", AbnormalsBeehiveTileEntity::new, AbnormalsBeehiveBlock.class);
	public static final RegistryObject<BlockEntityType<AbnormalsChestTileEntity>> CHEST = HELPER.createBlockEntity("chest", AbnormalsChestTileEntity::new, AbnormalsChestBlock.class);
	public static final RegistryObject<BlockEntityType<AbnormalsTrappedChestTileEntity>> TRAPPED_CHEST = HELPER.createBlockEntity("trapped_chest", AbnormalsTrappedChestTileEntity::new, AbnormalsTrappedChestBlock.class);
}