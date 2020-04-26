package com.teamabnormals.abnormals_core.core.utils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.teamabnormals.abnormals_core.common.items.AbnormalsSpawnEggItem;
import com.teamabnormals.abnormals_core.common.items.FuelItem;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import com.teamabnormals.abnormals_core.core.ExampleBlockRegistry;
import com.teamabnormals.abnormals_core.core.ExampleItemRegistry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.TallBlockItem;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Simple Registry Helper that can be configured to a specific mod
 * For adding new custom helper methods extend this class
 * For examples @see {@link ExampleItemRegistry} and {@link ExampleBlockRegistry}
 * @author SmellyModder(Luke Tonon)
 */
public class RegistryHelper {
	private final String modId;
	private final DeferredRegister<Item> itemRegister;
	private final DeferredRegister<Block> blockRegister;
	private final List<RegistryObject<Item>> spawnEggs = Lists.newArrayList();
	
	public RegistryHelper(String modId) {
		this.modId = modId;
		this.itemRegister = new DeferredRegister<>(ForgeRegistries.ITEMS, modId);
		this.blockRegister = new DeferredRegister<>(ForgeRegistries.BLOCKS, modId);
	}
	
	public DeferredRegister<Item> getDeferredItemRegister() {
		return this.itemRegister;
	}
	
	public DeferredRegister<Block> getDeferredBlockRegister() {
		return this.blockRegister;
	}
	
	public String getModId() {
		return this.modId;
	}
	
	public ResourceLocation prefix(String name) {
		return new ResourceLocation(this.modId, name);
	}
	
	/**
	 * Processes all the spawn egg colors, should be registered as an event with lowest priority
	 * @see {@link AbnormalsCore#registerItemColors} for example
	 * @param event - The ColorHandlerEvent.Item event
	 */
	@OnlyIn(Dist.CLIENT)
	public void processSpawnEggColors(ColorHandlerEvent.Item event) {
		for(RegistryObject<Item> items : this.spawnEggs) {
			if(ObfuscationReflectionHelper.getPrivateValue(RegistryObject.class, items, "value") != null) {
				Item item = items.get();
				if(item instanceof AbnormalsSpawnEggItem) {
					event.getItemColors().register((itemColor, itemsIn) -> {
						return ((AbnormalsSpawnEggItem) item).getColor(itemsIn);
					}, item);
				}
			}
		}
	}
	
	/**
	 * Registers an item
	 * @param name - Item name
	 * @param supplier - The Item
	 * @return - The Item RegistryObject
	 */
	public <I extends Item> RegistryObject<I> createItem(String name, Supplier<? extends I> supplier) {
		RegistryObject<I> item = this.itemRegister.register(name, supplier);
		return item;
	}
	
	/**
	 * Creates a Compat Item
	 * @param name - Item name
	 * @param modId - The modId of the mod this item is compatible for, set to "indev" for dev tests
	 * @param properties - The item's properties
	 * @param group - The ItemGroup for the item
	 * @return - The Item RegistryObject
	 */
	public RegistryObject<Item> createCompatItem(String name, String modId, Item.Properties properties, ItemGroup group) {
		ItemGroup determinedGroup = ModList.get().isLoaded(modId) || modId == "indev" ? group : null;
		RegistryObject<Item> item = this.itemRegister.register(name, () -> new Item(properties.group(determinedGroup)));
		return item;
	}
	
	/**
	 * Creates a Spawn Egg Item
	 * @see AbnormalsSpawnEggItem
	 * @param entityName - The entity's name
	 * @param supplier - The supplied entity type
	 * @param primaryColor - The egg's primary color
	 * @param secondaryColor - The egg's secondary color
	 * @return - The customized Spawn Egg
	 */
	public RegistryObject<Item> createSpawnEggItem(String entityName, Supplier<EntityType<?>> supplier, int primaryColor, int secondaryColor) {
		RegistryObject<Item> spawnEgg = this.itemRegister.register(entityName + "_spawn_egg", () -> new AbnormalsSpawnEggItem(supplier, primaryColor, secondaryColor, new Item.Properties().group(ItemGroup.MISC)));
		this.spawnEggs.add(spawnEgg);
		return spawnEgg;
	}
	
	/**
	 * Creates a simple Item.Properties
	 * @param stackSize - The item's max stack size
	 * @param itemGroup - The item's ItemGroup
	 * @return - The customized Item.Properties
	 */
	public static Item.Properties createSimpleItemProperty(int stackSize, ItemGroup itemGroup) {
		return new Item.Properties().group(itemGroup).maxStackSize(stackSize);
	}
	
	/**
	 * Creates a WallOrFloorItem
	 * @see WallOrFloorItem
	 * @param floorBlock - The floor block
	 * @param wallBlock - The wall block
	 * @param itemGroup - The ItemGroup for the item
	 * @return - The customized WallOrFloorItem
	 */
	public static BlockItem createWallOrFloorItem(Block floorBlock, Block wallBlock, ItemGroup itemGroup) {
		return (BlockItem) new WallOrFloorItem(floorBlock, wallBlock, new Item.Properties().group(itemGroup));
	}
	
