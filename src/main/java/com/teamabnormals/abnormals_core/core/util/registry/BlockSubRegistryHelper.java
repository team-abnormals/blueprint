package com.teamabnormals.abnormals_core.core.util.registry;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.abnormals_core.client.renderer.ChestItemRenderer;
import com.teamabnormals.abnormals_core.common.blocks.chest.AbnormalsChestBlock;
import com.teamabnormals.abnormals_core.common.blocks.chest.AbnormalsTrappedChestBlock;
import com.teamabnormals.abnormals_core.common.blocks.sign.AbnormalsStandingSignBlock;
import com.teamabnormals.abnormals_core.common.blocks.sign.AbnormalsWallSignBlock;
import com.teamabnormals.abnormals_core.common.items.AbnormalsSignItem;
import com.teamabnormals.abnormals_core.common.items.FuelBlockItem;
import com.teamabnormals.abnormals_core.common.items.InjectedBlockItem;
import com.teamabnormals.abnormals_core.common.tileentity.AbnormalsChestTileEntity;
import com.teamabnormals.abnormals_core.common.tileentity.AbnormalsTrappedChestTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

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
	private final DeferredRegister<Item> itemRegister;

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

	/**
	 * Creates and registers a {@link Block} with a {@link BlockItem}
	 *
	 * @param name     - The block's name
	 * @param supplier - The supplied {@link Block}
	 * @param group    - The {@link ItemGroup} for the {@link BlockItem}
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createBlock(String name, Supplier<? extends B> supplier, @Nullable ItemGroup group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(group)));
		return block;
	}

	/**
	 * Creates and registers a {@link Block} with a specified {@link BlockItem}
	 *
	 * @param name     - The block's name
	 * @param supplier - The supplied {@link Block}
	 * @param item     - The {@link BlockItem} for this {@link Block}.
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createBlockWithItem(String name, Supplier<? extends B> supplier, Supplier<BlockItem> item) {
		this.itemRegister.register(name, item);
		return this.deferredRegister.register(name, supplier);
	}

	/**
	 * Creates and registers a {@link Block} with no {@link BlockItem}
	 *
	 * @param name     - The block's name
	 * @param supplier - The supplied Block
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createBlockNoItem(String name, Supplier<? extends B> supplier) {
		return this.deferredRegister.register(name, supplier);
	}

	/**
	 * Creates and registers a {@link Block} with its {@link BlockItem} that can be used as fuel.
	 *
	 * @param name     - The block's name
	 * @param supplier - The supplied {@link Block}
	 * @param burnTime - How long the item will burn (measured in ticks)
	 * @param group    - The {@link ItemGroup} for the {@link BlockItem}
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createFuelBlock(String name, Supplier<? extends B> supplier, int burnTime, @Nullable ItemGroup group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new FuelBlockItem(block.get(), burnTime, new Item.Properties().group(group)));
		return block;
	}

	/**
	 * Creates and registers a {@link Block} with an {@link InjectedBlockItem}.
	 *
	 * @param name     - The block's name
	 * @param supplier - The supplied {@link Block}
	 * @param group    - The {@link ItemGroup} for the {@link InjectedBlockItem}
	 * @return A {@link RegistryObject} containing the created {@link Block}.
	 */
	public <B extends Block> RegistryObject<B> createInjectedBlock(String name, Item followItem, Supplier<? extends B> supplier, @Nullable ItemGroup group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new InjectedBlockItem(followItem, block.get(), new Item.Properties().group(group)));
		return block;
	}

	/**
	 * Creates and registers a {@link Block} with a {@link BlockItem} with a {@link ItemStackTileEntityRenderer}
	 *
	 * @param name     - The block's name
	 * @param supplier - The supplied {@link Block}
	 * @param ister    - A supplier containing a callable {@link ItemStackTileEntityRenderer} for the {@link BlockItem}
	 * @param group    - The {@link ItemGroup} for the {@link BlockItem}
	 * @return A {@link RegistryObject} containing the created {@link Block}
	 */
	public <B extends Block> RegistryObject<B> createBlockWithISTER(String name, Supplier<? extends B> supplier, Supplier<Callable<ItemStackTileEntityRenderer>> ister, @Nullable ItemGroup group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(group).setISTER(ister)));
		return block;
	}

	/**
	 * Creates and registers a {@link Block} with a {@link TallBlockItem}
	 *
	 * @param name     - The block's name
	 * @param supplier - The supplied {@link Block}
	 * @param group    - The {@link ItemGroup} for the {@link TallBlockItem}
	 * @return A {@link RegistryObject} containing the created {@link Block}
	 * @see TallBlockItem
	 */
	public <B extends Block> RegistryObject<B> createTallBlock(String name, Supplier<? extends B> supplier, ItemGroup group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new TallBlockItem(block.get(), new Item.Properties().group(group)));
		return block;
	}

	/**
	 * Creates and registers {@link Block} with a {@link WallOrFloorItem}
	 *
	 * @param name     - The block's name
	 * @param supplier - The supplied floor {@link Block}
	 * @param supplier - The supplied wall {@link Block}
	 * @param group    - The {@link ItemGroup} for the {@link WallOrFloorItem}
	 * @return A {@link RegistryObject} containing the created {@link Block}
	 * @see WallOrFloorItem
	 */
	public <B extends Block> RegistryObject<B> createWallOrFloorBlock(String name, Supplier<? extends B> supplier, Supplier<? extends B> wallSupplier, @Nullable ItemGroup group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new WallOrFloorItem(block.get(), wallSupplier.get(), new Item.Properties().group(group)));
		return block;
	}

	/**
	 * Creates and registers {@link Block} with a {@link BlockItem} that has {@link Rarity}
	 *
	 * @param name   - The block's name
	 * @param rarity - The {@link Rarity} of the {@link BlockItem}
	 * @param group  - The {@link ItemGroup} for the {@link BlockItem}
	 * @return A {@link RegistryObject} containing the created {@link Block}
	 */
	public <B extends Block> RegistryObject<B> createRareBlock(String name, Supplier<? extends B> supplier, Rarity rarity, @Nullable ItemGroup group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new BlockItem(block.get(), new Item.Properties().rarity(rarity).group(group)));
		return block;
	}

	/**
	 * Creates and registers {@link AbnormalsChestBlock} with a {@link FuelBlockItem}
	 *
	 * @param name       - The name for this {@link AbnormalsChestBlock}
	 * @param properties - The properties for this {@link AbnormalsChestBlock}
	 * @param group      - The ItemGroup for the BlockItem
	 * @return A {@link RegistryObject} containing the created {@link AbnormalsChestBlock}
	 */
	public RegistryObject<AbnormalsChestBlock> createChestBlock(String name, Block.Properties properties, @Nullable ItemGroup group) {
		RegistryObject<AbnormalsChestBlock> block = this.deferredRegister.register(name + "_chest", () -> new AbnormalsChestBlock(this.parent.getModId(), name, properties));
		this.itemRegister.register(name + "_chest", () -> new FuelBlockItem(block.get(), 300, new Item.Properties().group(group).setISTER(() -> chestISTER(false))));
		return block;
	}

	/**
	 * Creates and registers {@link AbnormalsTrappedChestBlock} with a {@link FuelBlockItem}.
	 *
	 * @param name       - The name for this {@link AbnormalsTrappedChestBlock}
	 * @param properties - The properties for this {@link AbnormalsTrappedChestBlock}
	 * @param group      - The ItemGroup for the BlockItem
	 * @return A {@link RegistryObject} containing the created {@link AbnormalsTrappedChestBlock}
	 */
	public RegistryObject<AbnormalsTrappedChestBlock> createTrappedChestBlock(String name, Block.Properties properties, @Nullable ItemGroup group) {
		RegistryObject<AbnormalsTrappedChestBlock> block = this.deferredRegister.register(name + "_trapped_chest", () -> new AbnormalsTrappedChestBlock(this.parent.getModId(), name, properties));
		this.itemRegister.register(name + "_trapped_chest", () -> new FuelBlockItem(block.get(), 300, new Item.Properties().group(group).setISTER(() -> chestISTER(true))));
		return block;
	}

	/**
	 * Creates and registers a {@link AbnormalsStandingSignBlock} and a {@link AbnormalsWallSignBlock} with an {@link AbnormalsSignItem}.
	 *
	 * @param name  - The name for the sign blocks
	 * @param color - The {@link MaterialColor} for the sign blocks.
	 * @return A {@link Pair} containing {@link RegistryObject}s of the {@link AbnormalsStandingSignBlock} and the {@link AbnormalsWallSignBlock}
	 */
	public Pair<RegistryObject<AbnormalsStandingSignBlock>, RegistryObject<AbnormalsWallSignBlock>> createSignBlock(String name, MaterialColor color) {
		ResourceLocation texture = new ResourceLocation(this.parent.getModId(), "textures/entity/signs/" + name + ".png");
		RegistryObject<AbnormalsStandingSignBlock> standing = this.deferredRegister.register(name + "_sign", () -> new AbnormalsStandingSignBlock(Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD), texture));
		RegistryObject<AbnormalsWallSignBlock> wall = this.deferredRegister.register(name + "_wall_sign", () -> new AbnormalsWallSignBlock(Block.Properties.create(Material.WOOD, color).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD).lootFrom(standing.get()), texture));
		this.itemRegister.register(name + "_sign", () -> new AbnormalsSignItem(standing.get(), wall.get(), new Item.Properties().maxStackSize(16).group(ItemGroup.DECORATIONS)));
		return Pair.of(standing, wall);
	}

	/**
	 * Creates and registers a compat {@link Block}
	 *
	 * @param modId    - The mod id of the mod this block is compatible for, set to "indev" for dev tests
	 * @param name     - The block's name
	 * @param supplier - The supplied {@link Block}
	 * @param group    - The {@link ItemGroup} for the {@link BlockItem}
	 * @return A {@link RegistryObject} containing the created {@link Block}
	 */
	public <B extends Block> RegistryObject<B> createCompatBlock(String modId, String name, Supplier<? extends B> supplier, @Nullable ItemGroup group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(ModList.get().isLoaded(modId) || modId == "indev" ? group : null)));
		return block;
	}

	/**
	 * Creates and registers a compat {@link Block} with a {@link FuelBlockItem}.
	 *
	 * @param modId    - The modId of the mod this block is compatible for, set to "indev" for dev tests
	 * @param name     - The block's name
	 * @param supplier - The supplied {@link Block}
	 * @param burnTime - How many ticks this fuel block should burn for.
	 * @param group    - The {@link ItemGroup} for the {@link BlockItem}
	 * @return A {@link RegistryObject} containing the created {@link Block}
	 */
	public <B extends Block> RegistryObject<B> createCompatFuelBlock(String modId, String name, Supplier<? extends B> supplier, int burnTime, @Nullable ItemGroup group) {
		RegistryObject<B> block = this.deferredRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new FuelBlockItem(block.get(), burnTime, new Item.Properties().group(ModList.get().isLoaded(modId) || modId == "indev" ? group : null)));
		return block;
	}

	/**
	 * Creates and registers a {@link AbnormalsChestBlock} and a {@link AbnormalsTrappedChestBlock} with their {@link FuelBlockItem}s.
	 *
	 * @param name        - The name for the chest blocks
	 * @param compatModId - The mod id of the mod these chests are compatible for.
	 * @param color       - The {@link MaterialColor} for the chest blocks.
	 * @return A {@link Pair} containing {@link RegistryObject}s of the {@link AbnormalsChestBlock} and the {@link AbnormalsTrappedChestBlock}
	 */
	public Pair<RegistryObject<AbnormalsChestBlock>, RegistryObject<AbnormalsTrappedChestBlock>> createCompatChestBlocks(String name, String compatModId, MaterialColor color) {
		boolean isModLoaded = ModList.get().isLoaded(compatModId) || compatModId == "indev";
		ItemGroup chestGroup = isModLoaded ? ItemGroup.DECORATIONS : null;
		ItemGroup trappedChestGroup = isModLoaded ? ItemGroup.REDSTONE : null;
		String modId = this.parent.getModId();
		String chestName = name + "_chest";
		String trappedChestName = name + "_trapped_chest";
		RegistryObject<AbnormalsChestBlock> chest = this.deferredRegister.register(chestName, () -> new AbnormalsChestBlock(modId, name, Block.Properties.create(Material.WOOD, color).hardnessAndResistance(2.5F).sound(SoundType.WOOD)));
		RegistryObject<AbnormalsTrappedChestBlock> trappedChest = this.deferredRegister.register(trappedChestName, () -> new AbnormalsTrappedChestBlock(modId, name, Block.Properties.create(Material.WOOD, color).hardnessAndResistance(2.5F).sound(SoundType.WOOD)));
		this.itemRegister.register(chestName, () -> new BlockItem(chest.get(), new Item.Properties().group(chestGroup).setISTER(() -> chestISTER(false))));
		this.itemRegister.register(trappedChestName, () -> new BlockItem(trappedChest.get(), new Item.Properties().group(trappedChestGroup).setISTER(() -> chestISTER(true))));
		return Pair.of(chest, trappedChest);
	}

	@OnlyIn(Dist.CLIENT)
	private static Callable<ItemStackTileEntityRenderer> chestISTER(boolean trapped) {
		return () -> new ChestItemRenderer<TileEntity>(trapped ? AbnormalsTrappedChestTileEntity::new : AbnormalsChestTileEntity::new);
	}
}
