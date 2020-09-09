package com.teamabnormals.abnormals_core.core.registry;

import com.teamabnormals.abnormals_core.common.blocks.AbnormalsBeehiveBlock;
import com.teamabnormals.abnormals_core.common.blocks.ScentedCandleBlock;
import com.teamabnormals.abnormals_core.common.blocks.chest.AbnormalsChestBlock;
import com.teamabnormals.abnormals_core.common.blocks.chest.AbnormalsTrappedChestBlock;
import com.teamabnormals.abnormals_core.common.blocks.sign.AbnormalsAbstractSignBlock;
import com.teamabnormals.abnormals_core.common.tileentity.*;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import com.teamabnormals.abnormals_core.core.util.RegistryHelper;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ACTileEntities {
	public static final RegistryHelper HELPER = AbnormalsCore.REGISTRY_HELPER;

	public static final RegistryObject<TileEntityType<AbnormalsSignTileEntity>> SIGN = HELPER.createTileEntity("sign", AbnormalsSignTileEntity::new, () -> collectBlocks(AbnormalsAbstractSignBlock.class));
	public static final RegistryObject<TileEntityType<AbnormalsBeehiveTileEntity>> BEEHIVE = HELPER.createTileEntity("beehive", AbnormalsBeehiveTileEntity::new, () -> collectBlocks(AbnormalsBeehiveBlock.class));
	public static final RegistryObject<TileEntityType<AbnormalsChestTileEntity>> CHEST = HELPER.createTileEntity("chest", AbnormalsChestTileEntity::new, () -> collectBlocks(AbnormalsChestBlock.class));
	public static final RegistryObject<TileEntityType<AbnormalsTrappedChestTileEntity>> TRAPPED_CHEST = HELPER.createTileEntity("trapped_chest", AbnormalsTrappedChestTileEntity::new, () -> collectBlocks(AbnormalsTrappedChestBlock.class));
	public static final RegistryObject<TileEntityType<ScentedCandleTileEntity>> SCENTED_CANDLE = HELPER.createTileEntity("scented_candle", ScentedCandleTileEntity::new, () -> collectBlocks(ScentedCandleBlock.class));

	private static Block[] collectBlocks(Class<? extends Block> blockClass) {
		return ForgeRegistries.BLOCKS.getValues().stream().filter(block -> blockClass.isInstance(block)).toArray(Block[]::new);
	}
}