package com.teamabnormals.blueprint.core.util.registry;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.client.ChestManager;
import com.teamabnormals.blueprint.client.renderer.block.ChestBlockEntityWithoutLevelRenderer;
import com.teamabnormals.blueprint.common.block.chest.BlueprintChestBlock;
import com.teamabnormals.blueprint.common.block.chest.BlueprintTrappedChestBlock;
import com.teamabnormals.blueprint.common.block.entity.BlueprintChestBlockEntity;
import com.teamabnormals.blueprint.common.block.entity.BlueprintTrappedChestBlockEntity;
import com.teamabnormals.blueprint.common.block.sign.BlueprintStandingSignBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintWallSignBlock;
import com.teamabnormals.blueprint.common.item.BEWLRBlockItem;
import com.teamabnormals.blueprint.common.item.BEWLRFuelBlockItem;
import com.teamabnormals.blueprint.common.item.FuelBlockItem;
import com.teamabnormals.blueprint.common.item.InjectedBlockItem;
import com.teamabnormals.blueprint.core.api.SignManager;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * A basic {@link AbstractSubRegistryHelper} for blocks. This contains some useful registering methods for blocks.
 *
 * @author SmellyModder (Luke Tonon)
 * @see AbstractSubRegistryHelper
 */
public class BlockSubRegistryHelper extends AbstractSubRegistryHelper<Block> {
	protected final DeferredRegister<Item> itemRegister;

	public BlockSubRegistryHelper(RegistryHelper parent) {
		this(parent, parent.getSubHelper(ForgeRegistries.ITEMS).getDeferredRegister(), DeferredRegister.create(ForgeRegistries.BLOCKS, parent.getModId()));
	}

	public BlockSubRegistryHelper(RegistryHelper parent, ISubRegistryHelper<Item> itemHelper) {
		this(parent, itemHelper.getDeferredRegister(), DeferredRegister.create(ForgeRegistries.BLOCKS, parent.getModId()));
	}

	public BlockSubRegistryHelper(RegistryHelper parent, DeferredRegister<Item> itemRegister, DeferredRegister<Block> deferredRegister) {
		super(parent, deferredRegister);
		this.itemRegister = itemRegister;
	}

	@OnlyIn(Dist.CLIENT)
	private static BEWLRBlockItem.LazyBEWLR chestBEWLR(boolean trapped) {
		return trapped ? new BEWLRBlockItem.LazyBEWLR((dispatcher, entityModelSet) -> {
			return new ChestBlockEntityWithoutLevelRenderer<>(dispatcher, entityModelSet, new BlueprintTrappedChestBlockEntity(BlockPos.ZERO, Blocks.TRAPPED_CHEST.defaultBlockState()));
		}) : new BEWLRBlockItem.LazyBEWLR((dispatcher, entityModelSet) -> {
			return new ChestBlockEntityWithoutLevelRenderer<>(dispatcher, entityModelSet, new BlueprintChestBlockEntity(BlockPos.ZERO, Blocks.CHEST.defaultBlockState()));
		});
	}

