package com.minecraftabnormals.abnormals_core.core.registry;

import com.minecraftabnormals.abnormals_core.common.blocks.AbnormalsBeehiveBlock;
import com.minecraftabnormals.abnormals_core.common.blocks.sign.AbnormalsAbstractSignBlock;
import com.minecraftabnormals.abnormals_core.common.blocks.chest.AbnormalsChestBlock;
import com.minecraftabnormals.abnormals_core.common.blocks.chest.AbnormalsTrappedChestBlock;
import com.minecraftabnormals.abnormals_core.common.tileentity.*;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;

import com.minecraftabnormals.abnormals_core.core.util.registry.TileEntitySubRegistryHelper;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ACTileEntities {
	public static final TileEntitySubRegistryHelper HELPER = AbnormalsCore.REGISTRY_HELPER.getTileEntitySubHelper();

	public static final RegistryObject<TileEntityType<AbnormalsSignTileEntity>> SIGN = HELPER.createTileEntity("sign", AbnormalsSignTileEntity::new, AbnormalsAbstractSignBlock.class);
	public static final RegistryObject<TileEntityType<AbnormalsBeehiveTileEntity>> BEEHIVE = HELPER.createTileEntity("beehive", AbnormalsBeehiveTileEntity::new, AbnormalsBeehiveBlock.class);
	public static final RegistryObject<TileEntityType<AbnormalsChestTileEntity>> CHEST = HELPER.createTileEntity("chest", AbnormalsChestTileEntity::new, AbnormalsChestBlock.class);
	public static final RegistryObject<TileEntityType<AbnormalsTrappedChestTileEntity>> TRAPPED_CHEST = HELPER.createTileEntity("trapped_chest", AbnormalsTrappedChestTileEntity::new, AbnormalsTrappedChestBlock.class);
}