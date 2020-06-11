package com.teamabnormals.abnormals_core.core.examples;

import com.teamabnormals.abnormals_core.common.blocks.AbnormalsBeehiveBlock;
import com.teamabnormals.abnormals_core.common.blocks.sign.AbnormalsAbstractSignBlock;
import com.teamabnormals.abnormals_core.common.tileentity.AbnormalsBeehiveTileEntity;
import com.teamabnormals.abnormals_core.common.tileentity.AbnormalsSignTileEntity;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import com.teamabnormals.abnormals_core.core.utils.RegistryHelper;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ExampleTileEntityRegistry {
	public static final RegistryHelper HELPER = AbnormalsCore.REGISTRY_HELPER;
	
	public static final RegistryObject<TileEntityType<AbnormalsSignTileEntity>> SIGN = HELPER.createTileEntity("sign", AbnormalsSignTileEntity::new, () -> collectSignBlocks());
	public static final RegistryObject<TileEntityType<AbnormalsBeehiveTileEntity>> BEEHIVE = HELPER.createTileEntity("beehive", AbnormalsBeehiveTileEntity::new, () -> collectBeehiveBlocks());

	private static Block[] collectSignBlocks() {
		return ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block instanceof AbnormalsAbstractSignBlock).toArray(Block[]::new);
	}
	
	private static Block[] collectBeehiveBlocks() {
		return ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block instanceof AbnormalsBeehiveBlock).toArray(Block[]::new);
	}
}