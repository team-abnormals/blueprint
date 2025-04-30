package com.teamabnormals.blueprint.core.data.server;

import com.teamabnormals.blueprint.core.other.BlueprintDataMaps;
import com.teamabnormals.blueprint.core.other.BlueprintDataMaps.ModdedBiomeSliceSizeEntry;
import com.teamabnormals.blueprint.core.other.tags.BlueprintItemTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.dimension.LevelStem;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class BlueprintDataMapProvider extends DataMapProvider {

	public BlueprintDataMapProvider(PackOutput output, CompletableFuture<Provider> provider) {
		super(output, provider);
	}

	@Override
	protected void gather(Provider provider) {
		this.builder(NeoForgeDataMaps.FURNACE_FUELS)
				.add(BlueprintItemTags.WOODEN_CHESTS, new FurnaceFuel(300), false)
				.add(BlueprintItemTags.WOODEN_TRAPPED_CHESTS, new FurnaceFuel(300), false)
				.add(BlueprintItemTags.WOODEN_LADDERS, new FurnaceFuel(300), false)
				.add(BlueprintItemTags.WOODEN_BOOKSHELVES, new FurnaceFuel(300), false)
				.add(BlueprintItemTags.WOODEN_CHISELED_BOOKSHELVES, new FurnaceFuel(300), false)
				.add(BlueprintItemTags.WOODEN_BOARDS, new FurnaceFuel(300), false)
				.add(BlueprintItemTags.LARGE_BOATS, new FurnaceFuel(2400), false)
				.remove(ItemTags.NON_FLAMMABLE_WOOD);

		this.builder(BlueprintDataMaps.MODDED_BIOME_SLICE_SIZES)
				.add(LevelStem.NETHER, new ModdedBiomeSliceSizeEntry(8), false);
	}
}