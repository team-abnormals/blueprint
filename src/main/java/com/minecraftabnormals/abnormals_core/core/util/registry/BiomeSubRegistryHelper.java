package com.minecraftabnormals.abnormals_core.core.util.registry;

import com.minecraftabnormals.abnormals_core.common.world.modification.BiomeModificationManager;
import net.minecraft.util.LazyValue;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * A {@link AbstractSubRegistryHelper} extension for biomes. This contains some useful registering methods for biomes.
 *
 * @author SmellyModder (Luke Tonon)
 * @see AbstractSubRegistryHelper
 * @see KeyedBiome
 */
public class BiomeSubRegistryHelper extends AbstractSubRegistryHelper<Biome> {

	public BiomeSubRegistryHelper(RegistryHelper parent, DeferredRegister<Biome> deferredRegister) {
		super(parent, deferredRegister);
	}

	public BiomeSubRegistryHelper(RegistryHelper parent) {
		super(parent, DeferredRegister.create(ForgeRegistries.BIOMES, parent.modId));
	}

	/**
	 * Registers a {@link Biome} and wraps it around a {@link KeyedBiome}.
	 *
	 * @param name  The name for the {@link Biome}.
	 * @param biome A {@link Biome} to register.
	 * @return A {@link KeyedBiome} wrapped around the registered {@link Biome}.
	 * @see KeyedBiome
	 */
	public KeyedBiome createBiome(String name, Supplier<Biome> biome) {
		return new KeyedBiome(this.deferredRegister.register(name, biome));
	}

	/**
	 * Registers a {@link Biome} and wraps it around a {@link KeyedBiome} and accepts a consumer onto {@link BiomeModificationManager#INSTANCE}.
	 *
	 * @param name     The name for the {@link Biome}.
	 * @param biome    A {@link Biome} to register.
	 * @param consumer A {@link BiConsumer} to accept for adding modifiers to the created biome.
	 * @return A {@link KeyedBiome} wrapped around the registered {@link Biome}.
	 * @see KeyedBiome
	 */
	public KeyedBiome createBiomeWithModifiers(String name, Supplier<Biome> biome, BiConsumer<RegistryObject<Biome>, BiomeModificationManager> consumer) {
		RegistryObject<Biome> biomeRegistryObject = this.deferredRegister.register(name, biome);
		consumer.accept(biomeRegistryObject, BiomeModificationManager.INSTANCE);
		return new KeyedBiome(biomeRegistryObject);
	}

	/**
	 * A wrapper around a {@link Biome} {@link RegistryObject} for storing a biome's {@link RegistryKey}.
	 * <p>This allows for a biome's {@link RegistryKey} to be cached.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class KeyedBiome {
		private static final ForgeRegistry<Biome> BIOME_REGISTRY = (ForgeRegistry<Biome>) ForgeRegistries.BIOMES;
		private final RegistryObject<Biome> biome;
		private final LazyValue<RegistryKey<Biome>> lazyKey;

		public KeyedBiome(RegistryObject<Biome> biome) {
			this.biome = biome;
			this.lazyKey = new LazyValue<>(() -> BIOME_REGISTRY.getKey(BIOME_REGISTRY.getID(this.biome.get())));
		}

		/**
		 * Performs the same function as {@link RegistryObject#get()}.
		 * <p>This value will automatically be updated when the backing biome registry is updated.</p>
		 *
		 * @return The {@link Biome} stored inside the {@link #biome} {@link RegistryObject}.
		 * @see RegistryObject#get()
		 */
		public Biome get() {
			return this.biome.get();
		}

		/**
		 * Gets the {@link #biome} {@link RegistryObject}.
		 *
		 * @return The {@link #biome} {@link RegistryObject}.
		 */
		public RegistryObject<Biome> getObject() {
			return this.biome;
		}

		/**
		 * Gets the {@link RegistryKey} of the biome stored in the {@link #biome} {@link RegistryObject}.
		 * <p>Only call this if {@link RegistryObject#isPresent()} returns true.</p>
		 *
		 * @return The {@link RegistryKey} of the biome stored in the {@link #biome} {@link RegistryObject}.
		 * @see RegistryObject#get()
		 */
		public RegistryKey<Biome> getKey() {
			return this.lazyKey.getValue();
		}
	}

}
