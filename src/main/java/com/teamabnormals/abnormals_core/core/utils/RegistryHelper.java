package com.teamabnormals.abnormals_core.core.utils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.abnormals_core.common.blocks.sign.AbnormalsStandingSignBlock;
import com.teamabnormals.abnormals_core.common.blocks.sign.AbnormalsWallSignBlock;
import com.teamabnormals.abnormals_core.common.dispenser.SpawnEggDispenseBehavior;
import com.teamabnormals.abnormals_core.common.items.AbnormalsBoatItem;
import com.teamabnormals.abnormals_core.common.items.AbnormalsSignItem;
import com.teamabnormals.abnormals_core.common.items.AbnormalsSpawnEggItem;
import com.teamabnormals.abnormals_core.common.items.FuelItem;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import com.teamabnormals.abnormals_core.core.examples.ExampleBlockRegistry;
import com.teamabnormals.abnormals_core.core.examples.ExampleEntityRegistry;
import com.teamabnormals.abnormals_core.core.examples.ExampleItemRegistry;
import com.teamabnormals.abnormals_core.core.examples.ExampleSoundRegistry;
import com.teamabnormals.abnormals_core.core.registry.BoatRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.item.TallBlockItem;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Simple Registry Helper that can be configured to a specific mod
 * 
 * Implementation for Registries:
 * When utilizing your mod's RegistryHelper make sure the classes using it are added to your mod's event bus
 * 
 * For examples @see {@link ExampleItemRegistry} and {@link ExampleBlockRegistry}
 * For adding new custom helper methods extend this class
 * @author SmellyModder(Luke Tonon)
 */
public class RegistryHelper {
	private final String modId;
	private final DeferredRegister<Item> itemRegister;
	private final DeferredRegister<Block> blockRegister;
	private final DeferredRegister<SoundEvent> soundRegister;
	private final DeferredRegister<TileEntityType<?>> tileEntityRegister;
	private final DeferredRegister<EntityType<?>> entityRegister;
	public final List<RegistryObject<Item>> spawnEggs = Lists.newArrayList();
	
