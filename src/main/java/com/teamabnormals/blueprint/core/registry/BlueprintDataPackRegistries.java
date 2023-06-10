package com.teamabnormals.blueprint.core.registry;

import com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSlice;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureRepaletterEntry;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DataPackRegistryEvent;

/**
 * Holds the resource keys for Blueprint's datapack registries.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintDataPackRegistries {
	public static final ResourceKey<Registry<StructureRepaletterEntry>> STRUCTURE_REPALETTERS = key("structure_repaletters");
	public static final ResourceKey<Registry<ModdedBiomeSlice>> MODDED_BIOME_SLICES = key("modded_biome_slices");

	/**
	 * Registers Blueprint's datapack registries.
	 *
	 * @param event A {@link DataPackRegistryEvent.NewRegistry} event instance to register to.
	 */
	public static void registerRegistries(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(STRUCTURE_REPALETTERS, StructureRepaletterEntry.CODEC);
		event.dataPackRegistry(MODDED_BIOME_SLICES, ModdedBiomeSlice.CODEC);
	}

	private static <T> ResourceKey<Registry<T>> key(String name) {
		return ResourceKey.createRegistryKey(new ResourceLocation(Blueprint.MOD_ID, name));
	}
}
