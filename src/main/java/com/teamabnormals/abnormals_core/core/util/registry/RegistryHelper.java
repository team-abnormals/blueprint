package com.teamabnormals.abnormals_core.core.util.registry;

import java.util.Map;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

/**
 * A class that works as a parent holder to children {@link ISubRegistryHelper}s.
 * <p>
 *     A map is stored inside this that maps a {@link IForgeRegistry} to a {@link ISubRegistryHelper} to get the child {@link ISubRegistryHelper} for a specific {@link IForgeRegistry}.
 *     A value put for a key in this map is a {@link ISubRegistryHelper} with the same parameterized type as the key.
 * </p>
 * Use the {@link #putSubHelper(IForgeRegistry, ISubRegistryHelper)} method to put a {@link ISubRegistryHelper} for a {@link IForgeRegistry}.
 * @author SmellyModder (Luke Tonon)
 */
public class RegistryHelper {
	private final Map<IForgeRegistry<?>, ISubRegistryHelper<?>> subHelpers = Maps.newHashMap();
	protected final String modId;
	
	protected RegistryHelper(String modId) {
		this.modId = modId;
	}

	/**
	 * @return The mod id belonging to this {@link RegistryHelper}.
	 */
	public String getModId() {
		return this.modId;
	}

	/**
	 * Creates a {@link ResourceLocation} for a string prefixed with the mod id.
	 * @param name - The string to prefix.
	 * @return A {@link ResourceLocation} for a string prefixed with the mod id
	 */
	public ResourceLocation prefix(String name) {
		return new ResourceLocation(this.modId, name);
	}

	/**
	 * Puts a {@link ISubRegistryHelper} for a {@link IForgeRegistry}.
	 * @param registry - The {@link IForgeRegistry} to map the key to.
	 * @param subHelper - The {@link ISubRegistryHelper} to be mapped.
	 * @param <K> The type of {@link IForgeRegistry}
	 */
	protected <K extends IForgeRegistryEntry<K>> void putSubHelper(IForgeRegistry<K> registry, ISubRegistryHelper<K> subHelper) {
		this.subHelpers.put(registry, subHelper);
	}

	/**
	 * Puts the default {@link ISubRegistryHelper}s onto the map.
	 */
	protected void putDefaultSubHelpers() {
		this.putSubHelper(ForgeRegistries.ITEMS, new ItemSubRegistryHelper(this));
		this.putSubHelper(ForgeRegistries.BLOCKS, new BlockSubRegistryHelper(this));
		this.putSubHelper(ForgeRegistries.SOUND_EVENTS, new SoundSubRegistryHelper(this));
		this.putSubHelper(ForgeRegistries.TILE_ENTITIES, new TileEntitySubRegistryHelper(this));
		this.putSubHelper(ForgeRegistries.ENTITIES, new EntitySubRegistryHelper(this));
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	public <T extends IForgeRegistryEntry<T>, S extends ISubRegistryHelper<T>> S getSubHelper(IForgeRegistry<T> registry) {
		S subHelper = (S) this.subHelpers.get(registry);
		if (subHelper == null) {
			throw new NullPointerException("No Sub Helper is registered for the forge registry of type " + registry.getRegistrySuperType());
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
	public <T extends AbstractSubRegistryHelper<TileEntityType<?>>> T getTileEntitySubHelper() {
		return this.getSubHelper(ForgeRegistries.TILE_ENTITIES);
	}

	@Nonnull
	public <T extends AbstractSubRegistryHelper<EntityType<?>>> T getEntitySubHelper() {
		return this.getSubHelper(ForgeRegistries.ENTITIES);
	}

	/**
	 * Registers all the mapped {@link ISubRegistryHelper}s.
	 * @param eventBus - The {@link IEventBus} to register the {@link ISubRegistryHelper}s to.
	 */
	public void register(IEventBus eventBus) {
		this.subHelpers.values().forEach(helper -> helper.register(eventBus));
	}

	public static class Builder {
		private final RegistryHelper registryHelper;

		public Builder(String modId) {
			this.registryHelper = new RegistryHelper(modId);
			this.registryHelper.putDefaultSubHelpers();
		}

		/**
		 * Puts a {@link ISubRegistryHelper} for a {@link IForgeRegistry}.
		 * @param registry - The {@link IForgeRegistry} to map the key to.
		 * @param subHelper - The {@link ISubRegistryHelper} to be mapped.
		 */
		public <T extends IForgeRegistryEntry<T>, S extends ISubRegistryHelper<T>> Builder putSubHelper(IForgeRegistry<T> registry, S subHelper) {
			this.registryHelper.putSubHelper(registry, subHelper);
			return this;
		}

		/**
		 * @return The built {@link RegistryHelper}.
		 */
		public RegistryHelper build() {
			return this.registryHelper;
		}
	}
}