	/**
	 * Creates and registers a {@link Block} with a {@link BlockItem}.
	 *
	 * @param name     The block's name.
	 * @param supplier The supplied {@link Block}.
	 * @param group    The {@link CreativeModeTab} for the {@link BlockItem}.
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createBlock(String name, Supplier<? extends B> supplier, @Nullable CreativeModeTab group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(group)));
		return block;
	}

	/**
	 * Creates and registers a {@link Block} with a {@link BlockItem} with custom {@link Item.Properties}.
	 *
	 * @param name       The block's name.
	 * @param supplier   The supplied {@link Block}.
	 * @param properties The {@link Item.Properties} for the {@link BlockItem}.
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createBlock(String name, Supplier<? extends B> supplier, Item.Properties properties) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new BlockItem(block.get(), properties));
		return block;
	}

	/**
	 * Creates and registers a {@link Block} with a specified {@link BlockItem}.
	 *
	 * @param name     The block's name.
	 * @param supplier The supplied {@link Block}.
	 * @param item     The {@link BlockItem} for this {@link Block}.
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createBlockWithItem(String name, Supplier<? extends B> supplier, Supplier<BlockItem> item) {
		this.itemRegister.register(name, item);
		return this.deferredRegister.register(name, supplier);
	}

	/**
	 * Creates and registers a {@link Block} with no {@link BlockItem}.
	 *
	 * @param name     The block's name.
	 * @param supplier The supplied Block.
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createBlockNoItem(String name, Supplier<? extends B> supplier) {
		return this.deferredRegister.register(name, supplier);
	}

	/**
	 * Creates and registers a {@link Block} with its {@link BlockItem} that can be used as fuel.
	 *
	 * @param name     The block's name.
	 * @param supplier The supplied {@link Block}.
	 * @param burnTime How long the item will burn (measured in ticks).
	 * @param group    The {@link CreativeModeTab} for the {@link BlockItem}.
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createFuelBlock(String name, Supplier<? extends B> supplier, int burnTime, @Nullable CreativeModeTab group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new FuelBlockItem(block.get(), burnTime, new Item.Properties().tab(group)));
		return block;
	}

	/**
	 * Creates and registers a {@link Block} with an {@link InjectedBlockItem}.
	 *
	 * @param name     The block's name.
	 * @param supplier The supplied {@link Block}.
	 * @param group    The {@link CreativeModeTab} for the {@link InjectedBlockItem}.
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createInjectedBlock(String name, Item followItem, Supplier<? extends B> supplier, @Nullable CreativeModeTab group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new InjectedBlockItem(followItem, block.get(), new Item.Properties().tab(group)));
		return block;
	}

	/**
	 * Creates and registers a {@link Block} with a {@link BlockItem} with a {@link BlockEntityWithoutLevelRenderer}.
	 *
	 * @param name     The block's name.
	 * @param supplier The supplied {@link Block}.
	 * @param belwr    A supplier for getting the {@link BlockEntityWithoutLevelRenderer} for the {@link BlockItem}.
	 * @param group    The {@link CreativeModeTab} for the {@link BlockItem}.
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createBlockWithBEWLR(String name, Supplier<? extends B> supplier, Supplier<Callable<BEWLRBlockItem.LazyBEWLR>> belwr, @Nullable CreativeModeTab group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new BEWLRBlockItem(block.get(), new Item.Properties().tab(group), belwr));
		return block;
	}

	/**
	 * Creates and registers a {@link Block} with a {@link DoubleHighBlockItem}.
	 *
	 * @param name     The block's name.
	 * @param supplier The supplied {@link Block}.
	 * @param group    The {@link CreativeModeTab} for the {@link DoubleHighBlockItem}.
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 * @see DoubleHighBlockItem
	 */
	public <B extends Block> RegistryObject<B> createDoubleHighBlock(String name, Supplier<? extends B> supplier, CreativeModeTab group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new DoubleHighBlockItem(block.get(), new Item.Properties().tab(group)));
		return block;
	}

	/**
	 * Creates and registers {@link Block} with a {@link StandingAndWallBlockItem}.
	 *
	 * @param name         The block's name.
	 * @param supplier     The supplied floor {@link Block}.
	 * @param wallSupplier The supplied wall {@link Block}.
	 * @param group        The {@link CreativeModeTab} for the {@link StandingAndWallBlockItem}.
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 * @see StandingAndWallBlockItem
	 */
	public <B extends Block> RegistryObject<B> createStandingAndWallBlock(String name, Supplier<? extends B> supplier, Supplier<? extends B> wallSupplier, @Nullable CreativeModeTab group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new StandingAndWallBlockItem(block.get(), wallSupplier.get(), new Item.Properties().tab(group)));
		return block;
	}

	/**
	 * Creates and registers {@link Block} with a {@link BlockItem} that has {@link Rarity}.
	 *
	 * @param name   The block's name.
	 * @param rarity The {@link Rarity} of the {@link BlockItem}.
	 * @param group  The {@link CreativeModeTab} for the {@link BlockItem}.
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createRareBlock(String name, Supplier<? extends B> supplier, Rarity rarity, @Nullable CreativeModeTab group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new BlockItem(block.get(), new Item.Properties().rarity(rarity).tab(group)));
		return block;
	}

	/**
	 * Creates and registers {@link BlueprintChestBlock} with a {@link BEWLRFuelBlockItem}.
	 *
	 * @param name       The name for this {@link BlueprintChestBlock}.
	 * @param properties The properties for this {@link BlueprintChestBlock}.
	 * @param group      The CreativeModeTab for the BlockItem.
	 * @return A {@link RegistryObject} containing the created {@link BlueprintChestBlock}.
	 */
	public RegistryObject<BlueprintChestBlock> createChestBlock(String name, Block.Properties properties, @Nullable CreativeModeTab group) {
		String modId = this.parent.getModId();
		RegistryObject<BlueprintChestBlock> block = this.deferredRegister.register(name + "_chest", () -> new BlueprintChestBlock(modId + ":" + name, properties));
		ChestManager.putChestInfo(modId, name, false);
		this.itemRegister.register(name + "_chest", () -> new BEWLRFuelBlockItem(block.get(), new Item.Properties().tab(group), () -> () -> chestBEWLR(false), 300));
		return block;
	}

	/**
	 * Creates and registers {@link BlueprintTrappedChestBlock} with a {@link BEWLRFuelBlockItem}.
	 *
	 * @param name       The name for this {@link BlueprintTrappedChestBlock}.
	 * @param properties The properties for this {@link BlueprintTrappedChestBlock}.
	 * @param group      The CreativeModeTab for the BlockItem.
	 * @return A {@link RegistryObject} containing the created {@link BlueprintTrappedChestBlock}.
	 */
	public RegistryObject<BlueprintTrappedChestBlock> createTrappedChestBlock(String name, Block.Properties properties, @Nullable CreativeModeTab group) {
		String modId = this.parent.getModId();
		RegistryObject<BlueprintTrappedChestBlock> block = this.deferredRegister.register(name + "_trapped_chest", () -> new BlueprintTrappedChestBlock(modId + ":" + name + "_trapped", properties));
		ChestManager.putChestInfo(modId, name, true);
		this.itemRegister.register(name + "_trapped_chest", () -> new BEWLRFuelBlockItem(block.get(), new Item.Properties().tab(group), () -> () -> chestBEWLR(true), 300));
		return block;
	}

	/**
	 * Creates and registers a {@link BlueprintStandingSignBlock} and a {@link BlueprintWallSignBlock} with a {@link SignItem}.
	 *
	 * @param name  The name for the sign blocks.
	 * @param color The {@link MaterialColor} for the sign blocks.
	 * @return A {@link Pair} containing {@link RegistryObject}s of the {@link BlueprintStandingSignBlock} and the {@link BlueprintWallSignBlock}.
	 */
	public Pair<RegistryObject<BlueprintStandingSignBlock>, RegistryObject<BlueprintWallSignBlock>> createSignBlock(String name, MaterialColor color) {
		WoodType type = SignManager.registerWoodType(WoodType.create(this.parent.getModId() + ":" + name));
		RegistryObject<BlueprintStandingSignBlock> standing = this.deferredRegister.register(name + "_sign", () -> new BlueprintStandingSignBlock(Block.Properties.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundType.WOOD), type));
		RegistryObject<BlueprintWallSignBlock> wall = this.deferredRegister.register(name + "_wall_sign", () -> new BlueprintWallSignBlock(Block.Properties.of(Material.WOOD, color).noCollission().strength(1.0F).sound(SoundType.WOOD).dropsLike(standing.get()), type));
		this.itemRegister.register(name + "_sign", () -> new SignItem(new Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS), standing.get(), wall.get()));
		return Pair.of(standing, wall);
	}

	/**
	 * Creates and registers a compat {@link Block}.
	 *
	 * @param modId    The mod id of the mod this block is compatible for, set to "indev" for dev tests.
	 * @param name     The block's name.
	 * @param supplier The supplied {@link Block}.
	 * @param group    The {@link CreativeModeTab} for the {@link BlockItem}.
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createCompatBlock(String modId, String name, Supplier<? extends B> supplier, @Nullable CreativeModeTab group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(areModsLoaded(modId) ? group : null)));
		return block;
	}

	/**
	 * Creates and registers a compat {@link Block}.
	 *
	 * @param name     The block's name.
	 * @param supplier The supplied {@link Block}.
	 * @param group    The {@link CreativeModeTab} for the {@link BlockItem}.
	 * @param modIds   The mod ids of the mods this block is compatible for.
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createCompatBlock(String name, Supplier<? extends B> supplier, @Nullable CreativeModeTab group, String... modIds) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(areModsLoaded(modIds) ? group : null)));
		return block;
	}

	/**
	 * Creates and registers a compat {@link Block} with a {@link FuelBlockItem}.
	 *
	 * @param modId    The modId of the mod this block is compatible for, set to "indev" for dev tests.
	 * @param name     The block's name.
	 * @param supplier The supplied {@link Block}.
	 * @param burnTime How many ticks this fuel block should burn for.
	 * @param group    The {@link CreativeModeTab} for the {@link BlockItem}.
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createCompatFuelBlock(String modId, String name, Supplier<? extends B> supplier, int burnTime, @Nullable CreativeModeTab group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new FuelBlockItem(block.get(), burnTime, new Item.Properties().tab(areModsLoaded(modId) ? group : null)));
		return block;
	}

	/**
	 * Creates and registers a compat {@link Block} with a {@link FuelBlockItem}.
	 *
	 * @param name     The block's name.
	 * @param supplier The supplied {@link Block}.
	 * @param burnTime How many ticks this fuel block should burn for.
	 * @param group    The {@link CreativeModeTab} for the {@link BlockItem}.
	 * @param modIds   The mod ids of the mods this block is compatible for.
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createCompatFuelBlock(String name, Supplier<? extends B> supplier, int burnTime, @Nullable CreativeModeTab group, String... modIds) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new FuelBlockItem(block.get(), burnTime, new Item.Properties().tab(areModsLoaded(modIds) ? group : null)));
		return block;
	}

	/**
	 * Creates and registers a {@link BlueprintChestBlock} and a {@link BlueprintTrappedChestBlock} with their {@link BEWLRFuelBlockItem}s.
	 *
	 * @param name        The name for the chest blocks.
	 * @param compatModId The mod id of the mod these chests are compatible for.
	 * @param color       The {@link MaterialColor} for the chest blocks.
	 * @return A {@link Pair} containing {@link RegistryObject}s of the {@link BlueprintChestBlock} and the {@link BlueprintTrappedChestBlock}.
	 */
	public Pair<RegistryObject<BlueprintChestBlock>, RegistryObject<BlueprintTrappedChestBlock>> createCompatChestBlocks(String compatModId, String name, MaterialColor color) {
		boolean isModLoaded = areModsLoaded(compatModId);
		CreativeModeTab chestGroup = isModLoaded ? CreativeModeTab.TAB_DECORATIONS : null;
		CreativeModeTab trappedChestGroup = isModLoaded ? CreativeModeTab.TAB_REDSTONE : null;
		String modId = this.parent.getModId();
		String chestName = name + "_chest";
		String trappedChestName = name + "_trapped_chest";
		RegistryObject<BlueprintChestBlock> chest = this.deferredRegister.register(chestName, () -> new BlueprintChestBlock(modId + ":" + name, Block.Properties.of(Material.WOOD, color).strength(2.5F).sound(SoundType.WOOD)));
		RegistryObject<BlueprintTrappedChestBlock> trappedChest = this.deferredRegister.register(trappedChestName, () -> new BlueprintTrappedChestBlock(modId + ":" + name + "_trapped", Block.Properties.of(Material.WOOD, color).strength(2.5F).sound(SoundType.WOOD)));
		this.itemRegister.register(chestName, () -> new BEWLRFuelBlockItem(chest.get(), new Item.Properties().tab(chestGroup), () -> () -> chestBEWLR(false), 300));
		this.itemRegister.register(trappedChestName, () -> new BEWLRFuelBlockItem(trappedChest.get(), new Item.Properties().tab(trappedChestGroup), () -> () -> chestBEWLR(true), 300));
		ChestManager.putChestInfo(modId, name, false);
		ChestManager.putChestInfo(modId, name, true);
		return Pair.of(chest, trappedChest);
	}

	/**
	 * Creates and registers a {@link BlueprintChestBlock} and a {@link BlueprintTrappedChestBlock} with their {@link BEWLRFuelBlockItem}s.
	 *
	 * @param name   The name for the chest blocks.
	 * @param color  The {@link MaterialColor} for the chest blocks.
	 * @param modIds The mod ids of the mods this block is compatible for.
	 * @return A {@link Pair} containing {@link RegistryObject}s of the {@link BlueprintChestBlock} and the {@link BlueprintTrappedChestBlock}.
	 */
	public Pair<RegistryObject<BlueprintChestBlock>, RegistryObject<BlueprintTrappedChestBlock>> createCompatChestBlocks(String name, MaterialColor color, String... modIds) {
		boolean isInGroup = areModsLoaded(modIds);
		CreativeModeTab chestGroup = isInGroup ? CreativeModeTab.TAB_DECORATIONS : null;
		CreativeModeTab trappedChestGroup = isInGroup ? CreativeModeTab.TAB_REDSTONE : null;
		String modId = this.parent.getModId();
		String chestName = name + "_chest";
		String trappedChestName = name + "_trapped_chest";
		RegistryObject<BlueprintChestBlock> chest = this.deferredRegister.register(chestName, () -> new BlueprintChestBlock(modId + ":" + name, Block.Properties.of(Material.WOOD, color).strength(2.5F).sound(SoundType.WOOD)));
		RegistryObject<BlueprintTrappedChestBlock> trappedChest = this.deferredRegister.register(trappedChestName, () -> new BlueprintTrappedChestBlock(modId + ":" + name + "_trapped", Block.Properties.of(Material.WOOD, color).strength(2.5F).sound(SoundType.WOOD)));
		this.itemRegister.register(chestName, () -> new BEWLRFuelBlockItem(chest.get(), new Item.Properties().tab(chestGroup), () -> () -> chestBEWLR(false), 300));
		this.itemRegister.register(trappedChestName, () -> new BEWLRFuelBlockItem(trappedChest.get(), new Item.Properties().tab(trappedChestGroup), () -> () -> chestBEWLR(true), 300));
		ChestManager.putChestInfo(modId, name, false);
		ChestManager.putChestInfo(modId, name, true);
		return Pair.of(chest, trappedChest);
	}
}
