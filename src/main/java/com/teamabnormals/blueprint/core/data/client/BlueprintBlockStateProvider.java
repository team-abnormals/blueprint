package com.teamabnormals.blueprint.core.data.client;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.block.sign.BlueprintCeilingHangingSignBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintStandingSignBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintWallHangingSignBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintWallSignBlock;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.BlockFamily.Variant;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public abstract class BlueprintBlockStateProvider extends BlockStateProvider {

	public BlueprintBlockStateProvider(PackOutput output, String modid, ExistingFileHelper helper) {
		super(output, modid, helper);
	}

	public void block(Block block) {
		this.simpleBlock(block);
		this.blockItem(block);
	}

	public void block(DeferredHolder<Block, ?> block) {
		this.block(block.get());
	}

	public void blockItem(Block block) {
		this.simpleBlockItem(block, new ModelFile.ExistingModelFile(blockTexture(block), this.models().existingFileHelper));
	}

	public void blockItem(DeferredHolder<Block, ?> block) {
		this.blockItem(block.get());
	}

	public void generatedItem(ItemLike item, String type) {
		this.generatedItem(item, item, type);
	}

	public void generatedItem(ItemLike item, ItemLike texture, String type) {
		this.generatedItem(item, prefix(type + "/", BlueprintItemModelProvider.key(texture)));
	}

	public void generatedItem(ItemLike item, ResourceLocation texture) {
		this.itemModels().withExistingParent(BuiltInRegistries.ITEM.getKey(item.asItem()).getPath(), "item/generated").texture("layer0", texture);
	}

	public void cubeBottomTopBlock(DeferredHolder<Block, ?> block) {
		ResourceLocation name = BuiltInRegistries.BLOCK.getKey(block.get());
		this.cubeBottomTopBlock(block, prefix("block/", suffix(name, "_side")), prefix("block/", suffix(name, "_bottom")), prefix("block/", suffix(name, "_top")));
	}

	public void cubeBottomTopBlock(DeferredHolder<Block, ?> block, ResourceLocation sideTexture, ResourceLocation bottomTexture, ResourceLocation topTexture) {
		this.simpleBlock(block.get(), this.models().cubeBottomTop(name(block.get()), sideTexture, bottomTexture, topTexture));
		this.blockItem(block);
	}

	public void cubeColumnBlock(DeferredHolder<Block, ?> block) {
		ResourceLocation name = BuiltInRegistries.BLOCK.getKey(block.get());
		this.cubeColumnBlock(block, prefix("block/", name), prefix("block/", suffix(name, "_top")));
	}

	public void cubeColumnBlock(DeferredHolder<Block, ?> block, ResourceLocation sideTexture, ResourceLocation topTexture) {
		this.simpleBlock(block.get(), this.models().cubeColumn(name(block.get()), sideTexture, topTexture));
		this.blockItem(block);
	}

	public void directionalBlock(DeferredHolder<Block, ?> block, ResourceLocation sideTexture, ResourceLocation bottomTexture, ResourceLocation topTexture) {
		this.directionalBlock(block.get(), models().cubeBottomTop(name(block.get()), sideTexture, bottomTexture, topTexture));
		this.blockItem(block);
	}

	public void directionalBlock(DeferredHolder<Block, ?> block) {
		ResourceLocation blockTexture = blockTexture(block.get());
		this.directionalBlock(block, suffix(blockTexture, "_side"), suffix(blockTexture, "_bottom"), suffix(blockTexture, "_top"));
	}

	public void directionalBlockSharedSide(DeferredHolder<Block, ?> block, DeferredHolder<Block, ?> parent) {
		ResourceLocation parentTexture = blockTexture(parent.get());
		this.directionalBlock(block, suffix(parentTexture, "_side"), suffix(parentTexture, "_bottom"), suffix(blockTexture(block.get()), "_top"));
	}

	public void directionalBlockSharedBottom(DeferredHolder<Block, ?> block, DeferredHolder<Block, ?> parent) {
		ResourceLocation blockTexture = blockTexture(block.get());
		this.directionalBlock(block, suffix(blockTexture, "_side"), suffix(blockTexture(parent.get()), "_bottom"), suffix(blockTexture, "_top"));
	}

	public void crossBlock(DeferredHolder<Block, ?> cross) {
		this.simpleBlock(cross.get(), models().cross(name(cross.get()), blockTexture(cross.get())));
		this.generatedItem(cross.get(), "block");
	}

	public void crossBlockWithPot(DeferredHolder<Block, ?> cross, DeferredHolder<Block, ?> flowerPot, ResourceLocation potTexture) {
		this.crossBlock(cross);
		this.simpleBlock(flowerPot.get(), models().singleTexture(name(flowerPot.get()), ResourceLocation.withDefaultNamespace("block/flower_pot_cross"), "plant", potTexture));
	}

	public void crossBlockWithPot(DeferredHolder<Block, ?> cross, DeferredHolder<Block, ?> flowerPot) {
		this.crossBlockWithPot(cross, flowerPot, blockTexture(cross.get()));
	}

	public void crossBlockWithCustomPot(DeferredHolder<Block, ?> cross, DeferredHolder<Block, ?> flowerPot) {
		this.crossBlockWithPot(cross, flowerPot, blockTexture(flowerPot.get()));
	}

	public void slabBlock(Block block, Block slab) {
		if (slab instanceof SlabBlock slabBlock) {
			this.slabBlock(slabBlock, blockTexture(block), blockTexture(block));
			this.blockItem(slab);
		}
	}

	public void stairsBlock(Block block, Block stairs) {
		if (stairs instanceof StairBlock stairBlock) {
			this.stairsBlock(stairBlock, blockTexture(block));
			this.blockItem(stairs);
		}
	}

	public void wallBlock(Block block, Block wall) {
		if (wall instanceof WallBlock wallBlock) {
			this.wallBlock(wallBlock, blockTexture(block));
			this.itemModels().getBuilder(name(wall)).parent(this.models().wallInventory(name(wall) + "_inventory", blockTexture(block)));
		}
	}

	public void baseBlocks(DeferredHolder<Block, ?> block, DeferredHolder<Block, ?> stairs, DeferredHolder<Block, ?> slab) {
		this.baseBlocks(block.get(), stairs.get(), slab.get(), null);
	}

	public void baseBlocks(DeferredHolder<Block, ?> block, DeferredHolder<Block, ?> stairs, DeferredHolder<Block, ?> slab, DeferredHolder<Block, ?> wall) {
		this.baseBlocks(block.get(), stairs.get(), slab.get(), wall.get());
	}

	public void baseBlocks(Block block, Block stairs, Block slab) {
		this.baseBlocks(block, stairs, slab, null);
	}

	public void baseBlocks(Block block, Block stairs, Block slab, Block wall) {
		this.block(block);
		this.stairsBlock(block, stairs);
		this.slabBlock(block, slab);
		this.wallBlock(block, wall);
	}

	public void fenceBlock(Block block, Block fence) {
		if (fence instanceof FenceBlock fenceBlock) {
			this.fenceBlock(fenceBlock, blockTexture(block));
			this.itemModels().getBuilder(name(fence)).parent(this.models().fenceInventory(name(fence) + "_inventory", blockTexture(block)));
		}
	}

	public void fenceGateBlock(Block block, Block fenceGate) {
		if (fenceGate instanceof FenceGateBlock fenceGateBlock) {
			this.fenceGateBlock(fenceGateBlock, blockTexture(block));
			this.blockItem(fenceGate);
		}
	}

	public void fenceBlocks(Block block, Block fence, Block fenceGate) {
		this.fenceBlock(block, fence);
		this.fenceGateBlock(block, fenceGate);
	}

	public void doorBlock(Block door) {
		if (door instanceof DoorBlock doorBlock) {
			this.doorBlock(doorBlock, suffix(blockTexture(door), "_bottom"), suffix(blockTexture(door), "_top"));
			this.generatedItem(door, "item");
		}
	}

	public void trapDoorBlock(Block trapDoor) {
		if (trapDoor instanceof TrapDoorBlock trapDoorBlock) {
			this.trapdoorBlock(trapDoorBlock, blockTexture(trapDoor), true);
			this.itemModels().getBuilder(name(trapDoor)).parent(this.models().trapdoorOrientableBottom(name(trapDoor) + "_bottom", blockTexture(trapDoor)));
		}
	}

	public void doorBlocks(Block door, Block trapdoor) {
		this.doorBlock(door);
		this.trapDoorBlock(trapdoor);
	}

	public void signBlocks(DeferredHolder<Block, ?> planks, Pair<DeferredHolder<? extends BlueprintStandingSignBlock, ?>, DeferredHolder<? extends BlueprintWallSignBlock, ?>> signs) {
		this.hangingSignBlocks(planks, signs.getFirst(), signs.getSecond());
	}

	public void signBlocks(Block block, Block sign, Block wallSign) {
		if (sign != null && wallSign != null) {
			ModelFile model = particle(sign, blockTexture(block));
			this.simpleBlock(sign, model);
			this.generatedItem(sign, "item");
			this.simpleBlock(wallSign, model);
		}
	}

	public void hangingSignBlocks(DeferredHolder<Block, ?> strippedLog, Pair<DeferredHolder<? extends BlueprintCeilingHangingSignBlock, ?>, DeferredHolder<? extends BlueprintWallHangingSignBlock, ?>> hangingSigns) {
		this.hangingSignBlocks(strippedLog, hangingSigns.getFirst(), hangingSigns.getSecond());
	}

	public void hangingSignBlocks(DeferredHolder<Block, ?> strippedLog, DeferredHolder<? extends Block, ?> sign, DeferredHolder<? extends Block, ?> wallSign) {
		ModelFile model = particle(sign, blockTexture(strippedLog.get()));
		this.simpleBlock(sign.get(), model);
		this.generatedItem(sign.get(), "item");
		this.simpleBlock(wallSign.get(), model);
	}

	public void buttonBlock(Block block, Block button) {
		this.buttonBlock(button, blockTexture(block));
	}

	public void buttonBlock(Block block, ResourceLocation texture) {
		ModelFile button = models().button(name(block), texture);
		ModelFile buttonPressed = models().buttonPressed(name(block) + "_pressed", texture);
		ModelFile buttonInventoryModel = models().buttonInventory(name(block) + "_inventory", texture);
		if (block instanceof ButtonBlock buttonBlock) {
			this.buttonBlock(buttonBlock, button, buttonPressed);
		}

		this.itemModels().getBuilder(name(block)).parent(buttonInventoryModel);
	}

	public void pressurePlateBlock(Block block, Block pressurePlate) {
		this.pressurePlateBlock(pressurePlate, blockTexture(block));
	}

	public void pressurePlateBlock(Block block, ResourceLocation texture) {
		ModelFile pressurePlate = models().pressurePlate(name(block), texture);
		ModelFile pressurePlateDown = models().pressurePlateDown(name(block) + "_down", texture);
		this.getVariantBuilder(block)
				.partialState().with(PressurePlateBlock.POWERED, true).addModels(new ConfiguredModel(pressurePlateDown))
				.partialState().with(PressurePlateBlock.POWERED, false).addModels(new ConfiguredModel(pressurePlate));
		this.blockItem(block);
	}

	public void boardsBlock(DeferredHolder<Block, ?> boards) {
		ModelFile boardsModel = models().getBuilder(name(boards.get())).parent(new ModelFile.UncheckedModelFile(Blueprint.location("block/template_boards"))).texture("all", blockTexture(boards.get()));
		ModelFile boardsHorizontalModel = models().getBuilder(name(boards.get()) + "_horizontal").parent(new ModelFile.UncheckedModelFile(Blueprint.location("block/template_boards_horizontal"))).texture("all", blockTexture(boards.get()));
		this.getVariantBuilder(boards.get()).partialState().with(RotatedPillarBlock.AXIS, Axis.Y).modelForState().modelFile(boardsModel).addModel().partialState().with(RotatedPillarBlock.AXIS, Axis.Z).modelForState().modelFile(boardsHorizontalModel).addModel().partialState().with(RotatedPillarBlock.AXIS, Axis.X).modelForState().modelFile(boardsHorizontalModel).rotationY(270).addModel();
		this.blockItem(boards);
	}

	public void bookshelfBlock(DeferredHolder<Block, ?> planks, DeferredHolder<Block, ?> bookshelf) {
		this.bookshelfBlock(planks.get(), bookshelf);
	}

	public void bookshelfBlock(Block planks, DeferredHolder<Block, ?> bookshelf) {
		this.simpleBlock(bookshelf.get(), this.models().cubeColumn(name(bookshelf.get()), blockTexture(bookshelf.get()), blockTexture(planks)));
		this.blockItem(bookshelf);
	}

	public static final String[] DEFAULT_BOOKSHELF_POSITIONS = new String[]{"top_left", "top_mid", "top_right", "bottom_left", "bottom_mid", "bottom_right"};
	public static final String[] ALTERNATE_BOOKSHELF_POSITIONS = new String[]{"top_left", "top_right", "mid_left", "mid_right", "bottom_left", "bottom_right"};
	public static final String[] BOTTOM_BOOKSHELF_POSITIONS = new String[]{"top_left", "top_right", "bottom_left", "bottom_mid_left", "bottom_mid_right", "bottom_right"};

	public void chiseledBookshelfBlock(DeferredHolder<Block, ?> registryObject) {
		chiseledBookshelfBlock(registryObject, DEFAULT_BOOKSHELF_POSITIONS, ResourceLocation.withDefaultNamespace("template_chiseled_bookshelf"));
	}

	public void chiseledBookshelfBlock(DeferredHolder<Block, ?> registryObject, String[] parts) {
		chiseledBookshelfBlock(registryObject, parts, blockTexture(registryObject.get()));
	}

	public void chiseledBookshelfBlock(DeferredHolder<Block, ?> registryObject, String[] parts, ResourceLocation parent) {
		Block chiseledBookshelf = registryObject.get();
		String name = name(chiseledBookshelf);
		ResourceLocation texture = blockTexture(chiseledBookshelf);

		BlockModelBuilder model = this.models().withExistingParent(name, "block/chiseled_bookshelf").texture("top", texture + "_top").texture("side", texture + "_side");
		MultiPartBlockStateBuilder builder = getMultipartBuilder(chiseledBookshelf);
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			int rotation = (int) (direction.toYRot() + 180) % 360;
			builder.part().modelFile(model).rotationY(rotation).uvLock(true).addModel().condition(HorizontalDirectionalBlock.FACING, direction);
			for (int i = 0; i < 6; i++) {
				String part = parts[i];
				BooleanProperty property = ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i);
				ResourceLocation slot = suffix(parent, "_slot_" + part);

				builder.part().modelFile(this.models().withExistingParent(name + "_occupied_slot_" + part, slot).texture("texture", texture + "_occupied"))
						.rotationY(rotation).uvLock(true).addModel()
						.condition(HorizontalDirectionalBlock.FACING, direction).condition(property, true);
				builder.part().modelFile(this.models().withExistingParent(name + "_empty_slot_" + part, slot).texture("texture", texture + "_empty"))
						.rotationY(rotation).uvLock(true).addModel()
						.condition(HorizontalDirectionalBlock.FACING, direction).condition(property, false);
			}
		}

		this.simpleBlockItem(chiseledBookshelf, this.models().withExistingParent(name + "_inventory", "block/chiseled_bookshelf_inventory").texture("top", texture + "_top").texture("side", texture + "_side").texture("front", texture + "_empty"));
	}

	public void ladderBlock(DeferredHolder<Block, ?> ladder) {
		this.horizontalBlock(ladder.get(), models().withExistingParent(name(ladder.get()), "block/ladder").texture("particle", blockTexture(ladder.get())).renderType("cutout").texture("texture", blockTexture(ladder.get())));
		this.generatedItem(ladder.get(), "block");
	}

	public void chestBlocks(DeferredHolder<Block, ?> planks, DeferredHolder<? extends Block, ?> chest, DeferredHolder<? extends Block, ?> trappedChest) {
		this.chestBlocks(planks.get(), chest, trappedChest);
	}

	public void chestBlocks(Block planks, DeferredHolder<? extends Block, ?> chest, DeferredHolder<? extends Block, ?> trappedChest) {
		ModelFile model = particle(chest, blockTexture(planks));
		this.simpleBlock(chest.get(), model);
		this.simpleBlock(trappedChest.get(), model);
		this.simpleBlockItem(chest.get(), new ModelFile.UncheckedModelFile(Blueprint.location("item/template_chest")));
		this.simpleBlockItem(trappedChest.get(), new ModelFile.UncheckedModelFile(Blueprint.location("item/template_chest")));
	}

	public void beehiveBlock(DeferredHolder<Block, ?> registryObject) {
		Block block = registryObject.get();
		ModelFile beehive = models().orientableWithBottom(name(block), suffix(blockTexture(block), "_side"), suffix(blockTexture(block), "_front"), suffix(blockTexture(block), "_end"), suffix(blockTexture(block), "_end")).texture("particle", suffix(blockTexture(block), "_side"));
		ModelFile beehiveHoney = models().orientableWithBottom(name(block) + "_honey", suffix(blockTexture(block), "_side"), suffix(blockTexture(block), "_front_honey"), suffix(blockTexture(block), "_end"), suffix(blockTexture(block), "_end")).texture("particle", suffix(blockTexture(block), "_side"));
		this.horizontalBlock(block, (state -> state.getValue(BlockStateProperties.LEVEL_HONEY) == 5 ? beehiveHoney : beehive));
		this.blockItem(block);
	}

	public void logBlocks(DeferredHolder<Block, ?> log, DeferredHolder<Block, ?> wood) {
		this.logBlock(log);
		this.woodBlock(wood, log);
	}

	public void woodBlock(DeferredHolder<Block, ?> block, DeferredHolder<Block, ?> log) {
		this.logBlock(block, blockTexture(log.get()), blockTexture(log.get()));
	}

	public void logBlock(DeferredHolder<Block, ?> block) {
		this.logBlock(block, blockTexture(block.get()), suffix(blockTexture(block.get()), "_top"));
	}

	public void logBlock(DeferredHolder<Block, ?> block, ResourceLocation sideTexture, ResourceLocation topTexture) {
		if (block.get() instanceof RotatedPillarBlock log) {
			this.axisBlock(log, sideTexture, topTexture);
			this.blockItem(block);
		}
	}

	public void leavesBlock(DeferredHolder<Block, ?> leaves) {
		this.simpleBlock(leaves.get(), models().getBuilder(name(leaves.get())).parent(new ModelFile.UncheckedModelFile(ResourceLocation.withDefaultNamespace("block/leaves"))).renderType("cutout_mipped").texture("all", blockTexture(leaves.get())));
		this.blockItem(leaves);
	}

	public void leafPileBlock(DeferredHolder<Block, ?> leaves, DeferredHolder<Block, ?> leafPile) {
		this.leafPileBlock(leaves.get(), leafPile, true);
	}

	public void leafPileBlock(DeferredHolder<Block, ?> leaves, DeferredHolder<Block, ?> leafPile, boolean tint) {
		this.leafPileBlock(leaves.get(), leafPile, tint);
	}

	public void leafPileBlock(Block leaves, DeferredHolder<Block, ?> leafPile) {
		this.leafPileBlock(leaves, leafPile, true);
	}

	public void leafPileBlock(Block leaves, DeferredHolder<Block, ?> leafPile, boolean tint) {
		this.leafPileBlock(leafPile, blockTexture(leaves), tint);
	}

	public void leafPileBlock(DeferredHolder<Block, ?> leafPile, ResourceLocation texture, boolean tint) {
		ModelFile leafPileModel = models().getBuilder(name(leafPile.get())).parent(new ModelFile.UncheckedModelFile(Blueprint.location("block/" + (tint ? "tinted_" : "") + "leaf_pile"))).renderType("cutout").texture("all", texture);
		MultiPartBlockStateBuilder builder = getMultipartBuilder(leafPile.get());
		builder.part().modelFile(leafPileModel).rotationX(270).uvLock(true).addModel().condition(BlockStateProperties.UP, true);
		builder.part().modelFile(leafPileModel).rotationX(270).uvLock(true).addModel().condition(BlockStateProperties.UP, false).condition(BlockStateProperties.NORTH, false).condition(BlockStateProperties.WEST, false).condition(BlockStateProperties.SOUTH, false).condition(BlockStateProperties.EAST, false).condition(BlockStateProperties.DOWN, false);
		builder.part().modelFile(leafPileModel).addModel().condition(BlockStateProperties.NORTH, true);
		builder.part().modelFile(leafPileModel).addModel().condition(BlockStateProperties.UP, false).condition(BlockStateProperties.NORTH, false).condition(BlockStateProperties.WEST, false).condition(BlockStateProperties.SOUTH, false).condition(BlockStateProperties.EAST, false).condition(BlockStateProperties.DOWN, false);
		builder.part().modelFile(leafPileModel).rotationY(270).uvLock(true).addModel().condition(BlockStateProperties.WEST, true);
		builder.part().modelFile(leafPileModel).rotationY(270).uvLock(true).addModel().condition(BlockStateProperties.UP, false).condition(BlockStateProperties.NORTH, false).condition(BlockStateProperties.WEST, false).condition(BlockStateProperties.SOUTH, false).condition(BlockStateProperties.EAST, false).condition(BlockStateProperties.DOWN, false);
		builder.part().modelFile(leafPileModel).rotationY(180).uvLock(true).addModel().condition(BlockStateProperties.SOUTH, true);
		builder.part().modelFile(leafPileModel).rotationY(180).uvLock(true).addModel().condition(BlockStateProperties.UP, false).condition(BlockStateProperties.NORTH, false).condition(BlockStateProperties.WEST, false).condition(BlockStateProperties.SOUTH, false).condition(BlockStateProperties.EAST, false).condition(BlockStateProperties.DOWN, false);
		builder.part().modelFile(leafPileModel).rotationY(90).uvLock(true).addModel().condition(BlockStateProperties.EAST, true);
		builder.part().modelFile(leafPileModel).rotationY(90).uvLock(true).addModel().condition(BlockStateProperties.UP, false).condition(BlockStateProperties.NORTH, false).condition(BlockStateProperties.WEST, false).condition(BlockStateProperties.SOUTH, false).condition(BlockStateProperties.EAST, false).condition(BlockStateProperties.DOWN, false);
		builder.part().modelFile(leafPileModel).rotationX(90).uvLock(true).addModel().condition(BlockStateProperties.DOWN, true);
		builder.part().modelFile(leafPileModel).rotationX(90).uvLock(true).addModel().condition(BlockStateProperties.UP, false).condition(BlockStateProperties.NORTH, false).condition(BlockStateProperties.WEST, false).condition(BlockStateProperties.SOUTH, false).condition(BlockStateProperties.EAST, false).condition(BlockStateProperties.DOWN, false);
		this.generatedItem(leafPile.get(), texture);
	}

	public void leavesBlocks(DeferredHolder<Block, ?> leaves, DeferredHolder<Block, ?> leafPile) {
		this.leavesBlocks(leaves, leafPile, true);
	}

	public void leavesBlocks(DeferredHolder<Block, ?> leaves, DeferredHolder<Block, ?> leafPile, boolean tinted) {
		this.leavesBlock(leaves);
		this.leafPileBlock(leaves, leafPile, tinted);
	}

	public void ironBarsBlock(DeferredHolder<Block, ?> bars) {
		this.ironBarsBlock(bars.get(), blockTexture(bars.get()));
		this.generatedItem(bars.get(), "block");
	}

	public void ironBarsBlock(Block block, ResourceLocation texture) {
		String name = name(block);
		ResourceLocation edgeTexture = suffix(texture, "_edge");

		ModelFile post = ironBarsBlock(name, "post", texture).texture("bars", edgeTexture);
		ModelFile postEnds = ironBarsBlock(name, "post_ends", texture).texture("edge", edgeTexture);
		ModelFile side = ironBarsBlock(name, "side", texture).texture("bars", texture).texture("edge", edgeTexture);
		ModelFile sideAlt = ironBarsBlock(name, "side_alt", texture).texture("bars", texture).texture("edge", edgeTexture);
		ModelFile cap = ironBarsBlock(name, "cap", texture).texture("bars", texture).texture("edge", edgeTexture);
		ModelFile capAlt = ironBarsBlock(name, "cap_alt", texture).texture("bars", texture).texture("edge", edgeTexture);

		this.paneBlock(block, post, postEnds, side, sideAlt, cap, capAlt);
	}

	public BlockModelBuilder ironBarsBlock(String name, String suffix, ResourceLocation barsTexture) {
		return models().getBuilder(name + "_" + suffix).parent(new ModelFile.UncheckedModelFile(ResourceLocation.withDefaultNamespace("block/iron_bars_" + suffix))).texture("particle", barsTexture);
	}

	public void paneBlock(Block block, ModelFile post, ModelFile postEnds, ModelFile side, ModelFile sideAlt, ModelFile cap, ModelFile capAlt) {
		MultiPartBlockStateBuilder builder = getMultipartBuilder(block).part().modelFile(postEnds).addModel().end();
		builder.part().modelFile(post).addModel().condition(BlockStateProperties.NORTH, false).condition(BlockStateProperties.WEST, false).condition(BlockStateProperties.SOUTH, false).condition(BlockStateProperties.EAST, false).end();

		for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
			builder.part().modelFile(direction == Direction.SOUTH || direction == Direction.WEST ? capAlt : cap).rotationY(direction.getAxis() == Axis.X ? 90 : 0).addModel()
					.condition(BlockStateProperties.NORTH, PipeBlock.PROPERTY_BY_DIRECTION.get(direction) == BlockStateProperties.NORTH)
					.condition(BlockStateProperties.WEST, PipeBlock.PROPERTY_BY_DIRECTION.get(direction) == BlockStateProperties.WEST)
					.condition(BlockStateProperties.SOUTH, PipeBlock.PROPERTY_BY_DIRECTION.get(direction) == BlockStateProperties.SOUTH)
					.condition(BlockStateProperties.EAST, PipeBlock.PROPERTY_BY_DIRECTION.get(direction) == BlockStateProperties.EAST)
					.end();

		}

		PipeBlock.PROPERTY_BY_DIRECTION.forEach((dir, value) -> {
			if (dir.getAxis().isHorizontal()) {
				builder.part().modelFile(dir == Direction.SOUTH || dir == Direction.WEST ? sideAlt : side).rotationY(dir.getAxis() == Axis.X ? 90 : 0).addModel().condition(value, true).end();
			}
		});
	}

	public void brushableBlock(DeferredHolder<Block, ?> registryObject) {
		Block block = registryObject.get();
		ModelFile[] models = new ModelFile[4];
		for (int i = 0; i < 4; i++) {
			models[i] = this.models().cubeAll(name(block) + "_" + i, suffix(blockTexture(block), "_" + i));
		}
		this.getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(models[state.getValue(BlockStateProperties.DUSTED)]).build());
		this.simpleBlockItem(block, models[0]);
	}

	public ModelFile particle(Block block, ResourceLocation texture) {
		return this.models().getBuilder(name(block)).texture("particle", texture);
	}

	public ModelFile particle(DeferredHolder<? extends Block, ?> block, ResourceLocation texture) {
		return this.particle(block.get(), texture);
	}

	public static String name(Block block) {
		return BuiltInRegistries.BLOCK.getKey(block).getPath();
	}

	public static ResourceLocation prefix(String prefix, ResourceLocation rl) {
		return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), prefix + rl.getPath());
	}

	public static ResourceLocation suffix(ResourceLocation rl, String suffix) {
		return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), rl.getPath() + suffix);
	}

	public static ResourceLocation remove(ResourceLocation rl, String remove) {
		return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), rl.getPath().replace(remove, ""));
	}

	public void woodworksBlocks(Block planks, DeferredHolder<Block, ?> boards, DeferredHolder<Block, ?> ladder, DeferredHolder<Block, ?> bookshelf, DeferredHolder<Block, ?> beehive, DeferredHolder<? extends Block, ?> chest, DeferredHolder<? extends Block, ?> trappedChest) {
		this.boardsBlock(boards);
		this.ladderBlock(ladder);
		this.beehiveBlock(beehive);
		this.bookshelfBlock(planks, bookshelf);
		this.chestBlocks(planks, chest, trappedChest);
	}

	public void woodworksBlocks(DeferredHolder<Block, ?> planks, DeferredHolder<Block, ?> boards, DeferredHolder<Block, ?> ladder, DeferredHolder<Block, ?> bookshelf, DeferredHolder<Block, ?> beehive, DeferredHolder<? extends Block, ?> chest, DeferredHolder<? extends Block, ?> trappedChest) {
		this.woodworksBlocks(planks.get(), boards, ladder, bookshelf, beehive, chest, trappedChest);
	}

	public void blockFamily(BlockFamily family) {
		Block block = family.getBaseBlock();

		this.baseBlocks(block, family.get(Variant.STAIRS), family.get(Variant.SLAB), family.get(Variant.WALL));
		this.fenceBlocks(block, family.get(Variant.FENCE), family.get(Variant.FENCE_GATE));
		this.doorBlocks(family.get(Variant.DOOR), family.get(Variant.TRAPDOOR));
		this.signBlocks(block, family.get(Variant.SIGN), family.get(Variant.WALL_SIGN));

		if (family.get(Variant.BUTTON) instanceof ButtonBlock button) {
			this.buttonBlock(block, button);
		}

		if (family.get(Variant.PRESSURE_PLATE) instanceof BasePressurePlateBlock pressurePlate) {
			this.pressurePlateBlock(block, pressurePlate);
		}
	}
}