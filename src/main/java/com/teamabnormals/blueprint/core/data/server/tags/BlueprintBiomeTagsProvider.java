package com.teamabnormals.blueprint.core.data.server.tags;

import com.teamabnormals.blueprint.core.other.tags.BlueprintBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BlueprintBiomeTagsProvider extends BiomeTagsProvider {

	public BlueprintBiomeTagsProvider(String modid, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
		super(output, lookupProvider, modid, fileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(BlueprintBiomeTags.IS_GRASSLAND).add(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.MEADOW);
		this.tag(BlueprintBiomeTags.IS_ICY).add(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES);
		this.tag(BlueprintBiomeTags.IS_DESERT).add(Biomes.DESERT);
		this.tag(BlueprintBiomeTags.IS_OUTER_END).add(Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS);

		this.tag(BlueprintBiomeTags.WITHOUT_DEFAULT_MONSTER_SPAWNS).add(Biomes.MUSHROOM_FIELDS, Biomes.DEEP_DARK);
		TagAppender<Biome> withMonsterSpawns = this.tag(BlueprintBiomeTags.WITH_DEFAULT_MONSTER_SPAWNS);
		MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD.usedBiomes().forEach((biome) -> {
			if (biome != Biomes.MUSHROOM_FIELDS && biome != Biomes.DEEP_DARK)
				withMonsterSpawns.add(biome);
		});
	}

}