	public RegistryHelper(String modId) {
		this.modId = modId;
		this.itemRegister = new DeferredRegister<>(ForgeRegistries.ITEMS, modId);
		this.blockRegister = new DeferredRegister<>(ForgeRegistries.BLOCKS, modId);
		this.soundRegister = new DeferredRegister<>(ForgeRegistries.SOUND_EVENTS, modId);
		this.tileEntityRegister = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, modId);
		this.entityRegister = new DeferredRegister<>(ForgeRegistries.ENTITIES, modId);
	}
	
	public DeferredRegister<Item> getDeferredItemRegister() {
		return this.itemRegister;
	}
	
	public DeferredRegister<Block> getDeferredBlockRegister() {
		return this.blockRegister;
	}
	
	public DeferredRegister<SoundEvent> getDeferredSoundRegister() {
		return this.soundRegister;
	}
	
	public DeferredRegister<TileEntityType<?>> getDeferredTileEntityRegister() {
		return this.tileEntityRegister;
	}
	
	public DeferredRegister<EntityType<?>> getDeferredEntityRegister() {
		return this.entityRegister;
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
	 * Processes all the spawn egg dispenser behaviors1, should be run in common setup
	 * @see {@link AbnormalsCore#commonSetup} for example
	 */
	public void processSpawnEggDispenseBehaviors() {
		for(RegistryObject<Item> items : this.spawnEggs) {
			Item item = items.get();
			if(item instanceof AbnormalsSpawnEggItem) {
				DispenserBlock.registerDispenseBehavior(item, new SpawnEggDispenseBehavior());
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
	 * @param modId - The modId of the mod this item is compatible for, set to "indev" for dev tests
	 * @param name - Item name
	 * @param properties - The item's properties
	 * @param group - The ItemGroup for the item
	 * @return - The Item RegistryObject
	 */
	public RegistryObject<Item> createCompatItem(String modId, String name, Item.Properties properties, ItemGroup group) {
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
	 * Creates a Boat Item
	 * @param - The name of the wood, @example "poise"
	 * @param - The planks block for the boat to drop
	 */
	public RegistryObject<Item> createBoatItem(String wood, RegistryObject<Block> block) {
		String type = this.getModId() + ":" + wood;
		RegistryObject<Item> boat = this.itemRegister.register(wood + "_boat", () -> new AbnormalsBoatItem(type, createSimpleItemProperty(1, ItemGroup.TRANSPORTATION)));
		BoatRegistry.registerBoat(type, boat, block);
		return boat;
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
	 * @param burnTime - How long the item will burn(measured in ticks)
	 * @param itemGroup - The Item's ItemGroup
	 * @return - The customized simple Item
	 */
	public static FuelItem createFuelItem(int burnTime, ItemGroup itemGroup) {
		return new FuelItem(burnTime, new Item.Properties().group(itemGroup));
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
	 * Creates a Block with a WallOrFloorItem
	 * @see WallOrFloorItem
	 * @param name - The block's name
	 * @param supplier - The supplied Floor Block
	 * @param supplier - The supplied Wall Block
	 * @param group - The TallBlockItem's ItemGroup
	 * @return - The Block with a WallOrFloorItem
	 */
	public <B extends Block> RegistryObject<B> createWallOrFloorBlock(String name, Supplier<? extends B> supplier, Supplier<? extends B> wallSupplier, @Nullable ItemGroup group) {
		RegistryObject<B> block = this.getDeferredBlockRegister().register(name, supplier);
		this.getDeferredItemRegister().register(name, () -> new WallOrFloorItem(block.get(), wallSupplier.get(), new Item.Properties().group(group)));
		return block;
	}
	
	/**
	 * Creates a block with its BlockItem that has a Rarity
	 * @param name - The block's name
	 * @param supplier - The supplied Block
	 * @param rarity - The item's rarity
	 * @param group - The ItemGroup for the BlockItem
	 * @return - The customized Block
	 */
	public <B extends Block> RegistryObject<B> createRareBlock(String name, Supplier<? extends B> supplier, Rarity rarity, @Nullable ItemGroup group) {
		RegistryObject<B> block = this.getDeferredBlockRegister().register(name, supplier);
		this.getDeferredItemRegister().register(name, () -> new BlockItem(block.get(), new Item.Properties().rarity(rarity).group(group)));
		return block;
	}
	
	public Pair<RegistryObject<AbnormalsStandingSignBlock>, RegistryObject<AbnormalsWallSignBlock>> createSignBlock(String name, MaterialColor color) {
		ResourceLocation texture = new ResourceLocation(this.getModId(), "textures/entity/signs/" + name + ".png");
		RegistryObject<AbnormalsStandingSignBlock> standing = this.blockRegister.register(name + "_sign", () -> new AbnormalsStandingSignBlock(Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD), texture));
		RegistryObject<AbnormalsWallSignBlock> wall = this.blockRegister.register(name + "_wall_sign", () -> new AbnormalsWallSignBlock(Block.Properties.create(Material.WOOD, color).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(SoundType.WOOD).lootFrom(standing.get()), texture));
		this.createItem(name + "_sign", () -> new AbnormalsSignItem(standing.get(), wall.get(), new Item.Properties().maxStackSize(16).group(ItemGroup.DECORATIONS)));
		return Pair.of(standing, wall);
	}
	
	/**
	 * Creates a Compat Block
	 * @param modId - The modId of the mod this block is compatible for, set to "indev" for dev tests
	 * @param name - The block's name
	 * @param supplier - The supplied Block
	 * @param group - The BlockItem's ItemGroup
	 * @return - The Compat Block
	 */
	public <B extends Block> RegistryObject<B> createCompatBlock(String modId, String name, Supplier<? extends B> supplier, @Nullable ItemGroup group) {
		ItemGroup determinedGroup = ModList.get().isLoaded(modId) || modId == "indev" ? group : null;
		RegistryObject<B> block = this.blockRegister.register(name, supplier);
		this.itemRegister.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(determinedGroup)));
		return block;
	}
	
	/**
	 * Creates a SoundEvent
	 * @see ({@link ExampleSoundRegistry}
	 * @param name - The sound's name
	 * @return - The named SoundEvent
	 */
	public RegistryObject<SoundEvent> createSoundEvent(String name) {
		return this.soundRegister.register(name, () -> new SoundEvent(this.prefix(name)));
	}
	
	/**
	 * Creates a TileEntityType
	 * @param name - Tile Entity's name
	 * @param tileEntity - The Tile Entity
	 * @param validBlocks - The valid blocks for this Tile Entity
	 * @return - The customized TileEntityType
	 */
	public <T extends TileEntity> RegistryObject<TileEntityType<T>> createTileEntity(String name, Supplier<? extends T> tileEntity, Supplier<Block[]> validBlocks) {
		return this.tileEntityRegister.register(name, () -> new TileEntityType<>(tileEntity, Sets.newHashSet(validBlocks.get()), null));
	}
	
	/**
	 * Creates a living entity RegistryObject
	 * For example @see {@link ExampleEntityRegistry}
	 * @param name - The entity's name
	 * @param factory - The entity's factory
	 * @param entityClassification - The entity's classification
	 * @param width - The width of the entity's bounding box
	 * @param height - The height of the entity's bounding box
	 * @return - The customized living entity RegistryObject
	 */
	public <E extends LivingEntity> RegistryObject<EntityType<E>> createLivingEntity(String name, EntityType.IFactory<E> factory, EntityClassification entityClassification, float width, float height) {
		return this.entityRegister.register(name, () -> createLivingEntity(factory, entityClassification, name, width, height));
	}
	
	/**
	 * Creates an entity RegistryObject
	 * No examples for this one, but {@link ExampleEntityRegistry} can help
	 * @param name - The entity's name
	 * @param factory - The entity's factory
	 * @param clientFactory - The entity's client factory
	 * @param entityClassification - The entity's classification
	 * @param width - The width of the entity's bounding box
	 * @param height - The height of the entity's bounding box
	 * @return - The customized entity RegistryObject
	 */
	public <E extends Entity> RegistryObject<EntityType<E>> createEntity(String name, EntityType.IFactory<E> factory, BiFunction<FMLPlayMessages.SpawnEntity, World, E> clientFactory, EntityClassification entityClassification, float width, float height) {
		return this.entityRegister.register(name, () -> createEntity(factory, clientFactory, entityClassification, name, width, height));
	}
	
	/**
	 * Creates a living entity
	 * For example @see {@link ExampleEntityRegistry}
	 * @param name - The entity's name
	 * @param factory - The entity's factory
	 * @param entityClassification - The entity's classification
	 * @param width - The width of the entity's bounding box
	 * @param height - The height of the entity's bounding box
	 * @return - The customized living entity
	 */
	public <E extends LivingEntity> EntityType<E> createLivingEntity(EntityType.IFactory<E> factory, EntityClassification entityClassification, String name, float width, float height) {
		ResourceLocation location = this.prefix(name);
		EntityType<E> entity = EntityType.Builder.create(factory, entityClassification)
			.size(width, height)
			.setTrackingRange(64)
			.setShouldReceiveVelocityUpdates(true)
			.setUpdateInterval(3)
			.build(location.toString()
		);
		return entity;
	}
	
	/**
	 * Creates an entity
	 * No examples for this one, but {@link ExampleEntityRegistry} can help
	 * @param name - The entity's name
	 * @param factory - The entity's factory
	 * @param clientFactory - The entity's client factory
	 * @param entityClassification - The entity's classification
	 * @param width - The width of the entity's bounding box
	 * @param height - The height of the entity's bounding box
	 * @return - The customized entity
	 */
	public <E extends Entity> EntityType<E> createEntity(EntityType.IFactory<E> factory, BiFunction<FMLPlayMessages.SpawnEntity, World, E> clientFactory, EntityClassification entityClassification, String name, float width, float height) {
		ResourceLocation location = this.prefix(name);
		EntityType<E> entity = EntityType.Builder.create(factory, entityClassification)
			.size(width, height)
			.setTrackingRange(64)
			.setShouldReceiveVelocityUpdates(true)
			.setUpdateInterval(3)
			.setCustomClientFactory(clientFactory)
			.build(location.toString()
		);
		return entity;
	}
}