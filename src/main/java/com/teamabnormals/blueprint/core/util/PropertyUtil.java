package com.teamabnormals.blueprint.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Consumer;

/**
 * A class containing template {@link BlockBehaviour.Properties} and {@link Item.Properties}
 *
 * @author bageldotjpg
 */
public final class PropertyUtil {

	public static Item.Properties stacksOnce() {
		return new Item.Properties().stacksTo(1);
	}

	public static Item.Properties food(FoodProperties food) {
		return new Item.Properties().food(food);
	}

	public static BlockBehaviour.Properties flower() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(Block.OffsetType.XZ);
	}

	public static BlockBehaviour.Properties sapling() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS);
	}

	public static BlockBehaviour.Properties ladder() {
		return BlockBehaviour.Properties.of().forceSolidOff().strength(0.4F).sound(SoundType.LADDER).noOcclusion().pushReaction(PushReaction.DESTROY);
	}

	public static BlockBehaviour.Properties flowerPot(FeatureFlag... featureFlags) {
		BlockBehaviour.Properties blockbehaviour$properties = BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY);
		if (featureFlags.length > 0)
			blockbehaviour$properties = blockbehaviour$properties.requiredFeatures(featureFlags);
		return blockbehaviour$properties;
	}

	public static BlockBehaviour.Properties thatch(MapColor color, SoundType soundType) {
		return BlockBehaviour.Properties.of().mapColor(color).strength(0.5F).sound(soundType).noOcclusion();
	}

	public static boolean never(BlockState state, BlockGetter getter, BlockPos pos) {
		return false;
	}

	public static boolean never(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> entity) {
		return false;
	}

	public static boolean always(BlockState state, BlockGetter getter, BlockPos pos) {
		return true;
	}

	public static boolean always(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> entity) {
		return true;
	}

	public static boolean ocelotOrParrot(BlockState state, BlockGetter reader, BlockPos pos, EntityType<?> entity) {
		return entity == EntityType.OCELOT || entity == EntityType.PARROT;
	}

	public record WoodSetProperties(MapColor woodColor, MapColor barkColor, MapColor leavesColor, Consumer<BlockBehaviour.Properties> basePropertiesConsumer, SoundType sound, SoundType logSound, SoundType leavesSound, SoundType chiseledBookshelfSound, NoteBlockInstrument instrument) {

		public static Builder builder(MapColor woodColor, MapColor barkColor) {
			return new Builder(woodColor, barkColor);
		}

		public static Builder builder(MapColor woodColor) {
			return new Builder(woodColor, woodColor);
		}

		public BlockBehaviour.Properties planks() {
			BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).instrument(this.instrument).strength(2.0F, 3.0F).sound(this.sound);
		}

		public BlockBehaviour.Properties log() {
			BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(state -> state.hasProperty(BlockStateProperties.AXIS) && state.getValue(BlockStateProperties.AXIS) != Axis.Y ? barkColor : woodColor).instrument(this.instrument).strength(2.0F).sound(this.logSound);
		}

		public BlockBehaviour.Properties leaves() {
			return BlockBehaviour.Properties.of().mapColor(this.leavesColor).strength(0.2F).randomTicks().sound(this.leavesSound).noOcclusion().isValidSpawn(PropertyUtil::ocelotOrParrot).isSuffocating(PropertyUtil::never).isViewBlocking(PropertyUtil::never).ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor(PropertyUtil::never);
		}

		public BlockBehaviour.Properties pressurePlate() {
			BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).forceSolidOn().instrument(this.instrument).noCollission().strength(0.5F).sound(this.sound).pushReaction(PushReaction.DESTROY);
		}

		public BlockBehaviour.Properties trapdoor() {
			BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).instrument(this.instrument).strength(3.0F).noOcclusion().isValidSpawn(PropertyUtil::never).sound(this.sound);
		}

		public BlockBehaviour.Properties button() {
			return BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY).sound(this.sound);
		}

		public BlockBehaviour.Properties door() {
			BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).instrument(this.instrument).strength(3.0F).noOcclusion().sound(this.sound).pushReaction(PushReaction.DESTROY);
		}

		public BlockBehaviour.Properties beehive() {
			BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).instrument(this.instrument).strength(0.6F).sound(this.sound);
		}

		public BlockBehaviour.Properties bookshelf() {
			BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).instrument(this.instrument).strength(1.5F).sound(this.sound);
		}

		public BlockBehaviour.Properties chiseledBookshelf() {
			BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).instrument(this.instrument).strength(1.5F).sound(this.chiseledBookshelfSound);
		}

		public BlockBehaviour.Properties ladder() {
			return PropertyUtil.ladder();
		}

		public BlockBehaviour.Properties sapling() {
			return BlockBehaviour.Properties.of().mapColor(this.leavesColor).noCollission().randomTicks().instabreak().sound(this.leavesSound).pushReaction(PushReaction.DESTROY);
		}

		public BlockBehaviour.Properties chest() {
			BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).instrument(this.instrument).strength(2.5F).sound(this.sound);
		}

		public BlockBehaviour.Properties sign() {
			BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return BlockBehaviour.Properties.of().mapColor(this.woodColor).forceSolidOn().instrument(this.instrument).noCollission().strength(1.0F);
		}

		public BlockBehaviour.Properties hangingSign() {
			BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).forceSolidOn().instrument(this.instrument).noCollission().strength(1.0F);
		}

		public BlockBehaviour.Properties leafPile() {
			return BlockBehaviour.Properties.of().mapColor(this.leavesColor).replaceable().noCollission().strength(0.2F).sound(this.leavesSound).ignitedByLava().pushReaction(PushReaction.DESTROY);
		}

		@Deprecated
		public BlockBehaviour.Properties leafCarpet() {
			return BlockBehaviour.Properties.of().mapColor(this.leavesColor).noCollission().strength(0.0F).sound(this.leavesSound).noOcclusion().ignitedByLava();
		}

		@Deprecated
		public BlockBehaviour.Properties post() {
			BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).instrument(this.instrument).strength(2.0F, 3.0F).sound(this.logSound);
		}

		public static final class Builder {
			private final MapColor woodColor;
			private final MapColor barkColor;
			private MapColor leavesColor = MapColor.PLANT;
			private Consumer<BlockBehaviour.Properties> basePropertiesConsumer = BlockBehaviour.Properties::ignitedByLava;
			private SoundType sound = SoundType.WOOD;
			private SoundType logSound = SoundType.WOOD;
			private SoundType leavesSound = SoundType.GRASS;
			private SoundType chiseledBookshelfSound = SoundType.CHISELED_BOOKSHELF;
			private NoteBlockInstrument instrument = NoteBlockInstrument.BASS;

			private Builder(MapColor woodColor, MapColor barkColor) {
				this.woodColor = woodColor;
				this.barkColor = barkColor;
			}

			public Builder basePropertiesConsumer(Consumer<BlockBehaviour.Properties> basePropertiesConsumer) {
				this.basePropertiesConsumer = basePropertiesConsumer;
				return this;
			}

			public Builder fireproof() {
				this.basePropertiesConsumer = block -> BlockBehaviour.Properties.of();
				return this;
			}

			public Builder leavesColor(MapColor color) {
				this.leavesColor = color;
				return this;
			}

			public Builder sound(SoundType soundType) {
				this.sound = soundType;
				return this;
			}

			public Builder logSound(SoundType soundType) {
				this.logSound = soundType;
				return this;
			}

			public Builder leavesSound(SoundType soundType) {
				this.leavesSound = soundType;
				return this;
			}

			public Builder chiseledBookshelfSound(SoundType soundType) {
				this.chiseledBookshelfSound = soundType;
				return this;
			}

			public Builder instrument(NoteBlockInstrument instrument) {
				this.instrument = instrument;
				return this;
			}

			public WoodSetProperties build() {
				return new WoodSetProperties(this.barkColor, this.woodColor, this.leavesColor, this.basePropertiesConsumer, this.sound, this.logSound, this.leavesSound, this.chiseledBookshelfSound, this.instrument);
			}
		}
	}
}
