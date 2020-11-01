package com.teamabnormals.abnormals_core.core.util.registry;

import com.google.common.collect.Lists;
import com.teamabnormals.abnormals_core.common.items.AbnormalsBoatItem;
import com.teamabnormals.abnormals_core.common.items.AbnormalsSpawnEggItem;
import com.teamabnormals.abnormals_core.common.items.FuelItem;
import com.teamabnormals.abnormals_core.core.registry.BoatRegistry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * A basic {@link AbstractSubRegistryHelper} for items. This contains some useful registering methods for items.
 *
 * @author SmellyModder (Luke Tonon)
 * @see AbstractSubRegistryHelper
 */
public class ItemSubRegistryHelper extends AbstractSubRegistryHelper<Item> {
	public final List<RegistryObject<AbnormalsSpawnEggItem>> spawnEggs = Lists.newArrayList();

	public ItemSubRegistryHelper(RegistryHelper parent, DeferredRegister<Item> deferredRegister) {
		super(parent, deferredRegister);
	}

	public ItemSubRegistryHelper(RegistryHelper parent) {
		super(parent, DeferredRegister.create(ForgeRegistries.ITEMS, parent.getModId()));
	}

	/**
	 * Registers an {@link Item}
	 *
	 * @param name     - The name for the item
	 * @param supplier - A {@link Supplier} containing the {@link Item}
	 * @return A {@link RegistryObject} containing the {@link Item}
	 */
	public <I extends Item> RegistryObject<I> createItem(String name, Supplier<? extends I> supplier) {
		return this.deferredRegister.register(name, supplier);
	}

	/**
	 * Creates and registers a compat {@link Item}
	 *
	 * @param modId      - The mod id of the mod this item is compatible for, set to "indev" for dev tests
	 * @param name       - The name for the item
	 * @param properties - The item's properties
	 * @param group      - The {@link ItemGroup} for the {@link Item}
	 * @return A {@link RegistryObject} containing the {@link Item}
	 */
	public RegistryObject<Item> createCompatItem(String modId, String name, Item.Properties properties, ItemGroup group) {
		RegistryObject<Item> item = this.deferredRegister.register(name, () -> new Item(properties.group(ModList.get().isLoaded(modId) || modId == "indev" ? group : null)));
		return item;
	}

	/**
	 * Creates and registers a compat {@link Item}
	 *
	 * @param name       - The name for the item
	 * @param properties - The item's properties
	 * @param group      - The {@link ItemGroup} for the {@link Item}
	 * @param modIdList  - The mod ids of the mods this block is compatible for
	 * @return A {@link RegistryObject} containing the {@link Item}
	 */
	public RegistryObject<Item> createCompatItem(String name, Item.Properties properties, ItemGroup group, String ...modIdList) {
		boolean areModsLoaded = true;
		for (String mod : modIdList)
			areModsLoaded &= ModList.get().isLoaded(mod);
		ItemGroup determinedGroup = areModsLoaded ? group : null;
		RegistryObject<Item> item = this.deferredRegister.register(name, () -> new Item(properties.group(determinedGroup)));
		return item;
	}

	/**
	 * Creates and registers a {@link AbnormalsSpawnEggItem}
	 *
	 * @param entityName     - The name of the entity this spawn egg spawns
	 * @param supplier       - The supplied {@link EntityType}
	 * @param primaryColor   - The egg's primary color
	 * @param secondaryColor - The egg's secondary color
	 * @return A {@link RegistryObject} containing the {@link AbnormalsSpawnEggItem}
	 * @see AbnormalsSpawnEggItem
	 */
	public RegistryObject<AbnormalsSpawnEggItem> createSpawnEggItem(String entityName, Supplier<EntityType<?>> supplier, int primaryColor, int secondaryColor) {
		RegistryObject<AbnormalsSpawnEggItem> spawnEgg = this.deferredRegister.register(entityName + "_spawn_egg", () -> new AbnormalsSpawnEggItem(supplier, primaryColor, secondaryColor, new Item.Properties().group(ItemGroup.MISC)));
		this.spawnEggs.add(spawnEgg);
		return spawnEgg;
	}