	/**
	 * Creates a TallBlockItem
	 * @see TallBlockItem
	 * @param blockForInput - The block for the item
	 * @param itemGroup - The ItemGroup for the item
	 * @return - The customized TallBlockItem
	 */
	public static BlockItem createTallBlockItem(Block blockForInput, ItemGroup itemGroup) {
		return (BlockItem) new TallBlockItem(blockForInput, new Item.Properties().group(itemGroup));
	}
	
	/**
	 * Creates a simple Item
	 * @param itemGroup - The Item's ItemGroup
	 * @return - The customized simple Item
	 */
	public static Item createSimpleItem(ItemGroup itemGroup) {
		return new Item(new Item.Properties().group(itemGroup));
	}
	
	/**
	 * Creates a Fuel Item
	 * @param itemGroup - The Item's ItemGroup
	 * @param burnTime - How long the item will burn(measured in ticks)
	 * @return - The customized simple Item
	 */
	public static FuelItem createFuelItem(ItemGroup itemGroup, int burnTime) {
		return new FuelItem(new Item.Properties().group(itemGroup), burnTime);
	}
	
	/**
	 * Creates a BlockItem with a specified Block and ItemGroup
	 * @param blockForInput - The block for the BlockItem
	 * @param itemGroup - The group for the BlockItem, can be null for no tab at all
	 * @return - The BlockItem
	 */
	public static BlockItem createSimpleBlockItem(Block blockForInput, @Nullable ItemGroup itemGroup) {
		return (BlockItem) new BlockItem(blockForInput, new Item.Properties().group(itemGroup)).setRegistryName(blockForInput.getRegistryName());
	}
	
	/**
	 * Creates a BlockItem with a specified Block with no ItemGroup
	 * @deprecated - Use {@link RegistryHelper#createSimpleBlockItem(Block, ItemGroup)} and set ItemGroup null
	 * @param blockForInput - The block for the BlockItem
	 * @return - A BlockItem with no tab
	 */
	@Deprecated
	public static BlockItem createNoTabBlockItem(Block blockForInput) {
		return (BlockItem) new BlockItem(blockForInput, new Item.Properties()).setRegistryName(blockForInput.getRegistryName());
	}
	
	/**
	 * Creates a Block with no BlockItem
	 * @param name - The block's name
	 * @param supplier - The supplied Block
	 * @return - The Block with no BlockItem
	 */
	public <B extends Block> RegistryObject<B> createBlockNoItem(String name, Supplier<? extends B> supplier) {
		RegistryObject<B> block = this.blockRegister.register(name, supplier);
		return block;
	}
	
	/**
	 * Creates a block with its BlockItem
	 * @param name - The block's name
	 * @param supplier - The supplied Block
	 * @param group - The ItemGroup for the BlockItem
	 * @return - The customized Block
	 */
	public <B extends Block> RegistryObject<B> createBlock(String name, Supplier<? extends B> supplier, @Nullable ItemGroup group) {
		RegistryObject<B> block = this.blockRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(group)));
		return block;
	}
	
	/**
	 * Creates a block with a ISTER(ItemStack TileEntity Renderer)
	 * @param name - The block's name
	 * @param supplier - The supplied Block
	 * @param ister - The ISTER for the BlockItem
	 * @param group - The ItemGroup for the BlockItem
	 * @return - The customized Block with its ISTER
	 */
	public <B extends Block> RegistryObject<B> createBlockWithISTER(String name, Supplier<? extends B> supplier, final Supplier<Callable<ItemStackTileEntityRenderer>> ister, @Nullable ItemGroup group) {
		RegistryObject<B> block = this.blockRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(group).setISTER(ister)));
		return block;
	}
	
	/**
	 * Creates a Block with a TallBlockItem
	 * @see TallBlockItem
	 * @param name - The block's name
	 * @param supplier - The supplied Block
	 * @param group - The TallBlockItem's ItemGroup
	 * @return - The Block with a TallBlockItem
	 */
	public <B extends Block> RegistryObject<B> createTallBlock(String name, Supplier<? extends B> supplier, ItemGroup group) {
		RegistryObject<B> block = this.blockRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new TallBlockItem(block.get(), new Item.Properties().group(group)));
		return block;
	}
	
	/**
	 * Creates a Compat Block
	 * @param name - The block's name
	 * @param modId - The modId of the mod this block is compatible for, set to "indev" for dev tests
	 * @param supplier - The supplied Block
	 * @param group - The BlockItem's ItemGroup
	 * @return - The Compat Block
	 */
	public <B extends Block> RegistryObject<B> createCompatBlock(String name, String modId, Supplier<? extends B> supplier, @Nullable ItemGroup group) {
		ItemGroup determinedGroup = ModList.get().isLoaded(modId) || modId == "indev" ? group : null;
		RegistryObject<B> block = this.blockRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(determinedGroup)));
		return block;
	}
}