package com.teamabnormals.abnormals_core.core.examples;

import java.util.concurrent.Callable;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.abnormals_core.client.renderer.ChestItemRenderer;
import com.teamabnormals.abnormals_core.common.blocks.AbnormalsBeehiveBlock;
import com.teamabnormals.abnormals_core.common.blocks.chest.AbnormalsChestBlock;
import com.teamabnormals.abnormals_core.common.blocks.chest.AbnormalsTrappedChestBlock;
import com.teamabnormals.abnormals_core.common.blocks.sign.AbnormalsStandingSignBlock;
import com.teamabnormals.abnormals_core.common.blocks.sign.AbnormalsWallSignBlock;
import com.teamabnormals.abnormals_core.common.blocks.test.ChunkLoadTestBlock;
import com.teamabnormals.abnormals_core.common.blocks.test.RotatedVoxelShapeTestBlock;
import com.teamabnormals.abnormals_core.common.blocks.wood.AbnormalsLogBlock;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import com.teamabnormals.abnormals_core.core.library.Test;
import com.teamabnormals.abnormals_core.core.utils.RegistryHelper;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.BrewingStandTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Test
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ExampleBlockRegistry {
	public static final RegistryHelper HELPER = AbnormalsCore.REGISTRY_HELPER;
	
	public static final RegistryObject<Block> BLOCK = HELPER.createBlock("block", () -> new Block(Block.Properties.from(Blocks.DIRT)), ItemGroup.BUILDING_BLOCKS);
	public static final RegistryObject<Block> TEST_LOADER = HELPER.createBlock("test_loader", () -> new ChunkLoadTestBlock(Block.Properties.from(Blocks.DIRT)), ItemGroup.BUILDING_BLOCKS);
	public static final RegistryObject<Block> TEST_ROTATION = HELPER.createBlock("test_rotation", () -> new RotatedVoxelShapeTestBlock(Block.Properties.from(Blocks.DIRT)), ItemGroup.BUILDING_BLOCKS);
	public static final Pair<RegistryObject<AbnormalsStandingSignBlock>, RegistryObject<AbnormalsWallSignBlock>> SIGNS = HELPER.createSignBlock("example", MaterialColor.PINK);
	
	public static final RegistryObject<Block> EXAMPLE_CHEST = HELPER.createBlockWithISTER("example_chest", () -> new AbnormalsChestBlock("example", () -> ExampleTileEntityRegistry.CHEST.get(), Block.Properties.from(Blocks.DIRT)), () -> chestISTER(), ItemGroup.BUILDING_BLOCKS);
	public static final RegistryObject<Block> EXAMPLE_CHEST_TWO = HELPER.createBlockWithISTER("example_chest_two", () -> new AbnormalsChestBlock("example_two", () -> ExampleTileEntityRegistry.CHEST.get(), Block.Properties.from(Blocks.DIRT)), () -> chestISTER(), ItemGroup.BUILDING_BLOCKS);
	public static final RegistryObject<Block> EXAMPLE_TRAPPED_CHEST = HELPER.createBlockWithISTER("example_trapped_chest", () -> new AbnormalsTrappedChestBlock("example", () -> ExampleTileEntityRegistry.TRAPPED_CHEST.get(), Block.Properties.from(Blocks.DIRT)), () -> chestISTER(), ItemGroup.BUILDING_BLOCKS);

	public static final RegistryObject<Block> LOG_BLOCK = HELPER.createBlock("log_block", () -> new AbnormalsLogBlock(() -> Blocks.STRIPPED_ACACIA_LOG, MaterialColor.GREEN, Block.Properties.from(Blocks.DIRT)), ItemGroup.BUILDING_BLOCKS);
	public static final RegistryObject<Block> BEEHIVE = HELPER.createBlock("example_beehive", () -> new AbnormalsBeehiveBlock(Block.Properties.from(Blocks.DIRT)), ItemGroup.BUILDING_BLOCKS);

	@OnlyIn(Dist.CLIENT)
	//TODO: Make it not crash when using AbnormalsChestTileEntity
	private static Callable<ItemStackTileEntityRenderer> chestISTER() {
		return () -> new ChestItemRenderer<TileEntity>(BrewingStandTileEntity::new);
	}
}