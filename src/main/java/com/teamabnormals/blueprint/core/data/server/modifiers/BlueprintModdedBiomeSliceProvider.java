package com.teamabnormals.blueprint.core.data.server.modifiers;

import com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSliceProvider;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.dimension.LevelStem;

/**
 * A {@link ModdedBiomeSliceProvider} subclass that generates Blueprint's built-in modded biome slices.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintModdedBiomeSliceProvider extends ModdedBiomeSliceProvider {

	public BlueprintModdedBiomeSliceProvider(DataGenerator dataGenerator) {
		super(dataGenerator, Blueprint.MOD_ID);
	}

	@Override
	protected void registerSlices() {
		this.registerSlice("originals", 10, new BiomeUtil.OriginalModdedBiomeProvider(), LevelStem.OVERWORLD.location(), LevelStem.NETHER.location(), LevelStem.END.location());
	}

}
