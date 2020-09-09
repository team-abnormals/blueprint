package core.registry;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.abnormals_core.common.blocks.AbnormalsBeehiveBlock;
import com.teamabnormals.abnormals_core.common.blocks.ScentedCandleBlock;
import com.teamabnormals.abnormals_core.common.blocks.chest.AbnormalsChestBlock;
import com.teamabnormals.abnormals_core.common.blocks.chest.AbnormalsTrappedChestBlock;
import com.teamabnormals.abnormals_core.common.blocks.sign.AbnormalsStandingSignBlock;
import com.teamabnormals.abnormals_core.common.blocks.sign.AbnormalsWallSignBlock;
import com.teamabnormals.abnormals_core.common.blocks.wood.AbnormalsLogBlock;
import com.teamabnormals.abnormals_core.core.annotations.Test;
import com.teamabnormals.abnormals_core.core.util.RegistryHelper;
import common.blocks.ChunkLoadTestBlock;
import common.blocks.RotatedVoxelShapeTestBlock;
import core.ACTest;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Test
@Mod.EventBusSubscriber(modid = ACTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestBlocks {
	private static final RegistryHelper HELPER = ACTest.REGISTRY_HELPER;

	public static final RegistryObject<Block> BLOCK = HELPER.createBlock("block", () -> new Block(Block.Properties.from(Blocks.DIRT)), ItemGroup.BUILDING_BLOCKS);
	public static final RegistryObject<Block> TEST_LOADER = HELPER.createBlock("test_loader", () -> new ChunkLoadTestBlock(Block.Properties.from(Blocks.DIRT)), ItemGroup.BUILDING_BLOCKS);
	public static final RegistryObject<Block> TEST_ROTATION = HELPER.createBlock("test_rotation", () -> new RotatedVoxelShapeTestBlock(Block.Properties.from(Blocks.DIRT)), ItemGroup.BUILDING_BLOCKS);
	public static final Pair<RegistryObject<AbnormalsStandingSignBlock>, RegistryObject<AbnormalsWallSignBlock>> SIGNS = HELPER.createSignBlock("test", MaterialColor.PINK);
	public static final Pair<RegistryObject<AbnormalsChestBlock>, RegistryObject<AbnormalsTrappedChestBlock>> CHESTS = HELPER.createCompatChestBlocks("test_two", MaterialColor.PURPLE);

	public static final RegistryObject<AbnormalsChestBlock> EXAMPLE_CHEST = HELPER.createChestBlock("test", Block.Properties.from(Blocks.DIRT), ItemGroup.BUILDING_BLOCKS);
	public static final RegistryObject<AbnormalsTrappedChestBlock> EXAMPLE_TRAPPED_CHEST = HELPER.createTrappedChestBlock("test", Block.Properties.from(Blocks.CHEST), ItemGroup.DECORATIONS);

	public static final RegistryObject<Block> LOG_BLOCK = HELPER.createBlock("log_block", () -> new AbnormalsLogBlock(() -> Blocks.STRIPPED_ACACIA_LOG, AbstractBlock.Properties.create(Material.WOOD, MaterialColor.ADOBE)), ItemGroup.BUILDING_BLOCKS);
	public static final RegistryObject<Block> BEEHIVE = HELPER.createBlock("example_beehive", () -> new AbnormalsBeehiveBlock(Block.Properties.from(Blocks.DIRT)), ItemGroup.BUILDING_BLOCKS);
	public static final RegistryObject<Block> SCENTED_CANDLE = HELPER.createBlock("test_candle", () -> new ScentedCandleBlock(Block.Properties.from(Blocks.DIRT), () -> Effects.BLINDNESS), ItemGroup.BUILDING_BLOCKS);
}