	/**
	 * Creates and registers a {@link AbnormalsBoatItem} and boat type.
	 *
	 * @param - The name of the wood, e.g. "oak"
	 * @param - The {@link Block} for the boat to drop
	 */
	public RegistryObject<Item> createBoatItem(String wood, RegistryObject<Block> block) {
		String type = this.parent.getModId() + ":" + wood;
		RegistryObject<Item> boat = this.deferredRegister.register(wood + "_boat", () -> new AbnormalsBoatItem(type, createSimpleItemProperty(1, ItemGroup.TRANSPORTATION)));
		BoatRegistry.registerBoat(type, boat, block);
		return boat;
	}

	/**
	 * Creates and registers a {@link WallOrFloorItem}
	 *
	 * @param floorBlock - The floor {@link Block}
	 * @param wallBlock  - The wall {@link Block}
	 * @param itemGroup  - The {@link ItemGroup} for the {@link WallOrFloorItem}
	 * @return The created {@link WallOrFloorItem}
	 * @see WallOrFloorItem
	 */
	public static BlockItem createWallOrFloorItem(Block floorBlock, Block wallBlock, ItemGroup itemGroup) {
		return new WallOrFloorItem(floorBlock, wallBlock, new Item.Properties().group(itemGroup));
	}

	/**
	 * Creates and registers a {@link TallBlockItem}
	 *
	 * @param blockForInput - The {@link Block} for the item
	 * @param itemGroup     - The {@link ItemGroup} for the {@link TallBlockItem}
	 * @return The created {@link TallBlockItem}
	 * @see TallBlockItem
	 */
	public static BlockItem createTallBlockItem(Block blockForInput, ItemGroup itemGroup) {
		return new TallBlockItem(blockForInput, new Item.Properties().group(itemGroup));
	}

	/**
	 * Creates a {@link FuelItem}
	 *
	 * @param burnTime  - How long the item will burn (measured in ticks)
	 * @param itemGroup - The {@link ItemGroup} for the {@link FuelItem}
	 * @return The created {@link FuelItem}
	 */
	public static FuelItem createFuelItem(int burnTime, ItemGroup itemGroup) {
		return new FuelItem(burnTime, new Item.Properties().group(itemGroup));
	}

	/**
	 * Creates a {@link BlockItem} with a specified {@link Block} and {@link ItemGroup}
	 *
	 * @param blockForInput - The {@link Block} for the {@link BlockItem}
	 * @param itemGroup     - The {@link ItemGroup} for the {@link BlockItem}, null to have it be in no group
	 * @return - The BlockItem
	 */
	public static BlockItem createSimpleBlockItem(Block blockForInput, @Nullable ItemGroup itemGroup) {
		return new BlockItem(blockForInput, new Item.Properties().group(itemGroup));
	}

	/**
	 * Creates a simple {@link Item.Properties} with a stack size and {@link ItemGroup}
	 *
	 * @param stackSize - The item's max stack size
	 * @param itemGroup - The item's ItemGroup
	 * @return The simple {@link Item.Properties}
	 */
	public static Item.Properties createSimpleItemProperty(int stackSize, ItemGroup itemGroup) {
		return new Item.Properties().group(itemGroup).maxStackSize(stackSize);
	}

	/**
	 * Processes all the spawn egg colors, should be registered as an event with lowest priority
	 *
	 * @param event - The {@link ColorHandlerEvent.Item} event
	 */
	@OnlyIn(Dist.CLIENT)
	public void processSpawnEggColors(ColorHandlerEvent.Item event) {
		ItemColors colors = event.getItemColors();
		for (RegistryObject<AbnormalsSpawnEggItem> items : this.spawnEggs) {
			if (items.isPresent()) {
				Item item = items.get();
				colors.register((itemColor, itemsIn) -> ((AbnormalsSpawnEggItem) item).getColor(itemsIn), item);
			}
		}
	}
}
