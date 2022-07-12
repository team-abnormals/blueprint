package com.teamabnormals.blueprint.core.util.registry;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A class that works as a parent holder to children {@link ISubRegistryHelper}s.
 * <p>
 * A map is stored inside this that maps a {@link IForgeRegistry} to a {@link ISubRegistryHelper} to get the child {@link ISubRegistryHelper} for a specific {@link IForgeRegistry}.
 * A value put for a key in this map is a {@link ISubRegistryHelper} with the same parameterized type as the key.
 * </p>
 * Use the {@link #putSubHelper(IForgeRegistry, ISubRegistryHelper)} method to put a {@link ISubRegistryHelper} for a {@link IForgeRegistry}.
 *
 * @author SmellyModder (Luke Tonon)
 */
public class RegistryHelper {
	private final Map<IForgeRegistry<?>, ISubRegistryHelper<?>> subHelpers = Maps.newHashMap();
	protected final String modId;

	public RegistryHelper(String modId) {
		this.modId = modId;
		this.putDefaultSubHelpers();
	}

	/**
	 * Creates a new {@link RegistryHelper} with a specified mod ID and then accepts a consumer onto it.
	 *
	 * @param modId    The mod ID for this helper.
	 * @param consumer A consumer to accept after the helper has been initialized.
	 * @return A new {@link RegistryHelper} with a specified mod ID that has had a consumer accepted onto it.
	 */
	public static RegistryHelper create(String modId, Consumer<RegistryHelper> consumer) {
		RegistryHelper helper = new RegistryHelper(modId);
		consumer.accept(helper);
		return helper;
	}

	/**
	 * @return The mod id belonging to this {@link RegistryHelper}.
	 */
	public String getModId() {
		return this.modId;
	}

	/**
	 * Creates a {@link ResourceLocation} for a string prefixed with the mod id.
	 *
	 * @param name The string to prefix.
	 * @return A {@link ResourceLocation} for a string prefixed with the mod id
	 */
	public ResourceLocation prefix(String name) {
		return new ResourceLocation(this.modId, name);
	}

	/**
	 * Puts a {@link ISubRegistryHelper} for a {@link IForgeRegistry}.
	 *
	 * @param registry  The {@link IForgeRegistry} to map the key to.
	 * @param subHelper The {@link ISubRegistryHelper} to be mapped.
	 * @param <V>       The type of objects to register in the helper.
	 */
	public <V> void putSubHelper(IForgeRegistry<V> registry, ISubRegistryHelper<V> subHelper) {
		this.subHelpers.put(registry, subHelper);
	}

	/**
	 * Puts the default {@link ISubRegistryHelper}s onto the map.
	 */
	protected void putDefaultSubHelpers() {
		this.putSubHelper(ForgeRegistries.ITEMS, new ItemSubRegistryHelper(this));
		this.putSubHelper(ForgeRegistries.BLOCKS, new BlockSubRegistryHelper(this));
		this.putSubHelper(ForgeRegistries.SOUND_EVENTS, new SoundSubRegistryHelper(this));
		this.putSubHelper(ForgeRegistries.BLOCK_ENTITY_TYPES, new BlockEntitySubRegistryHelper(this));
		this.putSubHelper(ForgeRegistries.ENTITY_TYPES, new EntitySubRegistryHelper(this));
		this.putSubHelper(ForgeRegistries.BIOMES, new BiomeSubRegistryHelper(this));
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	public <T, S extends ISubRegistryHelper<T>> S getSubHelper(IForgeRegistry<T> registry) {
		S subHelper = (S) this.subHelpers.get(registry);
		if (subHelper == null) {
			throw new NullPointerException("No Sub Helper is registered for the forge registry: " + registry);
		}
		return subHelper;
	}

	@Nonnull
	public <T extends AbstractSubRegistryHelper<Item>> T getItemSubHelper() {
		return this.getSubHelper(ForgeRegistries.ITEMS);
	}

	@Nonnull
	public <T extends AbstractSubRegistryHelper<Block>> T getBlockSubHelper() {
		return this.getSubHelper(ForgeRegistries.BLOCKS);
	}

	@Nonnull
	public <T extends AbstractSubRegistryHelper<SoundEvent>> T getSoundSubHelper() {
		return this.getSubHelper(ForgeRegistries.SOUND_EVENTS);
	}

	@Nonnull
	public <T extends AbstractSubRegistryHelper<BlockEntityType<?>>> T getBlockEntitySubHelper() {
		return this.getSubHelper(ForgeRegistries.BLOCK_ENTITY_TYPES);
	}

	@Nonnull
	public <T extends AbstractSubRegistryHelper<EntityType<?>>> T getEntitySubHelper() {
		return this.getSubHelper(ForgeRegistries.ENTITY_TYPES);
	}

	@Nonnull
	public <T extends AbstractSubRegistryHelper<Biome>> T getBiomeSubHelper() {
		return this.getSubHelper(ForgeRegistries.BIOMES);
	}

	/**
	 * Registers all the mapped {@link ISubRegistryHelper}s.
	 *
	 * @param eventBus The {@link IEventBus} to register the {@link ISubRegistryHelper}s to.
	 */
	public void register(IEventBus eventBus) {
		this.subHelpers.values().forEach(helper -> {
			helper.register(eventBus);
		});
	}
}