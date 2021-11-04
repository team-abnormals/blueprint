package core.registry;

import com.teamabnormals.blueprint.common.block.BlueprintBeehiveBlock;
import com.teamabnormals.blueprint.common.block.HedgeBlock;
import com.teamabnormals.blueprint.common.block.chest.BlueprintChestBlock;
import com.teamabnormals.blueprint.common.block.chest.BlueprintTrappedChestBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintStandingSignBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintWallSignBlock;
import com.teamabnormals.blueprint.common.block.thatch.ThatchStairBlock;
import com.teamabnormals.blueprint.common.block.wood.LogBlock;
import com.teamabnormals.blueprint.common.block.wood.WoodPostBlock;
import com.teamabnormals.blueprint.core.util.registry.BlockSubRegistryHelper;
import com.mojang.datafixers.util.Pair;
import common.block.ChunkLoadTestBlock;
import common.block.RotatedVoxelShapeTestBlock;
import common.block.TestEndimatedBlock;
import core.BlueprintTest;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.RegistryObject;

@Mod.EventBusSubscriber(modid = BlueprintTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestBlocks {
	private static final BlockSubRegistryHelper HELPER = BlueprintTest.REGISTRY_HELPER.getBlockSubHelper();

	public static final RegistryObject<Block> BLOCK = HELPER.createBlock("block", () -> new Block(Block.Properties.copy(Blocks.DIRT)), CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final RegistryObject<Block> TEST_LOADER = HELPER.createBlock("test_loader", () -> new ChunkLoadTestBlock(Block.Properties.copy(Blocks.DIRT)), CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final RegistryObject<Block> TEST_ROTATION = HELPER.createBlock("test_rotation", () -> new RotatedVoxelShapeTestBlock(Block.Properties.copy(Blocks.DIRT)), CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final Pair<RegistryObject<BlueprintStandingSignBlock>, RegistryObject<BlueprintWallSignBlock>> SIGNS = HELPER.createSignBlock("test", MaterialColor.COLOR_PINK);
	public static final Pair<RegistryObject<BlueprintChestBlock>, RegistryObject<BlueprintTrappedChestBlock>> CHESTS = HELPER.createCompatChestBlocks("indev", "test_two", MaterialColor.COLOR_PURPLE);

	public static final RegistryObject<BlueprintChestBlock> EXAMPLE_CHEST = HELPER.createChestBlock("test", Block.Properties.copy(Blocks.DIRT), CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final RegistryObject<BlueprintTrappedChestBlock> EXAMPLE_TRAPPED_CHEST = HELPER.createTrappedChestBlock("test", Block.Properties.copy(Blocks.CHEST), CreativeModeTab.TAB_DECORATIONS);

	public static final RegistryObject<Block> LOG_BLOCK = HELPER.createBlock("log_block", () -> new LogBlock(() -> Blocks.STRIPPED_ACACIA_LOG, Block.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)), CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final RegistryObject<Block> BEEHIVE = HELPER.createBlock("example_beehive", () -> new BlueprintBeehiveBlock(Block.Properties.copy(Blocks.DIRT)), CreativeModeTab.TAB_DECORATIONS);
	public static final RegistryObject<Block> TEST_STRIPPED_POST = HELPER.createBlock("test_stripped_post", () -> new WoodPostBlock(Block.Properties.copy(Blocks.DIRT)), CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final RegistryObject<Block> TEST_POST = HELPER.createBlock("test_post", () -> new WoodPostBlock(TEST_STRIPPED_POST, Block.Properties.copy(Blocks.DIRT)), CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final RegistryObject<Block> TEST_HEDGE = HELPER.createBlock("test_hedge", () -> new HedgeBlock(Block.Properties.copy(Blocks.DIRT)), CreativeModeTab.TAB_DECORATIONS);
	public static final RegistryObject<Block> TEST_THATCH_STAIRS = HELPER.createBlock("test_thatch_stairs", () -> new ThatchStairBlock(Blocks.DIRT.defaultBlockState(), Block.Properties.copy(Blocks.DIRT)), CreativeModeTab.TAB_DECORATIONS);

	public static final RegistryObject<Block> TEST_ENDIMATED = HELPER.createBlock("test_endimated", () -> new TestEndimatedBlock(Block.Properties.copy(Blocks.DIRT)), CreativeModeTab.TAB_DECORATIONS);
}
