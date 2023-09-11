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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Consumer;

/**
 * A class containing template {@link Block.Properties} and {@link Item.Properties}
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

	public static Block.Properties flower() {
		return Block.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(Block.OffsetType.XZ);
	}

	public static Block.Properties sapling() {
		return Block.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS);
	}

	public static Block.Properties ladder() {
		return BlockBehaviour.Properties.of().forceSolidOff().strength(0.4F).sound(SoundType.LADDER).noOcclusion().pushReaction(PushReaction.DESTROY);
	}

	public static Block.Properties flowerPot(FeatureFlag... featureFlags) {
		BlockBehaviour.Properties blockbehaviour$properties = BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY);
		if (featureFlags.length > 0)
			blockbehaviour$properties = blockbehaviour$properties.requiredFeatures(featureFlags);
		return blockbehaviour$properties;
	}

	public static Block.Properties thatch(MapColor color, SoundType soundType) {
		return Block.Properties.of().mapColor(color).strength(0.5F).sound(soundType).noOcclusion();
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

	public record WoodSetProperties(MapColor woodColor, MapColor barkColor, MapColor leavesColor, Consumer<Block.Properties> basePropertiesConsumer, SoundType sound, SoundType logSound, SoundType leavesSound) {

		public static Builder builder(MapColor woodColor, MapColor barkColor) {
			return new Builder(woodColor, barkColor);
		}

		public static Builder builder(MapColor woodColor) {
			return new Builder(woodColor, woodColor);
		}

		public Block.Properties planks() {
			BlockBehaviour.Properties properties = Block.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).strength(2.0F, 3.0F).sound(this.sound);
		}

		public Block.Properties log() {
			BlockBehaviour.Properties properties = Block.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(state -> state.hasProperty(BlockStateProperties.AXIS) && state.getValue(BlockStateProperties.AXIS) != Axis.Y ? barkColor : woodColor).strength(2.0F).sound(this.logSound);
		}

		public Block.Properties leaves() {
			return BlockBehaviour.Properties.of().mapColor(this.leavesColor).strength(0.2F).randomTicks().sound(this.leavesSound).noOcclusion().isValidSpawn(PropertyUtil::ocelotOrParrot).isSuffocating(PropertyUtil::never).isViewBlocking(PropertyUtil::never).ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor(PropertyUtil::never);
		}

		public Block.Properties pressurePlate() {
			BlockBehaviour.Properties properties = Block.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).noCollission().strength(0.5F).sound(this.sound);
		}

		public Block.Properties trapdoor() {
			BlockBehaviour.Properties properties = Block.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).strength(3.0F).sound(this.sound).noOcclusion().isValidSpawn(PropertyUtil::never);
		}

		public Block.Properties button() {
			return BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY).sound(this.sound);
		}

		public Block.Properties door() {
			BlockBehaviour.Properties properties = Block.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).strength(3.0F).sound(this.sound).noOcclusion();
		}

		public Block.Properties beehive() {
			BlockBehaviour.Properties properties = Block.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).strength(0.6F).sound(this.sound);
		}

		public Block.Properties bookshelf() {
			BlockBehaviour.Properties properties = Block.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).strength(1.5F).sound(this.sound);
		}

		public Block.Properties ladder() {
			return PropertyUtil.ladder();
		}

		public Block.Properties sapling() {
			return BlockBehaviour.Properties.of().mapColor(this.leavesColor).noCollission().randomTicks().instabreak().sound(this.leavesSound).pushReaction(PushReaction.DESTROY);
		}

		public Block.Properties chest() {
			BlockBehaviour.Properties properties = Block.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).strength(2.5F).sound(this.sound);
		}

		public Block.Properties leafPile() {
			return Block.Properties.of().mapColor(this.leavesColor).replaceable().noCollission().ignitedByLava().pushReaction(PushReaction.DESTROY).strength(0.2F).sound(this.leavesSound);
		}

		public Block.Properties leafCarpet() {
			return Block.Properties.of().mapColor(this.leavesColor).noCollission().ignitedByLava().strength(0.0F).sound(this.leavesSound).noOcclusion();
		}

		public Block.Properties post() {
			BlockBehaviour.Properties properties = Block.Properties.of();
			this.basePropertiesConsumer.accept(properties);
			return properties.mapColor(this.woodColor).strength(2.0F, 3.0F).sound(this.logSound);
		}

		public static final class Builder {
			private MapColor woodColor;
			private MapColor barkColor;
			private MapColor leavesColor = MapColor.PLANT;
			private Consumer<Block.Properties> basePropertiesConsumer = BlockBehaviour.Properties::ignitedByLava;
			private SoundType sound = SoundType.WOOD;
			private SoundType logSound = SoundType.WOOD;
			private SoundType leavesSound = SoundType.GRASS;

			private Builder(MapColor woodColor, MapColor barkColor) {
				this.woodColor = woodColor;
				this.barkColor = barkColor;
			}

			public Builder basePropertiesConsumer(Consumer<Block.Properties> basePropertiesConsumer) {
				this.basePropertiesConsumer = basePropertiesConsumer;
				return this;
			}

			@Deprecated
			public Builder woodColor(MapColor woodColor) {
				this.woodColor = woodColor;
				return this;
			}

			@Deprecated
			public Builder barkColor(MapColor barkColor) {
				this.barkColor = woodColor;
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

			public WoodSetProperties build() {
				return new WoodSetProperties(this.barkColor, this.woodColor, this.leavesColor, this.basePropertiesConsumer, this.sound, this.logSound, this.leavesSound);
			}
		}
	}
}
