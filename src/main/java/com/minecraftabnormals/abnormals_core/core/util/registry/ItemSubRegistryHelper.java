package com.minecraftabnormals.abnormals_core.core.util.registry;

import com.google.common.collect.Sets;
import com.minecraftabnormals.abnormals_core.common.items.AbnormalsBoatItem;
import com.minecraftabnormals.abnormals_core.common.items.AbnormalsSpawnEggItem;
import com.minecraftabnormals.abnormals_core.common.items.FuelItem;
import com.minecraftabnormals.abnormals_core.core.registry.BoatRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A basic {@link AbstractSubRegistryHelper} for items. This contains some useful registering methods for items.
 *
 * @author SmellyModder (Luke Tonon)
 * @see AbstractSubRegistryHelper
 */
public class ItemSubRegistryHelper extends AbstractSubRegistryHelper<Item> {
	private static final Field EGGS_FIELD = ObfuscationReflectionHelper.findField(SpawnEggItem.class, "field_195987_b");
	protected final Set<AbnormalsSpawnEggItem> spawnEggs = Sets.newHashSet();

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
		return this.deferredRegister.register(name, () -> new Item(properties.group(ModList.get().isLoaded(modId) || modId == "indev" ? group : null)));
	}

	/**
	 * Creates and registers a compat {@link Item}
	 *
	 * @param name       - The name for the item
	 * @param properties - The item's properties
	 * @param group      - The {@link ItemGroup} for the {@link Item}
	 * @param modIds     - The mod ids of the mods this block is compatible for
	 * @return A {@link RegistryObject} containing the {@link Item}
	 */
	public RegistryObject<Item> createCompatItem(String name, Item.Properties properties, ItemGroup group, String... modIds) {
		return this.deferredRegister.register(name, () -> new Item(properties.group(areModsLoaded(modIds) ? group : null)));
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
		AbnormalsSpawnEggItem eggItem = new AbnormalsSpawnEggItem(supplier, primaryColor, secondaryColor, new Item.Properties().group(ItemGroup.MISC));
		RegistryObject<AbnormalsSpawnEggItem> spawnEgg = this.deferredRegister.register(entityName + "_spawn_egg", () -> eggItem);
		this.spawnEggs.add(eggItem);
		return spawnEgg;
	}

	/**
	 * Creates and registers a {@link AbnormalsBoatItem} and boat type.
	 *
	 * @param wood - The name of the wood, e.g. "oak"
	 * @param block - The {@link Block} for the boat to drop
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

	@Override
	public void register(IEventBus eventBus) {
		super.register(eventBus);
		eventBus.addGenericListener(EntityType.class, EventPriority.LOWEST, this::handleSpawnEggMap);
	}

	@SuppressWarnings("unchecked")
	private void handleSpawnEggMap(RegistryEvent.Register<EntityType<?>> event) {
		if (!this.spawnEggs.isEmpty()) {
			try {
				Map<EntityType<?>, SpawnEggItem> map = (Map<EntityType<?>, SpawnEggItem>) EGGS_FIELD.get(null);
				this.spawnEggs.forEach(egg -> map.put(egg.getType(null), egg));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}
