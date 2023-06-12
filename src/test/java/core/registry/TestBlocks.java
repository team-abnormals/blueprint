package core.registry;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.block.BlueprintBeehiveBlock;
import com.teamabnormals.blueprint.common.block.HedgeBlock;
import com.teamabnormals.blueprint.common.block.chest.BlueprintChestBlock;
import com.teamabnormals.blueprint.common.block.chest.BlueprintTrappedChestBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintCeilingHangingSignBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintStandingSignBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintWallHangingSignBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintWallSignBlock;
import com.teamabnormals.blueprint.common.block.thatch.ThatchStairBlock;
import com.teamabnormals.blueprint.common.block.wood.LogBlock;
import com.teamabnormals.blueprint.common.block.wood.WoodPostBlock;
import com.teamabnormals.blueprint.core.api.WoodTypeRegistryHelper;
import com.teamabnormals.blueprint.core.util.PropertyUtil;
import com.teamabnormals.blueprint.core.util.PropertyUtil.WoodSetProperties;
import com.teamabnormals.blueprint.core.util.registry.BlockSubRegistryHelper;
import common.block.ChunkLoadTestBlock;
import common.block.RotatedVoxelShapeTestBlock;
import common.block.TestEndimatedBlock;
import common.block.TestFallingBlock;
import core.BlueprintTest;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = BlueprintTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestBlocks {
	private static final BlockSubRegistryHelper HELPER = BlueprintTest.REGISTRY_HELPER.getBlockSubHelper();
	private static final WoodType TEST_WOOD_TYPE = WoodTypeRegistryHelper.registerWoodType(new WoodType(BlueprintTest.MOD_ID + ":test", BlockSetType.OAK));
	public static final WoodSetProperties TEST_WOOD_SET = WoodSetProperties.builder(MapColor.TERRACOTTA_PINK).sound(SoundType.AMETHYST).build();

	public static final RegistryObject<Block> BLOCK = HELPER.createBlock("block", () -> new Block(Block.Properties.copy(Blocks.DIRT)));
	public static final RegistryObject<Block> TEST_LOADER = HELPER.createBlock("test_loader", () -> new ChunkLoadTestBlock(Block.Properties.copy(Blocks.DIRT)));
	public static final RegistryObject<Block> TEST_ROTATION = HELPER.createBlock("test_rotation", () -> new RotatedVoxelShapeTestBlock(Block.Properties.copy(Blocks.DIRT)));
	public static final Pair<RegistryObject<BlueprintStandingSignBlock>, RegistryObject<BlueprintWallSignBlock>> SIGNS = HELPER.createSignBlock("test", TEST_WOOD_TYPE, MapColor.COLOR_PINK);
	public static final Pair<RegistryObject<BlueprintCeilingHangingSignBlock>, RegistryObject<BlueprintWallHangingSignBlock>> HANGING_SIGNS = HELPER.createHangingSignBlock("test", TEST_WOOD_TYPE, MapColor.COLOR_PINK);

	public static final RegistryObject<BlueprintChestBlock> EXAMPLE_CHEST = HELPER.createChestBlock("test", TEST_WOOD_SET.chest());
	public static final RegistryObject<BlueprintTrappedChestBlock> EXAMPLE_TRAPPED_CHEST = HELPER.createTrappedChestBlock("test", TEST_WOOD_SET.chest());

	public static final RegistryObject<Block> LOG_BLOCK = HELPER.createBlock("log_block", () -> new LogBlock(() -> Blocks.STRIPPED_ACACIA_LOG, Block.Properties.of().ignitedByLava().mapColor(MapColor.COLOR_ORANGE)));
	public static final RegistryObject<Block> BEEHIVE = HELPER.createBlock("example_beehive", () -> new BlueprintBeehiveBlock(TEST_WOOD_SET.beehive()));
	public static final RegistryObject<Block> TEST_STRIPPED_POST = HELPER.createBlock("test_stripped_post", () -> new WoodPostBlock(Block.Properties.copy(Blocks.DIRT)));
	public static final RegistryObject<Block> TEST_POST = HELPER.createBlock("test_post", () -> new WoodPostBlock(TEST_STRIPPED_POST, Block.Properties.copy(Blocks.DIRT)));
	public static final RegistryObject<Block> TEST_HEDGE = HELPER.createBlock("test_hedge", () -> new HedgeBlock(TEST_WOOD_SET.planks()));
	public static final RegistryObject<Block> TEST_THATCH_STAIRS = HELPER.createBlock("test_thatch_stairs", () -> new ThatchStairBlock(Blocks.DIRT.defaultBlockState(), PropertyUtil.thatch(MapColor.COLOR_YELLOW, SoundType.AMETHYST)));
	public static final RegistryObject<Block> TEST_FALLING = HELPER.createBlock("test_falling", () -> new TestFallingBlock(Block.Properties.copy(Blocks.DIRT)));

	public static final RegistryObject<Block> TEST_ENDIMATED = HELPER.createBlock("test_endimated", () -> new TestEndimatedBlock(Block.Properties.copy(Blocks.DIRT)));
}
