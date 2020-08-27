package com.teamabnormals.abnormals_core.core.registry;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.TableLootEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author SmellyModder(Luke Tonon)
 */
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID)
public class LootInjectionRegistry {
	private static final Map<Set<ResourceLocation>, LootPool> POOLS = Maps.newHashMap();
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onInjectLoot(LootTableLoadEvent event) {
		POOLS.keySet().forEach(locations -> {
			if (locations.contains(event.getName())) {
				event.getTable().addPool(POOLS.get(locations));
			}
		});
	}
	
	public static class LootInjector {
		private final String modId;
		
		public LootInjector(String modId) {
			this.modId = modId;
		}
		
		public synchronized void registerLootInjection(LootPool pool, ResourceLocation... locations) {
			POOLS.put(Sets.newHashSet(locations), pool);
		}
		
		public LootPool buildLootBool(String name, int weight, int quality) {
			return LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation(this.modId, "injections/" + name)).weight(weight).quality(quality)).name(name).build();
		}
	}
}