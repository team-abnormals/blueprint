package com.minecraftabnormals.abnormals_core.core.registry;

import com.google.common.collect.Sets;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

/**
 * A simple class for adding loot injectors.
 *
 * @author SmellyModder (Luke Tonon)
 * @see LootPool
 */
//TODO: Possible remove this?
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID)
public final class LootInjectionRegistry {
	private static final Set<Pair<Set<ResourceLocation>, LootPool>> POOL_INJECTORS = Sets.newHashSet();

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLootTableLoad(LootTableLoadEvent event) {
		POOL_INJECTORS.forEach(setLootPoolPair -> {
			if (setLootPoolPair.getFirst().contains(event.getName())) {
				event.getTable().addPool(setLootPoolPair.getSecond());
			}
		});
	}

	/**
	 * Adds a {@link LootPool} to be injected into a {@link Set} of loot locations.
	 * <p>This method is synchronized, making it is safe to call during mod loading.</p>
	 *
	 * @param locations The {@link Set} of loot locations. (e.g. A set containing {@link net.minecraft.world.level.storage.loot.BuiltInLootTables#IGLOO_CHEST} and {@link net.minecraft.world.level.storage.loot.BuiltInLootTables#JUNGLE_TEMPLE})
	 * @param lootPool  The {@link LootPool} to inject into the {@link Set} of loot locations.
	 */
	public static synchronized void addLootInjector(Set<ResourceLocation> locations, LootPool lootPool) {
		POOL_INJECTORS.add(Pair.of(locations, lootPool));
	}

	/**
	 * A class for making the addition of loot injectors easier.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static class LootInjector {
		private final String modId;

		/**
		 * Creates a new {@link LootInjector} instance for a mod id.
		 *
		 * @param modId The mod id for this {@link LootInjector} (e.g. "endergetic")
		 */
		public LootInjector(String modId) {
			this.modId = modId;
		}

		/**
		 * Adds a loot injector for an array of locations.
		 * <p>This method internally has the same effect as {@link #addLootInjection(LootPool, ResourceLocation...)}, with the difference being it builds the {@link LootPool}.</p>
		 *
		 * @param name      The name of the {@link LootPool} to construct.
		 * @param weight    The weight for the {@link LootPool} to construct.
		 * @param quality   The quality for the {@link LootPool} to construct.
		 * @param locations The array of locations to inject this {@link LootPool} into.
		 * @see #addLootInjection(LootPool, ResourceLocation...)
		 * @see #addLootInjector(Set, LootPool)
		 */
		public void addLootInjection(String name, int weight, int quality, ResourceLocation... locations) {
			LootInjectionRegistry.addLootInjector(Sets.newHashSet(locations), this.buildLootPool(name, weight, quality));
		}

		/**
		 * Adds a loot injector for an array of locations.
		 *
		 * @param pool      The {@link LootPool} to inject into the locations.
		 * @param locations The array of locations to inject this {@link LootPool} into.
		 * @see #addLootInjector(Set, LootPool)
		 */
		public void addLootInjection(LootPool pool, ResourceLocation... locations) {
			LootInjectionRegistry.addLootInjector(Sets.newHashSet(locations), pool);
		}

		/**
		 * Builds a simple {@link LootPool} with a name, weight, and quality.
		 *
		 * @param name    The name of the {@link LootPool}, this is prefixed by the mod id.
		 * @param weight  The weight of the {@link LootPool}.
		 * @param quality The quality of the {@link LootPool}.
		 * @return The built {@link LootPool} with a name, weight, and quality.
		 * @see LootPool
		 */
		public LootPool buildLootPool(String name, int weight, int quality) {
			return LootPool.lootPool().add(LootTableReference.lootTableReference(new ResourceLocation(this.modId, "injections/" + name)).setWeight(weight).setQuality(quality)).name(this.modId + name).build();
		}
	}
}