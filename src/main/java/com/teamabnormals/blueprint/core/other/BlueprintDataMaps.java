package com.teamabnormals.blueprint.core.other;

import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.LevelStem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = Blueprint.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class BlueprintDataMaps {
	public static final DataMapType<LevelStem, ModdedBiomeSliceSizeEntry> MODDED_BIOME_SLICE_SIZES = DataMapType.builder(Blueprint.location("modded_biome_slice_sizes"), Registries.LEVEL_STEM, ModdedBiomeSliceSizeEntry.CODEC).build();

	@SubscribeEvent
	public static void registerDataMaps(RegisterDataMapTypesEvent event) {
		event.register(MODDED_BIOME_SLICE_SIZES);
	}

	public record ModdedBiomeSliceSizeEntry(int size) {
		public static final Codec<ModdedBiomeSliceSizeEntry> CODEC = ExtraCodecs.POSITIVE_INT.xmap(ModdedBiomeSliceSizeEntry::new, ModdedBiomeSliceSizeEntry::size);
	}
}
