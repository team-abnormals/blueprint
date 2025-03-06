package core.registry;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.block.BlueprintBeehiveBlock;
import com.teamabnormals.blueprint.common.block.LogBlock;
import com.teamabnormals.blueprint.common.block.chest.BlueprintChestBlock;
import com.teamabnormals.blueprint.common.block.chest.BlueprintTrappedChestBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintCeilingHangingSignBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintStandingSignBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintWallHangingSignBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintWallSignBlock;
import com.teamabnormals.blueprint.common.block.thatch.ThatchStairBlock;
import com.teamabnormals.blueprint.core.api.WoodTypeRegistryHelper;
import com.teamabnormals.blueprint.core.events.LoadThisClassEvent;
import com.teamabnormals.blueprint.core.util.PropertyUtil;
import com.teamabnormals.blueprint.core.util.PropertyUtil.WoodSetProperties;
import com.teamabnormals.blueprint.core.util.registry.BlockSubRegistryHelper;
import common.block.TestEndimatedBlock;
import common.block.TestFallingBlock;
import core.BlueprintTest;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredBlock;

@EventBusSubscriber(modid = BlueprintTest.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class TestBlocks {
	@SubscribeEvent
	public static void $(LoadThisClassEvent event) {}

	private static final BlockSubRegistryHelper HELPER = BlueprintTest.REGISTRY_HELPER.getBlockSubHelper();
	private static final WoodType TEST_WOOD_TYPE = WoodTypeRegistryHelper.registerWoodType(new WoodType(BlueprintTest.MOD_ID + ":test", BlockSetType.OAK));
	public static final WoodSetProperties TEST_WOOD_SET = WoodSetProperties.builder(MapColor.TERRACOTTA_PINK).sound(SoundType.AMETHYST).build();

	public static final DeferredBlock<Block> BLOCK = HELPER.createBlock("block", () -> new Block(Block.Properties.ofFullCopy(Blocks.DIRT)));
	public static final Pair<DeferredBlock<BlueprintStandingSignBlock>, DeferredBlock<BlueprintWallSignBlock>> SIGNS = HELPER.createSignBlock("test", TEST_WOOD_TYPE, TEST_WOOD_SET.sign());
	public static final Pair<DeferredBlock<BlueprintCeilingHangingSignBlock>, DeferredBlock<BlueprintWallHangingSignBlock>> HANGING_SIGNS = HELPER.createHangingSignBlock("test", TEST_WOOD_TYPE, TEST_WOOD_SET.hangingSign());

	public static final DeferredBlock<BlueprintChestBlock> EXAMPLE_CHEST = HELPER.createChestBlock("test", TEST_WOOD_SET.chest());
	public static final DeferredBlock<BlueprintTrappedChestBlock> EXAMPLE_TRAPPED_CHEST = HELPER.createTrappedChestBlock("test", TEST_WOOD_SET.chest());

	public static final DeferredBlock<LogBlock> LOG_BLOCK = HELPER.createBlock("log_block", () -> new LogBlock(() -> Blocks.STRIPPED_ACACIA_LOG, Block.Properties.of().ignitedByLava().mapColor(MapColor.COLOR_ORANGE)));
	public static final DeferredBlock<BlueprintBeehiveBlock> BEEHIVE = HELPER.createBlock("example_beehive", () -> new BlueprintBeehiveBlock(TEST_WOOD_SET.beehive()));
	public static final DeferredBlock<ThatchStairBlock> TEST_THATCH_STAIRS = HELPER.createBlock("test_thatch_stairs", () -> new ThatchStairBlock(Blocks.DIRT.defaultBlockState(), PropertyUtil.thatch(MapColor.COLOR_YELLOW, SoundType.AMETHYST)));
	public static final DeferredBlock<TestFallingBlock> TEST_FALLING = HELPER.createBlock("test_falling", () -> new TestFallingBlock(Block.Properties.ofFullCopy(Blocks.DIRT)));

	public static final DeferredBlock<TestEndimatedBlock> TEST_ENDIMATED = HELPER.createBlock("test_endimated", () -> new TestEndimatedBlock(Block.Properties.ofFullCopy(Blocks.DIRT)));
}
