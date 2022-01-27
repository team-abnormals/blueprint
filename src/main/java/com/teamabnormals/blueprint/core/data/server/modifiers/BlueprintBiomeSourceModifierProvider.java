package com.teamabnormals.blueprint.core.data.server.modifiers;

import com.teamabnormals.blueprint.common.world.modification.BiomeSourceModifierProvider;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.dimension.LevelStem;

/**
 * A {@link BiomeSourceModifierProvider} subclass that generates Blueprint's built-in biome source modifiers.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintBiomeSourceModifierProvider extends BiomeSourceModifierProvider {

	public BlueprintBiomeSourceModifierProvider(DataGenerator dataGenerator) {
		super(dataGenerator, Blueprint.MOD_ID);
	}

	@Override
	protected void registerModifiers() {
		this.registerModifier("originals", new BiomeUtil.OriginalModdedBiomeProvider(10), LevelStem.OVERWORLD, LevelStem.NETHER);
	}

}
