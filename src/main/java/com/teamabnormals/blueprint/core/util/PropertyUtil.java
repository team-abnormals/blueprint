package com.teamabnormals.blueprint.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

/**
 * A class containing template {@link Block.Properties} and {@link Item.Properties}
 *
 * @author bageldotjpg
 */
public final class PropertyUtil {
	@Deprecated public static final Item.Properties TOOL = new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS);
	@Deprecated public static final Item.Properties MISC_TOOL = new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC);

	public static Item.Properties tool() {
		return new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS);
	}

	public static Item.Properties miscTool() {
		return new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC);
	}

	public static Item.Properties food(FoodProperties food) {
		return new Item.Properties().food(food).tab(CreativeModeTab.TAB_FOOD);
	}

	@Deprecated public static final Block.Properties FLOWER = Block.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(Block.OffsetType.XZ);
	@Deprecated public static final Block.Properties SAPLING = Block.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS);
	@Deprecated public static final Block.Properties LADDER = Block.Properties.of(Material.DECORATION).strength(0.4F).sound(SoundType.LADDER).noOcclusion();
	@Deprecated public static final Block.Properties FLOWER_POT = Block.Properties.of(Material.DECORATION).instabreak().noOcclusion();

	public static Block.Properties flower() {
		return Block.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(Block.OffsetType.XZ);
	}

	public static Block.Properties sapling() {
		return Block.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS);
	}

	public static Block.Properties ladder() {
		return Block.Properties.of(Material.DECORATION).strength(0.4F).sound(SoundType.LADDER).noOcclusion();
	}

	public static Block.Properties flowerPot() {
		return Block.Properties.of(Material.DECORATION).instabreak().noOcclusion();
	}

	public static Block.Properties thatch(MaterialColor color, SoundType soundType) {
		return Block.Properties.of(Material.GRASS, color).strength(0.5F).sound(soundType).noOcclusion();
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

	public record WoodSetProperties(MaterialColor woodColor, MaterialColor leavesColor, Material material, SoundType sound, SoundType logSound, SoundType leavesSound) {

		public static Builder builder(MaterialColor woodColor) {
			return new Builder(woodColor);
		}

		public Block.Properties planks() {
			return Block.Properties.of(this.material, this.woodColor).strength(2.0F, 3.0F).sound(this.sound);
		}

		public Block.Properties log() {
			return Block.Properties.of(this.material, this.woodColor).strength(2.0F).sound(this.logSound);
		}

		public Block.Properties leaves() {
			return Block.Properties.of(Material.LEAVES, this.leavesColor).strength(0.2F).randomTicks().sound(this.leavesSound).noOcclusion().isValidSpawn(PropertyUtil::ocelotOrParrot).isSuffocating(PropertyUtil::never).isViewBlocking(PropertyUtil::never);
		}

		public Block.Properties pressurePlate() {
			return Block.Properties.of(this.material, this.woodColor).noCollission().strength(0.5F).sound(this.sound);
		}

		public Block.Properties trapdoor() {
			return Block.Properties.of(this.material, this.woodColor).strength(3.0F).sound(this.sound).noOcclusion().isValidSpawn(PropertyUtil::never);
		}

		public Block.Properties button() {
			return Block.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(this.sound);
		}

		public Block.Properties door() {
			return Block.Properties.of(this.material, this.woodColor).strength(3.0F).sound(this.sound).noOcclusion();
		}

		public Block.Properties beehive() {
			return Block.Properties.of(this.material, this.woodColor).strength(0.6F).sound(this.sound);
		}

		public Block.Properties bookshelf() {
			return Block.Properties.of(this.material, this.woodColor).strength(1.5F).sound(this.sound);
		}

		public Block.Properties ladder() {
			return PropertyUtil.ladder();
		}

		public Block.Properties sapling() {
			return Block.Properties.of(Material.PLANT, this.leavesColor).noCollission().randomTicks().instabreak().sound(this.leavesSound);
		}

		public Block.Properties chest() {
			return Block.Properties.of(this.material, this.woodColor).strength(2.5F).sound(this.sound);
		}

		public Block.Properties leafPile() {
			return Block.Properties.of(Material.REPLACEABLE_PLANT, this.leavesColor).noCollission().strength(0.2F).sound(this.leavesSound);
		}

		public Block.Properties leafCarpet() {
			return Block.Properties.of(Material.CLOTH_DECORATION, this.leavesColor).strength(0.0F).sound(this.leavesSound).noOcclusion();
		}

		public Block.Properties post() {
			return Block.Properties.of(this.material, this.woodColor).strength(2.0F, 3.0F).sound(this.logSound);
		}

		public static final class Builder {
			private MaterialColor woodColor;
			private MaterialColor leavesColor = MaterialColor.PLANT;
			private Material material = Material.WOOD;
			private SoundType sound = SoundType.WOOD;
			private SoundType logSound = SoundType.WOOD;
			private SoundType leavesSound = SoundType.GRASS;

			private Builder(MaterialColor woodColor) {
				this.woodColor = woodColor;
			}

			public Builder material(Material material) {
				this.material = material;
				return this;
			}

			public Builder woodColor(MaterialColor woodColor) {
				this.woodColor = woodColor;
				return this;
			}

			public Builder leavesColor(MaterialColor color) {
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
				return new WoodSetProperties(this.woodColor, this.leavesColor, this.material, this.sound, this.logSound, this.leavesSound);
			}
		}
	}
}
