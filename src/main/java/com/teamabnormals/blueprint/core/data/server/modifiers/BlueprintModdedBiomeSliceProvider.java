package com.teamabnormals.blueprint.core.data.server.modifiers;

import com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSliceProvider;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.concurrent.CompletableFuture;

/**
 * A {@link ModdedBiomeSliceProvider} subclass that generates Blueprint's built-in modded biome slices.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintModdedBiomeSliceProvider extends ModdedBiomeSliceProvider {

	public BlueprintModdedBiomeSliceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(Blueprint.MOD_ID, output, lookupProvider);
	}

	@Override
	protected void registerSlices(HolderLookup.Provider provider) {
		this.registerSlice("originals", 10, new BiomeUtil.OriginalModdedBiomeProvider(), LevelStem.OVERWORLD.location(), LevelStem.NETHER.location(), LevelStem.END.location());
	}

}
